package com.microservicios.wso2_spring_utils.jwt.resolvers;

import java.util.Base64;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicios.wso2_spring_utils.jwt.annotations.JwtClaim;
import com.microservicios.wso2_spring_utils.jwt.exceptions.MissingClaimException;

import lombok.extern.slf4j.Slf4j;

/**
 * Resolver para extraer claims especificos de un JWT y mapearlos a parametros de metodos.
 */
@Component
@Slf4j
public class JwtClaimArgumentResolver implements HandlerMethodArgumentResolver {

	private static final int BEARER_LENGTH = 7;
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
			
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Verifica si el parametro esta anotado con {@link JwtClaim}
	 * 
	 * @param parameter el parametro del metodo
	 * @return true si tiene la anotacion {@link JwtClaim}
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(JwtClaim.class);
	}
	
	/**
	 * Resuelve el valor del claim desde el JWT en el header Authorization
	 * 
	 * @param parameter el parametro del metodo a resolver
	 * @return el valor del claim extraido del JWT
	 * @throws MissingClaimException si no hay header Authorization o el claim no existe
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) {
		
		String authHeader = webRequest.getHeader(AUTHORIZATION_HEADER);
		
		if(authHeader == null || !authHeader.startsWith(BEARER_PREFIX) || authHeader.trim().isEmpty()) {
			throw new MissingClaimException("Authorization header requerido");
		}
		
		String jwt = authHeader.substring(BEARER_LENGTH);
		JwtClaim annotation = parameter.getParameterAnnotation(JwtClaim.class);
		String claimName = annotation.value();
		boolean required = annotation.required();
		
		JsonNode claims = decodePayload(jwt);
		
		Object claimValue = extractClaimFromNode(claims, claimName, parameter.getParameterType());
		
		if(claimValue == null && required) {
			throw new MissingClaimException("Claim requerido no encontrado: " + claimName);
		}
		
		return claimValue;
	}

	/**
	 * Extrae un claim del JWT ya parseado lo convierte al tipo especificado.
	 * Soporta String, Integer, Long, Boolean y tipos complejos via Jackson
	 * @param claims del JWT parseados
	 * @param claimName nombre del claim a extraer
	 * @param targetType tipo de dato esperado
	 * @return el valor del claim convertido al tipo apropiado o null si no existe
	 */
	private Object extractClaimFromNode(JsonNode claims, String claimName, Class<?> targetType) {
		
		if(claims == null || !claims.has(claimName)) {
			return null;
		}
		
		try {
			
			JsonNode claimNode = claims.get(claimName);
			
            if (targetType == String.class) {
                return claimNode.asText();
            } else if (targetType == Integer.class || targetType == int.class) {
                return claimNode.asInt();
            } else if (targetType == Long.class || targetType == long.class) {
                return claimNode.asLong();
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return claimNode.asBoolean();
            } else {
                return objectMapper.treeToValue(claimNode, targetType);
            }
	
		} catch(Exception e) {
			log.error("Error al extraer el claim {} : {}", claimName, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Decodifica el payload del JWT
	 * @param jwt el token JWT completo
	 * @return {@link JsonNode} del payload del JWT decodificado
	 */
	private JsonNode decodePayload(String jwt) {
		try {
			String[] parts = jwt.split("\\.");
			if(parts.length < 2) return null;
			
			String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
			JsonNode claims = objectMapper.readTree(payload);
			
			return claims;
		} catch (Exception e) {
			log.error("Error al decodificar el JWT: {}", e.getMessage());
			return null;
		}
	}
	
	
}
