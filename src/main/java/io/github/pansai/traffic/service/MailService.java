package io.github.pansai.traffic.service;

public interface MailService {

    /**
     * Send account activation email.
     * @param toEmail receiver email address
     * @param toToken activation token
     */
    void sendActivationMail(String toEmail, String toToken);
}
