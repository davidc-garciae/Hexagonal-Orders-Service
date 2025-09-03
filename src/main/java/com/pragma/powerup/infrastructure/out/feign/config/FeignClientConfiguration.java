package com.pragma.powerup.infrastructure.out.feign.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfiguration {

  @Bean
  public RequestInterceptor jwtRequestInterceptor() {
    return requestTemplate -> {
      // Obtener el token JWT del request actual
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes != null) {
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
          // Propagar el token JWT a la llamada Feign
          requestTemplate.header("Authorization", authHeader);
        }
      }
    };
  }
}
