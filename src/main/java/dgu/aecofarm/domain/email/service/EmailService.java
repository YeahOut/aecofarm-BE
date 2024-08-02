package dgu.aecofarm.domain.email.service;

import dgu.aecofarm.entity.EmailMessage;

public interface EmailService {
    void sendAuthCode(String email, String authCode);
}

