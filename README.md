# WSO2 Spring Utils

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-brightgreen?logo=springboot)
![License](https://img.shields.io/badge/License-MIT-blue)
![Version](https://img.shields.io/badge/Version-1.0.0-success)

Librería de utilidades para facilitar la integración de Spring Boot con WSO2 APIM y servicios OAuth.

## 🚀 Características

- ✅ Extracción automática de claims JWT mediante anotaciones
- ✅ Auto-configuración para Spring Boot
- ✅ Soporte para múltiples tipos de datos
- ✅ Claims opcionales y requeridos
- ✅ Sin configuración adicional necesaria

## 📖 Uso

### Extracción de claims JWT
```java
@RestController
@RequestMapping("/api")
public class UserController {
    
    @GetMapping("/perfil")
    public ResponseEntity getPerfil(
        @JwtClaim("email") String email,
        @JwtClaim("sub") String userId,
        @JwtClaim("name") String name
    ) {
        return ResponseEntity.ok(Map.of(
            "email", email,
            "userId", userId,
            "name", name
        ));
    }
}
```

### Claims opcionales
```java
@GetMapping("/perfil")
public ResponseEntity getPerfil(
    @JwtClaim("email") String email,
    @JwtClaim(value = "premium", required = false) Boolean isPremium
) {
    // isPremium será null si no existe en el JWT
    boolean premium = isPremium != null && isPremium;
    return ResponseEntity.ok(userService.getProfile(email, premium));
}
```

### Tipos soportados

- `String`
- `Integer` / `int`
- `Long` / `long`
- `Boolean` / `boolean`
- Objetos complejos (serializados con Jackson)

## ⚙️ Configuración

La librería se auto-configura automáticamente en aplicaciones Spring Boot Web.
No necesita configuración adicional.

## 🔒 Seguridad

**⚠️ Importante:** Esta librería NO valida la firma del JWT. 

Debe usarse junto con:
- Spring Security OAuth2 Resource Server
- Filtros de seguridad personalizados que validen el token
