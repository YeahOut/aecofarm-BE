package dgu.aecofarm.config.auth;

import dgu.aecofarm.domain.member.service.MemberService;
import dgu.aecofarm.entity.Member;
import dgu.aecofarm.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// OncePerRequestFilter : 매번 들어갈 때 마다 체크 해주는 필터
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음 => 로그인 하지 않음
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Header의 Authorization 값이 'Bearer '로 시작하지 않으면 => 잘못된 토큰
        if (!authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
        String token = authorizationHeader.split(" ")[1];

        // 전송받은 Jwt Token이 만료되었으면 => 다음 필터 진행(인증 X)
        if (JwtTokenUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Jwt Token에서 loginId 추출
        String memberId = JwtTokenUtil.getLoginId(token);

        // 추출한 loginId로 User 찾아오기
        Optional<Member> loginUser = memberService.getLoginUserInfoByMemberId(memberId);

        if (loginUser.isPresent()) {
            Member member = loginUser.get();

            // 기본 권한 부여
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    member.getMemberId(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 권한 부여
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
