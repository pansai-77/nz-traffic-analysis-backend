package io.github.pansai.traffic.service.impl;

import io.github.pansai.traffic.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service("mailService")
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Base URL for activation link
     */
    @Value("${app.mail.activation-base-url}")
    private String activationBaseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendActivationMail(String toEmail, String token) {
        String activationLink = activationBaseUrl + "/api/userInfo/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Traffic WebSite-PS: Click to activate your account");
        message.setText(
                "【Traffic WebSite-PS】" +
                        "Thank you for register!\n\n" +
                        "We have received your application, please click the link below to activate your account: \n\n" +
                        activationLink + "\n\n" +
                        "If you did not register for this account, please ignore this massage.\n\n" +
                        "Please do not respond, it's a test mail"
        );

        mailSender.send(message);
    }
}
