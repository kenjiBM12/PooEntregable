
package pe.edu.upeu.project_final.implementacion;

import pe.edu.upeu.project_final.interfaces.ISalaDao;
import pe.edu.upeu.project_final.modelo.Sala;
import pe.edu.upeu.project_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISalaDao {

    @Override
    public boolean insert(Sala s) {
        boolean flag = false;
        // CORREGIDO: INSERT registra los campos recibidos con sus nombres reales.
        String query = "INSERT INTO sala(nombre,tipo,capacidad) VALUES(?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
                // AGREGADO: conserva en el objeto el ID generado por esta conexion.
                try (Statement consulta = cn.createStatement();
                     ResultSet rs = consulta.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        s.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar sala.", e);
        }
        return flag;
    }

    @Override
    public boolean update(Sala s) {
        boolean flag = false;
        // CORREGIDO: usa idsala, que es la clave primaria de esta tabla.
        String query = "UPDATE sala SET nombre=?,tipo=?,capacidad=? WHERE idsala=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            st.setInt(4, s.getId());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar sala.", e);
        }
        return flag;
    }

    @Override
    public boolean delete(Sala s) {
        boolean flag = false;
        String query = "DELETE FROM sala WHERE idsala=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getId());
            // CORREGIDO: un ID inexistente no debe indicar que hubo un borrado.
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar sala.", e);
        }
        return flag;
    }

    @Override
    public Sala searchById(int id) {
        Sala dato = null;
        String query = "SELECT * FROM sala WHERE idsala=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    dato = mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar sala.", e);
        }
        return dato;
    }

    @Override
    public List<Sala> listAll() {
        List<Sala> lista = new ArrayList<>();
        String query = "SELECT * FROM sala ORDER BY idsala";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            // CORREGIDO: se informa el error real en lugar de mostrar una lista vacia.
            throw new IllegalStateException("Error al listar sala.", e);
        }
        return lista;
    }

    // Lectura comun para buscar por ID y listar.
    private Sala mapear(ResultSet rs) throws SQLException {
        Sala s = new Sala();
        s.setId(rs.getInt("idsala"));
        s.setNombre(rs.getString("nombre"));
        s.setTipo(rs.getString("tipo"));
        s.setCapacidad(rs.getInt("capacidad"));
        return s;
    }
}