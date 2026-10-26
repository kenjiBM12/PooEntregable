package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.utils.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISalaDao {
    @Override
    public boolean insert(Sala s) {
        boolean flag = false;
        if (s == null || s.getNombre() == null || s.getNombre().isEmpty()
                || s.getTipo() == null || s.getTipo().isEmpty() || s.getCapacidad() <= 0) {
            return flag;
        }
        String query = "INSERT INTO sala(nombre,tipo,capacidad) VALUES(?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al insertar sala: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Sala s) {
        boolean flag = false;
        if (s == null || s.getNombre() == null || s.getNombre().isEmpty()
                || s.getTipo() == null || s.getTipo().isEmpty() || s.getCapacidad() <= 0) {
            return flag;
        }
        List<Funcion> funciones = new FuncionDaoImpl().listAll();
        List<Venta> ventas = new VentaDaoImpl().listAll();
        for (Funcion funcion : funciones) {
            if (funcion.getSala().getIdsala() == s.getIdsala()) {
                long ocupados = 0;
                for (Venta venta : ventas) {
                    if (venta.getFuncion().getIdfuncion() == funcion.getIdfuncion()) {
                        ocupados += venta.getCantidadAsientos();
                    }
                }
                if (ocupados > s.getCapacidad()) {
                    return flag;
                }
            }
        }
        String query = "UPDATE sala SET nombre=?,tipo=?,capacidad=? WHERE idsala=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            st.setInt(4, s.getIdsala());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al actualizar sala: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Sala searchById(int id) {
        Sala sala = null;
        String query = "SELECT * FROM sala WHERE idsala=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    sala = new Sala();
                    sala.setIdsala(rs.getInt("idsala"));
                    sala.setNombre(rs.getString("nombre"));
                    sala.setTipo(rs.getString("tipo"));
                    sala.setCapacidad(rs.getInt("capacidad"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar sala: " + e.getMessage());
        }
        return sala;
    }

    @Override
    public boolean delete(Sala s) {
        boolean flag = false;
        if (s == null) {
            return flag;
        }
        String query = "SELECT count(*) FROM funcion WHERE id_sala=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getIdsala());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return flag;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar sala: " + e.getMessage());
            return flag;
        }
        query = "DELETE FROM sala WHERE idsala=?";
        try (Connection cn = ConexionDB.getConnection();
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
        String query = "SELECT * FROM sala";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Sala sala = new Sala();
                sala.setIdsala(rs.getInt("idsala"));
                sala.setNombre(rs.getString("nombre"));
                sala.setTipo(rs.getString("tipo"));
                sala.setCapacidad(rs.getInt("capacidad"));
                lista.add(sala);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar salas: " + e.getMessage());
        }
        return lista;
    }
}
