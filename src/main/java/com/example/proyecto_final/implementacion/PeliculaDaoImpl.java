
package com.example.proyecto_final.implementacion;


import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDaoImpl implements IPeliculaDao {


    @Override
    public boolean insert(Pelicula p) {
        boolean flag = false;

        String query = "INSERT INTO pelicula "
                + "(titulo, genero, duracion, clasificacion) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracion());
            st.setString(4, p.getClasificacion());

            st.executeUpdate();
            flag = true;

        } catch (SQLException e) {
            System.out.println(
                    "Error al insertar película: " + e.getMessage());
        }

        return flag;
    }

    @Override

    public boolean update(Pelicula p) {

        boolean flag = false;
        String query = "UPDATE pelicula SET titulo=?, genero=?, "
                + "duracion=?, clasificacion=? WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracion());
            st.setString(4, p.getClasificacion());
            st.setInt(5, p.getIdpelicula());
            st.executeUpdate();
            flag = true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar película: " + e.getMessage());
        }
            return flag;
        }


    @Override
    public Pelicula searchById(int id) {
        Pelicula pe = null;
        String query = " SELECT * FROM pelicula WHERE id=? ";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    pe.setIdpelicula(rs.getInt("idpelicula"));
                    pe.setTitulo(rs.getString("titulo"));
                    pe.setDuracion(rs.getInt("duracion"));
                    pe.setGenero(rs.getString("genero"));
                    pe.setClasificacion(rs.getString("clasificacion"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar película: " + e.getMessage());

        }
        return pe;
    }

    @Override
    public boolean delete(Pelicula p) {
        boolean flag = false;
        String query = " DELETE FROM pelicula WHERE id=? ";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, p.getIdpelicula());
            st.executeUpdate();
            flag = true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar película: " + e.getMessage());
        }
            return false;

    }

    @Override
    public List<Pelicula> listAll() {
        List<Pelicula> lista = new ArrayList<>();
        String query = "SELECT * FROM pelicula";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Pelicula pe = new Pelicula();
                pe.setIdpelicula(rs.getInt("idpelicula"));
                pe.setTitulo(rs.getString("titulo"));
                pe.setDuracion(rs.getInt("duracion"));
                pe.setGenero(rs.getString("genero"));
                pe.setClasificacion(rs.getString("clasisficacion"));
                lista.add(pe);

            }

        } catch (SQLException e) {
            System.err.println("Error al listar películas: " + e.getMessage());
        }

        return lista;
    }

    private Pelicula leerPelicula(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();

        p.setIdpelicula(rs.getInt("idpelic"));
        p.setTitulo(rs.getString("titulo"));
        p.setGenero(rs.getString("genero"));
        p.setDuracion(rs.getInt("duracion"));
        p.setClasificacion(rs.getString("clasificacion"));

        return p;
    }
}
