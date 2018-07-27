package com.fancydsp.data.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${spring.mail.subject-prefix}")
    private String subjectPrefix;
    @Value("${spring.mail.error.to}")
    private String errorTo;

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getSubjectPrefix() {
        return subjectPrefix;
    }

    public void setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
    }

    public String getErrorTo() {
        return errorTo;
    }

    public void setErrorTo(String errorTo) {
        this.errorTo = errorTo;
    }
}
