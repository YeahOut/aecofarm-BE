package dgu.aecofarm.domain.alarm.controller;

import dgu.aecofarm.domain.alarm.service.AlarmService;
import dgu.aecofarm.domain.member.service.MemberService;
import dgu.aecofarm.dto.alarm.AlarmResponseDTO;
import dgu.aecofarm.entity.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {

    private final AlarmService alarmService;
    private final MemberService memberService;

    @GetMapping("/list")
    public Response<?> getAlarmStatus(Authentication auth) {
        try {
            return Response.success(alarmService.getAlarmStatus(auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }
}
