package cr.ac.ucr.paraiso.ie.c5h060.expresofast.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración explícita de CORS pedida en el Laboratorio 8 para que el
 * cliente (Live Server, http://127.0.0.1:5500) pueda consumir la API REST.
 *
 * Nota: SecurityConfig ya define un bean CorsConfigurationSource más amplio
 * (usado por la cadena de filtros de Spring Security). Esta clase se agrega
 * además, tal como lo pide el enunciado, restringiendo los orígenes
 * permitidos a los del servidor de desarrollo local del front-end.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
