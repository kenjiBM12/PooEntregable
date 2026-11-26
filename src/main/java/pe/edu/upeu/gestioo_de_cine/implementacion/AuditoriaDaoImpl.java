package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IAuditoriaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Auditoria;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 9: DAO de la bitacora de auditoria.
 *
 * Solo dos operaciones:
 *   - registrar: guarda un movimiento (quien, que hizo y cuando)
 *   - listar: devuelve los ultimos movimientos, del mas nuevo al mas viejo
 *
 * La conexion es la compartida del sistema: aqui NO se cierra, solo se
 * cierran el PreparedStatement y el ResultSet (try-with-resources).
 */
public class AuditoriaDaoImpl implements IAuditoriaDao {

    @Override
    public void registrar(int idUsuario, String usuario, String accion, String detalle) {
        String query = "INSERT INTO auditoria(fecha,id_usuario,usuario,accion,detalle) "
                + "VALUES(?,?,?,?,?)";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            //se guarda la fecha con hora y sin nanosegundos para que se lea facil
            st.setString(1, LocalDateTime.now().withNano(0).toString());
            st.setInt(2, idUsuario);
            st.setString(3, usuario != null ? usuario : "sistema");
            st.setString(4, accion != null ? accion : "MOVIMIENTO");
            st.setString(5, detalle != null ? detalle : "");
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar la auditoria: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Auditoria> listar(int limite) {
        List<Auditoria> lista = new ArrayList<>();
        //el limite tambien va con "?" para no concatenar valores en el SQL
        String query = "SELECT * FROM auditoria ORDER BY id DESC LIMIT ?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, limite);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Auditoria a = new Auditoria();
                    a.setId(rs.getInt("id"));
                    a.setFecha(rs.getString("fecha"));
                    a.setIdUsuario(rs.getInt("id_usuario"));
                    a.setUsuario(rs.getString("usuario"));
                    a.setAccion(rs.getString("accion"));
                    a.setDetalle(rs.getString("detalle"));
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar la auditoria: " + e.getMessage(), e);
        }
        return lista;
    }
}
