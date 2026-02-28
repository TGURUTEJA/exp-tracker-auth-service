package com.Auth_service.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
        .additionalInterceptors((request, body, execution) -> {
            System.out.println(">>> " + request.getMethod() + " " + request.getURI());
            System.out.println(">>> Headers: " + request.getHeaders());
            if (body != null && body.length > 0) {
                System.out.println(">>> Body: " + new String(body));
            } else {
                System.out.println(">>> Body: <empty>");
            }
            return execution.execute(request, body);
        })
        .build();
    }
}
