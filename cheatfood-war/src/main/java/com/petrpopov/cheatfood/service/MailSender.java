package com.petrpopov.cheatfood.service;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;

/**
 * User: petrpopov
 * Date: 12.08.13
 * Time: 21:58
 */
public class MailSender implements Runnable {

    private JavaMailSenderImpl mailSender;
    private MimeMessage message;

    public MailSender(JavaMailSenderImpl mailSender, MimeMessage message) {
        this.mailSender = mailSender;
        this.message = message;
    }

    @Override
    public void run() {
        this.mailSender.send(message);
    }
}
