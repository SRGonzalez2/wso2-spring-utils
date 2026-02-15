package com.microservicios.wso2_spring_utils.config;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.microservicios.wso2_spring_utils.jwt.resolvers.JwtClaimArgumentResolver;


/**
 * Auto-configuracion para WSO2 Spring utils.
 * Habilita automaticamente todas las utilidades cuando se detecta Spring Boot Web.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnClass(HandlerMethodArgumentResolver.class)
public class Wso2SpringUtilsAutoConfiguration implements WebMvcConfigurer {
	
	/**
	 * Crea el resolver de JWT claims si no existe uno personalizado.
	 */
	@Bean
	@ConditionalOnMissingBean
	public JwtClaimArgumentResolver jwtClaimArgumentResolver() {
		return new JwtClaimArgumentResolver();
	}
	
	/**
	 * Registra el resolver automaticamente en Spring MVC.
	 */
	@Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(jwtClaimArgumentResolver());
    }	
}
