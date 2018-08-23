package com.fancydsp.data.service.impl;

import com.fancydsp.data.configuration.EmailConfig;
import com.fancydsp.data.domain.Pair;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service("emailService")
public class EmailService {

    @Resource
    private EmailConfig emailConfig;
    @Resource
    private JavaMailSender mailSender;
//    @Resource
//    private VelocityEngine velocityEngine;

    public void sendSimpleMail(String sendTo, String title, String content) {
        if (sendTo == null) {
            sendTo = emailConfig.getErrorTo();
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailConfig.getEmailFrom());
        message.setTo(sendTo.split(";"));
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendAttachmentsMail(String sendTo, String title, String content, List<Pair<String, File>> attachments) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {


            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfig.getEmailFrom());
            helper.setTo(sendTo.split(";"));
            helper.setSubject(emailConfig.getSubjectPrefix() + title);
            helper.setText(content);

            for (Pair<String, File> pair : attachments) {
                helper.addAttachment(pair.getLeft(), new FileSystemResource(pair.getRight()));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        mailSender.send(mimeMessage);
    }

//    todo 使用模板发送邮件
//    public void sendTemplateMail(String sendTo, String titel, Map<String, Object> content, List<Pair<String, File>> attachments) {
//
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            helper.setFrom(emailConfig.getEmailFrom());
//            helper.setTo(sendTo);
//            helper.setSubject(titel);
//
//            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "template.vm", "UTF-8", content);
//            helper.setText(text, true);
//
//            for (Pair<String, File> pair : attachments) {
//                helper.addAttachment(pair.getLeft(), new FileSystemResource(pair.getRight()));
//            }
//        } catch (Exception e) {
//            throw new RuntimeServiceException(e);
//        }
//
//        mailSender.send(mimeMessage);
//    }

}
