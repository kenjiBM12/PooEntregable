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
        String query = "INSERT INTO pelicula(titulo,genero,duracion,clasificacion)VALUES(?,?,?,?);";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracionMin());
            st.setString(4, p.getClasificacion());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar pelicula:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Pelicula p) {
        boolean flag = false;
        String query = " UPDATE pelicula SET titulo=?, genero=?, duracion=?, clasificacion=? " +
                " WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracionMin());
            st.setString(4, p.getClasificacion());
            st.setInt(5, p.getId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar pelicula:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Pelicula searchById(int id) {
        Pelicula pe = null;
        String query = "SELECT * FROM pelicula WHERE id=?;";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    pe = new Pelicula();
                    pe.setId(rs.getInt("id"));
                    pe.setTitulo(rs.getString("titulo"));
                    pe.setGenero(rs.getString("genero"));
                    pe.setDuracionMin(rs.getInt("duracion"));
                    pe.setClasificacion(rs.getString("clasificacion"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pelicula por Id:" + e.getMessage());
        }
        return pe;
    }

    @Override
    public boolean delete(Pelicula p) {
        boolean flag = false;
        String query = "DELETE FROM pelicula WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar pelicula:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Pelicula> listAll() {
        List<Pelicula> lista = new ArrayList<>();
        String query = "SELECT * FROM pelicula;";
        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
        ) {
            while (rs.next()) {
                Pelicula pe = new Pelicula();
                pe.setId(rs.getInt("id"));
                pe.setTitulo(rs.getString("titulo"));
                pe.setGenero(rs.getString("genero"));
                pe.setDuracionMin(rs.getInt("duracion"));
                pe.setClasificacion(rs.getString("clasificacion"));
                lista.add(pe);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pelicula:" + e.getMessage());
        }
        return lista;
    }
}
