package com.toeic.online.service.impl;

import com.toeic.online.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

    private static final JavaMailSender javaMailSender = null;

    //    public EmailServiceImpl(JavaMailSender javaMailSender) {
    //        this.javaMailSender = javaMailSender;
    //    }

    //    @Override
    public static void sendMail(String to, String subject, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        javaMailSender.send(msg);
    }

    public static void main(String[] args) {
        sendMail("nguyenleanh3101@gmail.com", "Test mail", "Demo send mail");
    }
}
