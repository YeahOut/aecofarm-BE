package dgu.aecofarm.domain.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.domain.email.service.EmailService;
import dgu.aecofarm.dto.member.*;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.ContractRepository;
import dgu.aecofarm.repository.ItemRepository;
import dgu.aecofarm.repository.LoveRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;
    private final ContractRepository contractRepository;
    private final LoveRepository loveRepository;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public SignupResponseDTO initiateSignup(SignupRequestDTO signupRequestDTO, String imageUrl) {
        Optional<Member> checkDuplicate = memberRepository.findMemberByEmail(signupRequestDTO.getEmail());
        if (checkDuplicate.isPresent()) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        String authCode = generateAuthCode();
        emailService.sendAuthCode(signupRequestDTO.getEmail(), authCode);

        return SignupResponseDTO.builder()
                .signupRequestDTO(signupRequestDTO)
                .expectedCode(authCode)
                .build();
    }

    @Override
    public String completeSignup(SignupRequestDTO signupRequestDTO, String authCode, String expectedCode, String imageUrl) {
        if (!authCode.equals(expectedCode)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        Member member = Member.builder()
                .email(signupRequestDTO.getEmail())
                .userName(signupRequestDTO.getUserName())
                .password(toSHA256(signupRequestDTO.getPassword()))
                .phone(signupRequestDTO.getPhone())
                .schoolNum(signupRequestDTO.getSchoolNum())
                .image(imageUrl)
                .point(3000)
                .build();
        memberRepository.save(member);

        // 회원가입 시 기본 포인트 3000

        return "회원가입 성공";
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        } else {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());  // Content-Type 설정
            metadata.setContentDisposition("inline");  // Content-Disposition 설정

            try {
                amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
                amazonS3.setObjectAcl(bucketName, fileName, CannedAccessControlList.PublicRead);  // Set ACL to public-read
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }

            return amazonS3.getUrl(bucketName, fileName).toString();
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(loginRequestDTO.getEmail());
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("유효한 이메일이 아닙니다.");
        }

        Member member = optionalMember.get();
        String encode_password = toSHA256(loginRequestDTO.getPassword());

        if (encode_password.equals(member.getPassword())) {
            LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                    .memberId(member.getMemberId())
                    .userName(member.getUserName())
                    .build();
            return loginResponseDTO;
        }
        throw new IllegalArgumentException("비밀번호가 틀립니다.");
    }
    // Jwt Token에서 추출한 loginId로 Member 찾아오기
    @Override
    public Optional<Member> getLoginUserInfoByMemberId(String memberId) {
        return memberRepository.findByMemberId(Long.valueOf(memberId));
    }
    // 로그인한 Member 조회
    @Override
    public Optional<JwtInfoResponseDTO> getLoginUserInfoByUserid(String memberId) {
        return memberRepository.findByMemberId(Long.valueOf(memberId)).map(member ->
                JwtInfoResponseDTO.builder()
                        .memberId(member.getMemberId())
                        .userName(member.getUserName())
                        .build());
    }

    @Override
    public String findPassword(FindPasswordRequestDTO findPasswordDTO) {
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(findPasswordDTO.getEmail());
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("유효한 이메일이 아닙니다.");
        }// 사용자 이름과 학번이 일치하는지 확인
        Member member = optionalMember.get();

        // 사용자 이름과 학번이 일치하는지 확인
        if (!member.getUserName().equals(findPasswordDTO.getUserName()) || member.getSchoolNum() != findPasswordDTO.getSchoolNum()) {
            throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");
        }
        // 비밀번호 재설정
        String newPassword = toSHA256(findPasswordDTO.getPassword());
        member.updatePassword(newPassword);
        memberRepository.save(member);

        return "비밀번호 재설정에 성공하였습니다.";
    }

    @Override
    public String signout(String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new IllegalArgumentException("유효한 사용자 ID가 아닙니다."));

        List<Love> loves = loveRepository.findByMember(member);
        loveRepository.deleteAll(loves);

        memberRepository.delete(member);

        return "회원 탈퇴에 성공하였습니다.";
    }

    @Override
    public RecommendResponseDTO getRecommand(String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        List<String> recommendedKeywords;
        try {
            List<Object> rawRecentList = member.getRecent() == null ? List.of() : objectMapper.readValue(member.getRecent(), List.class);
            List<Long> recentContractIds = rawRecentList.stream()
                    .map(id -> {
                        if (id instanceof Integer) {
                            return ((Integer) id).longValue();
                        } else if (id instanceof Long) {
                            return (Long) id;
                        } else {
                            throw new IllegalArgumentException("유효하지 않은 ID 타입입니다.");
                        }
                    })
                    .collect(Collectors.toList());

            // 최근 본 물품을 역순으로 정렬
            Collections.reverse(recentContractIds);

            recommendedKeywords = recentContractIds.stream()
                    .map(contractId -> contractRepository.findById(contractId)
                            .filter(contract -> contract.getStatus() == Status.NONE)
                            .map(Contract::getItem)
                            .map(Item::getItemName)
                            .orElse(null))
                    .filter(itemName -> itemName != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("최근 본 물품을 리스트로 변환하는데 실패했습니다.", e);
        }

        List<String> hotSearchRankings = itemRepository.findAllByOrderByClickDesc().stream()
                .filter(item -> contractRepository.findByItem(item)
                        .stream()
                        .anyMatch(contract -> contract.getStatus() == Status.NONE))
                .map(Item::getItemName)
                .limit(8) // HOT 순위는 최대 8개까지 가져옴
                .collect(Collectors.toList());

        return RecommendResponseDTO.builder()
                .recommendedKeywords(recommendedKeywords)
                .hotSearchRankings(hotSearchRankings)
                .build();
    }

    @Override
    public SearchResponseDTO searchItems(SearchRequestDTO searchRequestDTO, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        String keyword = searchRequestDTO.getKeyword();

        List<Contract> lendContracts = contractRepository.findByCategoryAndStatusAndItemItemNameContaining(Category.LEND, Status.NONE, keyword);
        List<Contract> borrowContracts = contractRepository.findByCategoryAndStatusAndItemItemNameContaining(Category.BORROW, Status.NONE, keyword);

        List<SearchResponseDTO.SearchItemDTO> lendItems = lendContracts.stream().map(contract -> {
            Item item = contract.getItem();
            List<String> itemHashList;
            try {
                itemHashList = objectMapper.readValue(item.getItemHash(), List.class);
            } catch (IOException e) {
                throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
            }
            boolean likeStatus = loveRepository.existsByItemAndMember(item, member);
            boolean donateStatus = item.getPrice() == 0;

            return SearchResponseDTO.SearchItemDTO.builder()
                    .contractId(contract.getContractId())
                    .itemName(item.getItemName())
                    .itemImage(item.getItemImage())
                    .price(item.getPrice())
                    .itemPlace(item.getItemPlace())
                    .time(item.getTime())
                    .contractTime(item.getContractTime())
                    .itemHash(itemHashList)
                    .likeStatus(likeStatus)
                    .donateStatus(donateStatus)
                    .build();
        }).collect(Collectors.toList());

        List<SearchResponseDTO.SearchItemDTO> borrowItems = borrowContracts.stream().map(contract -> {
            Item item = contract.getItem();
            List<String> itemHashList;
            try {
                itemHashList = objectMapper.readValue(item.getItemHash(), List.class);
            } catch (IOException e) {
                throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
            }
            boolean likeStatus = loveRepository.existsByItemAndMember(item, member);
            boolean donateStatus = item.getPrice() == 0;

            return SearchResponseDTO.SearchItemDTO.builder()
                    .contractId(contract.getContractId())
                    .itemName(item.getItemName())
                    .itemImage(item.getItemImage())
                    .price(item.getPrice())
                    .itemPlace(item.getItemPlace())
                    .time(item.getTime())
                    .contractTime(item.getContractTime())
                    .itemHash(itemHashList)
                    .likeStatus(likeStatus)
                    .donateStatus(donateStatus)
                    .build();
        }).collect(Collectors.toList());

        return SearchResponseDTO.builder()
                .lendItems(lendItems)
                .borrowItems(borrowItems)
                .build();
    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }

    private String toSHA256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
