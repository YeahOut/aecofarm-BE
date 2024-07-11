package dgu.aecofarm.domain.alarm.service;

import dgu.aecofarm.dto.alarm.AlarmResponseDTO;

public interface AlarmService {
    AlarmResponseDTO getAlarmStatus(String memberId);
}
