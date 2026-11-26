package pe.edu.upeu.gestioo_de_cine.utils;

import pe.edu.upeu.gestioo_de_cine.implementacion.AuditoriaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IAuditoriaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;

/**
 * MEJORA 9: atajo para escribir en la bitacora de auditoria.
 *
 * Las pantallas solo llaman a Bitacora.registrar("VENTA", "detalle...") y
 * aqui se toma el usuario conectado de la clase Sesion. Si la auditoria
 * falla (por ejemplo, una base vieja sin la tabla), el error se imprime
 * y NO se interrumpe la operacion principal: la venta o el login siguen.
 */
public class Bitacora {

    private static final IAuditoriaDao auditoriaDao = new AuditoriaDaoImpl();

    public static void registrar(String accion, String detalle) {
        try {
            Usuario u = Sesion.getUsuario();
            int idUsuario = u != null ? u.getIdusuario() : 0;
            String nombre = u != null ? u.getUsuario() : "sistema";
            auditoriaDao.registrar(idUsuario, nombre, accion, detalle);
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar en la auditoria: " + e.getMessage());
        }
    }
}
