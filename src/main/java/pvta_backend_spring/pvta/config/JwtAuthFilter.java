package pvta_backend_spring.pvta.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.utiles.Utiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Utiles utiles;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Usuario usuario = utiles.leerToken(token);
                UsuarioContext.setUsuario(usuario);
            }
            filterChain.doFilter(request, response);
        } catch (ResponseStatusException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            handleAuthenticationError(response, e);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            handleAuthenticationError(response, new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido", e));
        } finally {
            UsuarioContext.clear();
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, ResponseStatusException ex) {
        try {
            response.setStatus(ex.getStatusCode().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", ex.getStatusCode().value());
            errorResponse.put("error", ex.getReason());
            errorResponse.put("message", ex.getReason() != null ? ex.getReason() : "Token inválido");
            errorResponse.put("timestamp", java.time.Instant.now().toString());

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException ioException) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error writing authentication error response", ioException);
        }
    }
}
