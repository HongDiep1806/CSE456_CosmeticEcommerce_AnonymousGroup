package com.example.demo.service;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import org.springframework.beans.factory.annotation.Value;

@Service
public class SendEmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    
    @Value("${spring.mail.username}")
    private String fromEmailId;
    
    public void sendEmail(String recipient, String body, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmailId);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
        
    }
    public String generateResetCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6-digit reset code
    }
}