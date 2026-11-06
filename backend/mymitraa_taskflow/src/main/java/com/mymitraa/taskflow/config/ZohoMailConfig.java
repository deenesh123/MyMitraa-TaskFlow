package com.mymitraa.taskflow.config;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Zoho Mail SMTP Spring Configuration.
 * Configures JavaMailSender with protected environment variables.
 * Supports port 465 (SMTPS / SSL) and port 587 (STARTTLS).
 */
@Configuration
public class ZohoMailConfig {

    private static final Logger log = LoggerFactory.getLogger(ZohoMailConfig.class);

    @Value("${spring.mail.host:smtp.zoho.com}")
    private String host;

    @Value("${spring.mail.port:465}")
    private int port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable:true}")
    private boolean sslEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}")
    private boolean starttlsEnable;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        if (username != null && !username.trim().isEmpty()) {
            mailSender.setUsername(username.trim());
        }
        if (password != null && !password.trim().isEmpty()) {
            mailSender.setPassword(password.trim());
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        if (port == 465 || sslEnable) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", String.valueOf(port));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        } else if (port == 587 || starttlsEnable) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        log.info("Initialized JavaMailSender for Zoho Mail host: {}, port: {}, ssl: {}", host, port, sslEnable);
        return mailSender;
    }
}
