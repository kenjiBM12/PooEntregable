package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.utils.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDaoImpl implements IPeliculaDao {
    @Override
    public boolean insert(Pelicula p) {
        boolean flag = false;
        if (p == null || p.getTitulo() == null || p.getTitulo().isEmpty()
                || p.getGenero() == null || p.getGenero().isEmpty()
                || p.getClasificacion() == null || p.getClasificacion().isEmpty()
                || p.getDuracion() <= 0) {
            return flag;
        }
        String query = "INSERT INTO pelicula(titulo,genero,duracion,clasificacion) VALUES(?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracion());
            st.setString(4, p.getClasificacion());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al insertar pelicula: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Pelicula p) {
        boolean flag = false;
        if (p == null || p.getTitulo() == null || p.getTitulo().isEmpty()
                || p.getGenero() == null || p.getGenero().isEmpty()
                || p.getClasificacion() == null || p.getClasificacion().isEmpty()
                || p.getDuracion() <= 0) {
            return flag;
        }
        String query = "UPDATE pelicula SET titulo=?,genero=?,duracion=?,clasificacion=? WHERE idpelicula=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, p.getTitulo());
            st.setString(2, p.getGenero());
            st.setInt(3, p.getDuracion());
            st.setString(4, p.getClasificacion());
            st.setInt(5, p.getIdpelicula());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al actualizar pelicula: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Pelicula searchById(int id) {
        Pelicula pelicula = null;
        String query = "SELECT * FROM pelicula WHERE idpelicula=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    pelicula = new Pelicula();
                    pelicula.setIdpelicula(rs.getInt("idpelicula"));
                    pelicula.setTitulo(rs.getString("titulo"));
                    pelicula.setGenero(rs.getString("genero"));
                    pelicula.setDuracion(rs.getInt("duracion"));
                    pelicula.setClasificacion(rs.getString("clasificacion"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar pelicula: " + e.getMessage());
        }
        return pelicula;
    }

    @Override
    public boolean delete(Pelicula p) {
        boolean flag = false;
        if (p == null) {
            return flag;
        }
        String query = "SELECT count(*) FROM funcion WHERE id_pelicula=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getIdpelicula());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return flag;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar pelicula: " + e.getMessage());
            return flag;
        }
        query = "DELETE FROM pelicula WHERE idpelicula=?";
        try (Connection cn = ConexionDB.getConnection();
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
        String query = "SELECT * FROM pelicula";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Pelicula pelicula = new Pelicula();
                pelicula.setIdpelicula(rs.getInt("idpelicula"));
                pelicula.setTitulo(rs.getString("titulo"));
                pelicula.setGenero(rs.getString("genero"));
                pelicula.setDuracion(rs.getInt("duracion"));
                pelicula.setClasificacion(rs.getString("clasificacion"));
                lista.add(pelicula);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar peliculas: " + e.getMessage());
        }
        return lista;
    }
}
