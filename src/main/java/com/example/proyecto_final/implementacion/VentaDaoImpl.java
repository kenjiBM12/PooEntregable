package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    public VentaDaoImpl() {
        crearTabla();
    }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS venta (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_funcion INTEGER NOT NULL, " +
                "cliente TEXT NOT NULL, " +
                "cantidad_asientos INTEGER NOT NULL, " +
                "total_pagar REAL NOT NULL, " +
                "fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
        try (Connection conn = ConexionDB.getConexion(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int registrarVenta(Venta v) {
        int res = 0;
        String sql = "INSERT INTO venta(id_funcion, cliente, cantidad_asientos, total_pagar) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getIdFuncion());
            ps.setString(2, v.getCliente());
            ps.setInt(3, v.getCantidadAsientos());
            ps.setDouble(4, v.getTotalPagar());
            res = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<Venta> listarVentas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM venta";
        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Venta(
                        rs.getInt("id"),
                        rs.getInt("id_funcion"),
                        rs.getString("cliente"),
                        rs.getInt("cantidad_asientos"),
                        rs.getDouble("total_pagar"),
                        rs.getString("fecha_venta")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}