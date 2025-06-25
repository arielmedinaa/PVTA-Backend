package pvta_backend_spring.pvta.context;

import pvta_backend_spring.pvta.entities.Usuario;

public class UsuarioContext {

    private static final ThreadLocal<Usuario> currentUser = new ThreadLocal<>();

    public static void setUsuario(Usuario usuario) {
        currentUser.set(usuario);
    }

    public static Usuario getUsuario() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
