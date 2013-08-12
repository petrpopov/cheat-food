package com.petrpopov.cheatfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

/**
 * User: petrpopov
 * Date: 12.08.13
 * Time: 19:41
 */

@Component
public class MailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    public void sendMail(String email, String url, HttpServletRequest request) throws MessagingException {


        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setSubject("Password restore request");
        message.setFrom("info@cheatfood.com");
        message.setTo(email);
        message.setText(getContent(url, request), true);


        // Send mail
        MailSender sender = new MailSender(mailSender, mimeMessage);
        Thread thread = new Thread(sender);
        thread.start();
    }

    private String getGlobalUrl(HttpServletRequest request) {

        return request.getScheme()+"://"
                + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private String getContent(String url, HttpServletRequest request) {

        String address = getGlobalUrl(request) + "/forget/" + url;

        String res = "<html><body></body><p>Привет!</p>"
                + "<p>Кто-то (скорее всего это были вы) запрашивал восстановление пароля на сервисе Cheat Food.</p>"
                + "<p>Если это были вы, пройдите, пожалуйста, по ссылке:</p>"
                + "<p><a href=\"" + address + "\"/>" + address + "</p>"
                + "</html>";

        return res;
    }
}
