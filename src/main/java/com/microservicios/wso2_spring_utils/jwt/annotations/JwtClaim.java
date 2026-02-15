package com.microservicios.wso2_spring_utils.jwt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotacion para extraer automaticamente un claim de un JWT.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JwtClaim {

	/**
	 * Nombre del claim a extraer del JWT
	 * @return el nombre del claim
	 */
	String value();
	
	/**
	 * Si es requerido. Si es true y no existe el clail, lanza excepcion
	 * @return true siel claim es requerido, false en caso contrario
	 */
	boolean required() default true;
	
}
