package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISalaDao {

    @Override
    public boolean insert(Sala s) {
        String query = "INSERT INTO sala (nombre, tipo, capacidad) "
                + "VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al insertar sala: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Sala s) {
        String query = "UPDATE sala SET nombre=?, tipo=?, capacidad=? "
                + "WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            st.setInt(4, s.getIdsala());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar sala: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Sala searchById(int id) {
        String query = "SELECT * FROM sala WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return leerSala(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar sala: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean delete(Sala s) {
        String query = "DELETE FROM sala WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, s.getIdsala());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar sala: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Sala> listAll() {
        List<Sala> lista = new ArrayList<>();
        String query = "SELECT * FROM sala";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                lista.add(leerSala(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar salas: " + e.getMessage());
        }

        return lista;
    }

    private Sala leerSala(ResultSet rs) throws SQLException {
        Sala s = new Sala();

        s.setIdsala(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setTipo(rs.getString("tipo"));
        s.setCapacidad(rs.getInt("capacidad"));

        return s;
    }
}
