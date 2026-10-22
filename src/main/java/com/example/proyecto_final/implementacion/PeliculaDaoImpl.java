package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDaoImpl implements IPeliculaDao {

    @Override
    public boolean insert(Pelicula p) {
        boolean flag = false;
        if (p == null || p.getTitulo() == null || p.getTitulo().isBlank() || p.getGenero() == null || p.getGenero().isBlank() || p.getClasificacion() == null || p.getClasificacion().isBlank() || p.getDuracion() <= 0) {
            System.out.println("Datos de pelicula incorrectos");
            return flag;
        }
        String query = "INSERT INTO pelicula (titulo, genero, duracion, clasificacion) VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracion());
            st.setString(4, p.getClasificacion());

            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error en insert de pelicula: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Pelicula p) {
        boolean flag = false;
        if (p == null || p.getTitulo() == null || p.getTitulo().isBlank() || p.getGenero() == null || p.getGenero().isBlank() || p.getClasificacion() == null || p.getClasificacion().isBlank() || p.getDuracion() <= 0) {
            System.out.println("Datos de pelicula incorrectos");
            return flag;
        }
        String query = "UPDATE pelicula SET titulo=?, genero=?, duracion=?, clasificacion=? WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracion());
            st.setString(4, p.getClasificacion());
            st.setInt(5, p.getIdpelicula());

            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error en update de pelicula: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Pelicula searchById(int id) {
        Pelicula registro = null;
        String query = "SELECT id AS idpelicula, titulo, genero, duracion, clasificacion FROM pelicula WHERE id=?";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    registro = new Pelicula();
                    registro.setIdpelicula(rs.getInt("idpelicula"));
                    registro.setTitulo(rs.getString("titulo"));
                    registro.setGenero(rs.getString("genero"));
                    registro.setDuracion(rs.getInt("duracion"));
                    registro.setClasificacion(rs.getString("clasificacion"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar pelicula: " + e.getMessage());
            registro = null;
        }
        return registro;
    }

    @Override
    public boolean delete(Pelicula p) {
        boolean flag = false;
        String query = "DELETE FROM pelicula WHERE id=? AND NOT EXISTS (SELECT 1 FROM funcion WHERE id_pelicula=pelicula.id)";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getIdpelicula());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar pelicula: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Pelicula> listAll() {
        List<Pelicula> lista = new ArrayList<>();
        String query = "SELECT id AS idpelicula, titulo, genero, duracion, clasificacion FROM pelicula";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Pelicula registro = new Pelicula();
                registro.setIdpelicula(rs.getInt("idpelicula"));
                registro.setTitulo(rs.getString("titulo"));
                registro.setGenero(rs.getString("genero"));
                registro.setDuracion(rs.getInt("duracion"));
                registro.setClasificacion(rs.getString("clasificacion"));
                lista.add(registro);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pelicula: " + e.getMessage());
        }
        return lista;
    }
}
