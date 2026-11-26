package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Auditoria;

import java.util.List;

/**
 * MEJORA 9: interfaz de la bitacora de auditoria.
 *
 * registrar guarda un movimiento; listar devuelve los ultimos movimientos
 * para mostrarlos en pantalla. Igual que los demas DAO, los errores de la
 * base se lanzan como RuntimeException y la pantalla los avisa.
 */
public interface IAuditoriaDao {

    void registrar(int idUsuario, String usuario, String accion, String detalle);

    List<Auditoria> listar(int limite);
}
