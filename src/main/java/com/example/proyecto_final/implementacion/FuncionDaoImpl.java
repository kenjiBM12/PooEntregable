package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {

    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;
        if (f == null || f.getPelicula() == null || f.getSala() == null || f.getHorario() == null || !Double.isFinite(f.getPrecio()) || f.getPrecio() <= 0) {
            System.out.println("Datos de funcion incorrectos");
            return flag;
        }
        String query = "INSERT INTO funcion (id_pelicula, id_sala, horario, precio) SELECT ?, ?, ?, ? WHERE EXISTS (SELECT 1 FROM pelicula WHERE id=?) AND EXISTS (SELECT 1 FROM sala WHERE id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario().toString());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getPelicula().getIdpelicula());
            st.setInt(6, f.getSala().getIdsala());

            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error en insert de funcion: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;
        if (f == null || f.getPelicula() == null || f.getSala() == null || f.getHorario() == null || !Double.isFinite(f.getPrecio()) || f.getPrecio() <= 0) {
            System.out.println("Datos de funcion incorrectos");
            return flag;
        }
        String query = "UPDATE funcion SET id_pelicula=?, id_sala=?, horario=?, precio=? WHERE id=? AND EXISTS (SELECT 1 FROM pelicula WHERE id=?) AND EXISTS (SELECT 1 FROM sala WHERE id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario().toString());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getIdfuncion());
            st.setInt(6, f.getPelicula().getIdpelicula());
            st.setInt(7, f.getSala().getIdsala());

            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error en update de funcion: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Funcion searchById(int id) {
        Funcion registro = null;
        String query = "SELECT f.id AS idfuncion, substr(f.horario,1,10) AS fecha_funcion, f.precio, "
                + "p.id AS idpelicula, p.titulo, p.genero, p.duracion, p.clasificacion, "
                + "s.id AS idsala, s.nombre AS sala_nombre, s.tipo AS sala_tipo, s.capacidad "
                + "FROM funcion f, pelicula p, sala s "
                + "WHERE f.id_pelicula=p.id AND f.id_sala=s.id AND f.id=?";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Pelicula p = new Pelicula();
                    p.setIdpelicula(rs.getInt("idpelicula"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setGenero(rs.getString("genero"));
                    p.setDuracion(rs.getInt("duracion"));
                    p.setClasificacion(rs.getString("clasificacion"));

                    Sala s = new Sala();
                    s.setIdsala(rs.getInt("idsala"));
                    s.setNombre(rs.getString("sala_nombre"));
                    s.setTipo(rs.getString("sala_tipo"));
                    s.setCapacidad(rs.getInt("capacidad"));

                    registro = new Funcion();
                    registro.setIdfuncion(rs.getInt("idfuncion"));

                    registro.setHorario(LocalDate.parse(rs.getString("fecha_funcion")));
                    registro.setPrecio(rs.getDouble("precio"));
                    registro.setPelicula(p);
                    registro.setSala(s);
                }
            }
        } catch (SQLException | DateTimeParseException e) {
            System.out.println("Error al buscar funcion: " + e.getMessage());
            registro = null;
        }
        return registro;
    }

    @Override
    public boolean delete(Funcion f) {
        boolean flag = false;
        String query = "DELETE FROM funcion WHERE id=? AND NOT EXISTS (SELECT 1 FROM venta WHERE id_funcion=funcion.id)";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar funcion: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();
        String query = "SELECT f.id AS idfuncion, substr(f.horario,1,10) AS fecha_funcion, f.precio, "
                + "p.id AS idpelicula, p.titulo, p.genero, p.duracion, p.clasificacion, "
                + "s.id AS idsala, s.nombre AS sala_nombre, s.tipo AS sala_tipo, s.capacidad "
                + "FROM funcion f, pelicula p, sala s "
                + "WHERE f.id_pelicula=p.id AND f.id_sala=s.id";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setIdpelicula(rs.getInt("idpelicula"));
                p.setTitulo(rs.getString("titulo"));
                p.setGenero(rs.getString("genero"));
                p.setDuracion(rs.getInt("duracion"));
                p.setClasificacion(rs.getString("clasificacion"));

                Sala s = new Sala();
                s.setIdsala(rs.getInt("idsala"));
                s.setNombre(rs.getString("sala_nombre"));
                s.setTipo(rs.getString("sala_tipo"));
                s.setCapacidad(rs.getInt("capacidad"));

                Funcion registro = new Funcion();
                registro.setIdfuncion(rs.getInt("idfuncion"));

                registro.setHorario(LocalDate.parse(rs.getString("fecha_funcion")));
                registro.setPrecio(rs.getDouble("precio"));
                registro.setPelicula(p);
                registro.setSala(s);
                lista.add(registro);
            }
        } catch (SQLException | DateTimeParseException e) {
            System.out.println("Error al listar funcion: " + e.getMessage());
        }
        return lista;
    }
}
