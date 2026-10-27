
package pe.edu.upeu.project_final.implementacion;

import pe.edu.upeu.project_final.interfaces.IPeliculaDao;
import pe.edu.upeu.project_final.modelo.Pelicula;
import pe.edu.upeu.project_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDaoImpl implements IPeliculaDao {

    @Override
    public boolean insert(Pelicula p) {
        boolean flag = false;
        // CORREGIDO: INSERT registra los campos recibidos con sus nombres reales.
        String query = "INSERT INTO pelicula(titulo,genero,duracion,clasificacion) VALUES(?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracionMin());
            st.setString(4, p.getClasificacion());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
                // AGREGADO: conserva en el objeto el ID generado por esta conexion.
                try (Statement consulta = cn.createStatement();
                     ResultSet rs = consulta.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar pelicula.", e);
        }
        return flag;
    }

    @Override
    public boolean update(Pelicula p) {
        boolean flag = false;
        // CORREGIDO: usa idpelicula, que es la clave primaria de esta tabla.
        String query = "UPDATE pelicula SET titulo=?,genero=?,duracion=?,clasificacion=? WHERE idpelicula=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracionMin());
            st.setString(4, p.getClasificacion());
            st.setInt(5, p.getId());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar pelicula.", e);
        }
        return flag;
    }

    @Override
    public boolean delete(Pelicula p) {
        boolean flag = false;
        String query = "DELETE FROM pelicula WHERE idpelicula=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getId());
            // CORREGIDO: un ID inexistente no debe indicar que hubo un borrado.
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar pelicula.", e);
        }
        return flag;
    }

    @Override
    public Pelicula searchById(int id) {
        Pelicula dato = null;
        String query = "SELECT * FROM pelicula WHERE idpelicula=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    dato = mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar pelicula.", e);
        }
        return dato;
    }

    @Override
    public List<Pelicula> listAll() {
        List<Pelicula> lista = new ArrayList<>();
        String query = "SELECT * FROM pelicula ORDER BY idpelicula";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            // CORREGIDO: se informa el error real en lugar de mostrar una lista vacia.
            throw new IllegalStateException("Error al listar pelicula.", e);
        }
        return lista;
    }

    // Lectura comun para buscar por ID y listar.
    private Pelicula mapear(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();
        p.setId(rs.getInt("idpelicula"));
        p.setTitulo(rs.getString("titulo"));
        p.setGenero(rs.getString("genero"));
        p.setDuracionMin(rs.getInt("duracion"));
        p.setClasificacion(rs.getString("clasificacion"));
        return p;
    }
}
