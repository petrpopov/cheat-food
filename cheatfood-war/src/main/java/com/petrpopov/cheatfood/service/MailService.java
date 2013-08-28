package com.petrpopov.cheatfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * User: petrpopov
 * Date: 12.08.13
 * Time: 19:41
 */

@Component
public class MailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Value("#{properties.mail_account}")
    private String mailAccount;

    public void sendForgetPasswordMail(String email, String url, String globalUrl) throws MessagingException {

        String content = getForgetPasswordContent(url, globalUrl);
        sendEmail(email, getForgetPasswordSubject(), content);
    }

    public void sendChangeEmailMail(String email, String url, String globalUrl) throws MessagingException {

        String content = getChangeEmailContent(url, globalUrl);
        sendEmail(email, getChangeEmailSubject(), content);
    }

    private void sendEmail(String email, String subject, String content) throws MessagingException {

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        message.setSubject(subject);
        message.setFrom(mailAccount);
        message.setTo(email);
        message.setText(content, true);


        // Send mail
        MailSender sender = new MailSender(mailSender, mimeMessage);
        Thread thread = new Thread(sender);
        thread.start();
    }

    private String getForgetPasswordSubject() {
        return "Password restore request";

    }

    private String getForgetPasswordContent(String url, String globalUrl) {

        String address = globalUrl + "/api/users/forget/" + url;

        String res = "<html><body></body><p>Привет!</p>"
                + "<p>Кто-то (скорее всего это были вы) запрашивал восстановление пароля на сервисе "
                + "<a href=\"" + globalUrl + "\">Cheat Food</a>"
                + "</p>"
                + "<p>Если это были вы, пройдите, пожалуйста, по ссылке:</p>"
                + "<p><a href=\"" + address + "\">" + address + "</a></p>"
                + "<p>Если это были не вы - удалите же скорее это письмо!</p>"
                + "<p>Пока-пока!</p>"
                + "</html>";

        return res;
    }

    private String getChangeEmailSubject() {
        return "Change email";
    }

    private String getChangeEmailContent(String url, String globalUrl) {

        String address = globalUrl + "/api/users/changeemail/" + url;

        String res = "<html><body></body><p>Привет!</p>"
                + "<p>Кто-то (скорее всего это были вы) запрашивал смену (или привязку) email на сервисе "
                + "<a href=\"" + globalUrl + "\">Cheat Food</a>"
                + "</p>"
                + "<p>Если это были вы, пройдите, пожалуйста, по ссылке:</p>"
                + "<p><a href=\"" + address + "\">" + address + "</a></p>"
                + "<p>Если это были не вы - удалите же скорее это письмо!</p>"
                + "<p>Пока-пока!</p>"
                + "</html>";

        return res;
    }
}
