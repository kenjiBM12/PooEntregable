
package pe.edu.upeu.project_final.implementacion;

import pe.edu.upeu.project_final.interfaces.IFuncionDao;
import pe.edu.upeu.project_final.modelo.Funcion;
import pe.edu.upeu.project_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {

    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;
        // CORREGIDO: INSERT registra los campos recibidos con sus nombres reales.
        String query = "INSERT INTO funcion(id_pelicula,id_sala,horario,precio) VALUES(?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdPelicula());
            st.setInt(2, f.getIdSala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
                // AGREGADO: conserva en el objeto el ID generado por esta conexion.
                try (Statement consulta = cn.createStatement();
                     ResultSet rs = consulta.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        f.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar funcion.", e);
        }
        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;
        // CORREGIDO: usa idfuncion, que es la clave primaria de esta tabla.
        String query = "UPDATE funcion SET id_pelicula=?,id_sala=?,horario=?,precio=? WHERE idfuncion=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdPelicula());
            st.setInt(2, f.getIdSala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getId());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar funcion.", e);
        }
        return flag;
    }

    @Override
    public boolean delete(Funcion f) {
        boolean flag = false;
        String query = "DELETE FROM funcion WHERE idfuncion=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getId());
            // CORREGIDO: un ID inexistente no debe indicar que hubo un borrado.
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar funcion.", e);
        }
        return flag;
    }

    @Override
    public Funcion searchById(int id) {
        Funcion dato = null;
        String query = "SELECT * FROM funcion WHERE idfuncion=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    dato = mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar funcion.", e);
        }
        return dato;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();
        String query = "SELECT * FROM funcion ORDER BY idfuncion";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            // CORREGIDO: se informa el error real en lugar de mostrar una lista vacia.
            throw new IllegalStateException("Error al listar funcion.", e);
        }
        return lista;
    }

    // Lectura comun para buscar por ID y listar.
    private Funcion mapear(ResultSet rs) throws SQLException {
        Funcion f = new Funcion();
        f.setId(rs.getInt("idfuncion"));
        f.setIdPelicula(rs.getInt("id_pelicula"));
        f.setIdSala(rs.getInt("id_sala"));
        f.setHorario(rs.getString("horario"));
        f.setPrecio(rs.getDouble("precio"));
        return f;
    }
}