package pe.edu.upeu.gestioo_de_cine.modelo;

public class UserSession {

    private static Usuario usuarioActual;

    public static void iniciar(Usuario u) {
        usuarioActual = u;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static void cerrar() {
        usuarioActual = null;
    }

    public static boolean esAdmin() {
        return usuarioActual != null && "admin".equalsIgnoreCase(usuarioActual.getRol());
    }
}
