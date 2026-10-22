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
        boolean flag = false;

        String query = "INSERT INTO sala (nombre, tipo, capacidad) "
                + "VALUES (?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());

            st.executeUpdate();
            flag = true;

        } catch (SQLException e) {
            System.out.println(
                    "Error al insertar sala: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public boolean update(Sala s) {
        boolean flag = false;

        String query = "UPDATE sala SET nombre=?, tipo=?, capacidad=? "
                + "WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            st.setInt(4, s.getIdsala());

            flag = st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error al actualizar sala: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public Sala searchById(int id) {
        Sala sa = null;

        String query = "SELECT * FROM sala WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    sa = new Sala();

                    sa.setIdsala(rs.getInt("idsala"));
                    sa.setNombre(rs.getString("nombre"));
                    sa.setTipo(rs.getString("tipo"));
                    sa.setCapacidad(rs.getInt("capacidad"));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al buscar sala: " + e.getMessage());
        }

        return sa;
    }

    @Override
    public boolean delete(Sala s) {
        boolean flag = false;

        String query = "DELETE FROM sala WHERE idsala=? ";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, s.getIdsala());

            st.executeUpdate();
            flag =true;

        } catch (SQLException e) {
            System.out.println(
                    "Error al eliminar sala: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public List<Sala> listAll() {
        List<Sala> lista = new ArrayList<>();

        String query = "SELECT * FROM sala";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Sala sa = new Sala();

                sa.setIdsala(rs.getInt("idsala"));
                sa.setNombre(rs.getString("nombre"));
                sa.setTipo(rs.getString("tipo"));
                sa.setCapacidad(rs.getInt("capacidad"));

                lista.add(sa);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al listar salas: " + e.getMessage());
        }

        return lista;
    }
}