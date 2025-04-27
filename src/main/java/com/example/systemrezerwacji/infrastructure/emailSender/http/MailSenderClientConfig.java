package com.example.systemrezerwacji.infrastructure.emailSender.http;


import com.example.systemrezerwacji.infrastructure.notificationmode.SendMail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class MailSenderClientConfig {


    @Bean
    public RestTemplateResponseErrorHandler restTemplateResponseErrorHandler() {
        return new RestTemplateResponseErrorHandler();
    }

    @Bean
    public RestTemplate restTemplate(@Value("${mail.sender.http.client.config.connectionTimeout:1000}") long connectionTimeout,
                                     @Value("${mail.sender.http.client.config.readTimeout:1000}") long readTimeout,
                                     RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
        return new RestTemplateBuilder()
                .errorHandler(restTemplateResponseErrorHandler)
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Bean
    public SendMail remoteSendMailClient(RestTemplate restTemplate,
                                         @Value("${mail.sender.http.client.config.uri:}") String uri,
                                         @Value("${mail.sender.http.client.config.port:}") int port) {
        return new SendMailHttpClient(restTemplate,uri,port);
    }

}
