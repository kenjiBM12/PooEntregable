package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDaoImpl implements IPeliculaDao {

    public PeliculaDaoImpl() {
        crearTabla();
    }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS pelicula (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "genero TEXT NOT NULL, " +
                "duracion_min INTEGER NOT NULL, " +
                "clasificacion TEXT NOT NULL);";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insertar(Pelicula p) {
        int res = 0;
        String sql = "INSERT INTO pelicula(titulo, genero, duracion_min, clasificacion) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getGenero());
            ps.setInt(3, p.getDuracionMin());
            ps.setString(4, p.getClasificacion());
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public List<Pelicula> listar() {
        List<Pelicula> lista = new ArrayList<>();
        String sql = "SELECT * FROM pelicula";
        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Pelicula(rs.getInt("id"), rs.getString("titulo"), rs.getString("genero"), rs.getInt("duracion_min"), rs.getString("clasificacion")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public int actualizar(Pelicula p) {
        int res = 0;
        String sql = "UPDATE pelicula SET titulo=?, genero=?, duracion_min=?, clasificacion=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getGenero());
            ps.setInt(3, p.getDuracionMin());
            ps.setString(4, p.getClasificacion());
            ps.setInt(5, p.getId());
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public int eliminar(int id) {
        int res = 0;
        String sql = "DELETE FROM pelicula WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }
}