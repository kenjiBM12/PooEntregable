package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {

    public FuncionDaoImpl() { crearTabla(); }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS funcion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_pelicula INTEGER NOT NULL, " +
                "id_sala INTEGER NOT NULL, " +
                "horario TEXT NOT NULL, " +
                "precio REAL NOT NULL);";
        try (Connection conn = ConexionDB.getConexion(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public int insertar(Funcion f) {
        int res = 0;
        String sql = "INSERT INTO funcion(id_pelicula, id_sala, horario, precio) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdPelicula());
            ps.setInt(2, f.getIdSala());
            ps.setString(3, f.getHorario());
            ps.setDouble(4, f.getPrecio());
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public List<Funcion> listar() {
        List<Funcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcion";
        try (Connection conn = ConexionDB.getConexion(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Funcion(rs.getInt("id"), rs.getInt("id_pelicula"), rs.getInt("id_sala"), rs.getString("horario"), rs.getDouble("precio")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public int actualizar(Funcion f) {
        int res = 0;
        String sql = "UPDATE funcion SET id_pelicula=?, id_sala=?, horario=?, precio=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getIdPelicula());
            ps.setInt(2, f.getIdSala());
            ps.setString(3, f.getHorario());
            ps.setDouble(4, f.getPrecio());
            ps.setInt(5, f.getId());
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public int eliminar(int id) {
        int res = 0;
        String sql = "DELETE FROM funcion WHERE id=?";
        try (Connection conn = ConexionDB.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            res = ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }
}