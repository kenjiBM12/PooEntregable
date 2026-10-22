package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISalaDao {

    @Override
    public boolean insert(Sala s) {
        boolean flag = false;
        if (s == null || s.getNombre() == null || s.getNombre().isBlank() || s.getTipo() == null || s.getTipo().isBlank() || s.getCapacidad() <= 0) {
            System.out.println("Datos de sala incorrectos");
            return flag;
        }
        String query = "INSERT INTO sala (nombre, tipo, capacidad) VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());

            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error en insert de sala: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Sala s) {
        boolean flag = false;
        if (s == null || s.getNombre() == null || s.getNombre().isBlank() || s.getTipo() == null || s.getTipo().isBlank() || s.getCapacidad() <= 0) {
            System.out.println("Datos de sala incorrectos");
            return flag;
        }
        String query = "UPDATE sala SET nombre=?, tipo=?, capacidad=? WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            st.setInt(4, s.getIdsala());

            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error en update de sala: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Sala searchById(int id) {
        Sala registro = null;
        String query = "SELECT id AS idsala, nombre, tipo, capacidad FROM sala WHERE id=?";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    registro = new Sala();
                    registro.setIdsala(rs.getInt("idsala"));
                    registro.setNombre(rs.getString("nombre"));
                    registro.setTipo(rs.getString("tipo"));
                    registro.setCapacidad(rs.getInt("capacidad"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar sala: " + e.getMessage());
            registro = null;
        }
        return registro;
    }

    @Override
    public boolean delete(Sala s) {
        boolean flag = false;
        String query = "DELETE FROM sala WHERE id=? AND NOT EXISTS (SELECT 1 FROM funcion WHERE id_sala=sala.id)";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getIdsala());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar sala: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Sala> listAll() {
        List<Sala> lista = new ArrayList<>();
        String query = "SELECT id AS idsala, nombre, tipo, capacidad FROM sala";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Sala registro = new Sala();
                registro.setIdsala(rs.getInt("idsala"));
                registro.setNombre(rs.getString("nombre"));
                registro.setTipo(rs.getString("tipo"));
                registro.setCapacidad(rs.getInt("capacidad"));
                lista.add(registro);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar sala: " + e.getMessage());
        }
        return lista;
    }
}
