package com.example.kulvida.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


@Slf4j
@Service
public class EmailSenderUtil {

    @Autowired
    private JavaMailSender mailSender;


    public void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom("ngankemlaurence@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
        log.info("mail sent successfully");


    }

    public SimpleMailMessage generateMessage(String toEmail, String subject, String body){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom("ngankemlaurence@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        return message;
    }


    public void sendReceipt(String toEmail, String subject, String body, String filepath){

        SimpleMailMessage message=generateMessage(toEmail,subject,body);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(message.getFrom());
            helper.setTo(message.getTo());
            helper.setSubject(message.getSubject());
            helper.setText(String.format(
                    message.getText()));

            FileSystemResource file = new FileSystemResource(filepath);
            helper.addAttachment(file.getFilename(), file);
            mailSender.send(mimeMessage);
            File receiptfile= new File(filepath);
            log.info("deletion of file {}: {}",filepath,receiptfile.delete());

        }catch (MessagingException e) {
            throw new MailParseException(e);
        }


    }
}
