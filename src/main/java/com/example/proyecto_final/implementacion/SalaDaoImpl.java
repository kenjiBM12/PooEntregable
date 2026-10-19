package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISalaDao {

    public SalaDaoImpl() { crearTabla(); }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS sala (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "tipo TEXT NOT NULL, " +
                "capacidad INTEGER NOT NULL);";
        try (Connection conn = ConexionDB.getConexion(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public int insertar(Sala s) {
        int res = 0;
        String sql = "INSERT INTO sala(nombre, tipo, capacidad) VALUES(?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getTipo());
            ps.setInt(3, s.getCapacidad());
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public List<Sala> listar() {
        List<Sala> lista = new ArrayList<>();
        String sql = "SELECT * FROM sala";
        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Sala(rs.getInt("id"), rs.getString("nombre"), rs.getString("tipo"), rs.getInt("capacidad")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public int actualizar(Sala s) {
        int res = 0;
        String sql = "UPDATE sala SET nombre=?, tipo=?, capacidad=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getTipo());
            ps.setInt(3, s.getCapacidad());
            ps.setInt(4, s.getId());
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public int eliminar(int id) {
        int res = 0;
        String sql = "DELETE FROM sala WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }
}