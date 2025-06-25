package pvta_backend_spring.pvta.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pvta_backend_spring.pvta.context.UsuarioContext;
import pvta_backend_spring.pvta.entities.Usuario;
import pvta_backend_spring.pvta.utiles.Utiles;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Utiles utiles;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Usuario usuario = utiles.leerToken(token);
                UsuarioContext.setUsuario(usuario);
            }
            filterChain.doFilter(request, response);
        } finally {
            UsuarioContext.clear();
        }
    }
}
