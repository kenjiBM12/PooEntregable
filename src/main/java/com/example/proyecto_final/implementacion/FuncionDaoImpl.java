package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {

    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;

        String query = "INSERT INTO funcion "
                + "(id_pelicula, id_sala, horario, precio) "
                + "SELECT ?, ?, ?, ? "
                + "WHERE EXISTS "
                + "(SELECT 1 FROM pelicula WHERE idpelicula=?) "
                + "AND EXISTS "
                + "(SELECT 1 FROM sala WHERE id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());

            // Comprueba que existan la película y la sala.
            st.setInt(5, f.getPelicula().getIdpelicula());
            st.setInt(6, f.getSala().getIdsala());

            flag = st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error al insertar función: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;

        String query = "UPDATE funcion SET id_pelicula=?, id_sala=?, "
                + "horario=?, precio=? WHERE id=? "
                + "AND EXISTS "
                + "(SELECT 1 FROM pelicula WHERE idpelicula=?) "
                + "AND EXISTS "
                + "(SELECT 1 FROM sala WHERE id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getIdfuncion());

            st.setInt(6, f.getPelicula().getIdpelicula());
            st.setInt(7, f.getSala().getIdsala());

            flag = st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error al actualizar función: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public Funcion searchById(int id) {
        Funcion fu = null;

        String query = "SELECT f.id AS funcion_id, f.horario, f.precio, "
                + "p.idpelicula AS pelicula_id, p.titulo, p.genero, "
                + "p.duracion, p.clasificacion, "
                + "s.id AS sala_id, s.nombre AS sala_nombre, "
                + "s.tipo AS sala_tipo, s.capacidad "
                + "FROM funcion f, pelicula p, sala s "
                + "WHERE f.id_pelicula=p.idpelicula "
                + "AND f.id_sala=s.id "
                + "AND f.id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Pelicula p = new Pelicula();
                    p.setIdpelicula(rs.getInt("pelicula_id"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setGenero(rs.getString("genero"));
                    p.setDuracion(rs.getInt("duracion"));
                    p.setClasificacion(rs.getString("clasificacion"));

                    Sala s = new Sala();
                    s.setIdsala(rs.getInt("sala_id"));
                    s.setNombre(rs.getString("sala_nombre"));
                    s.setTipo(rs.getString("sala_tipo"));
                    s.setCapacidad(rs.getInt("capacidad"));

                    fu = new Funcion();
                    fu.setIdfuncion(rs.getInt("funcion_id"));
                    fu.setHorario(rs.getString("horario"));
                    fu.setPrecio(rs.getDouble("precio"));
                    fu.setPelicula(p);
                    fu.setSala(s);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al buscar función: " + e.getMessage());
        }

        return fu;
    }

    @Override
    public boolean delete(Funcion f) {
        boolean flag = false;

        // No elimina una función que tiene ventas registradas.
        String query = "DELETE FROM funcion WHERE id=? "
                + "AND NOT EXISTS "
                + "(SELECT 1 FROM venta WHERE id_funcion=funcion.id)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getIdfuncion());

            flag = st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error al eliminar función: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();

        String query = "SELECT f.id AS funcion_id, f.horario, f.precio, "
                + "p.idpelicula AS pelicula_id, p.titulo, p.genero, "
                + "p.duracion, p.clasificacion, "
                + "s.id AS sala_id, s.nombre AS sala_nombre, "
                + "s.tipo AS sala_tipo, s.capacidad "
                + "FROM funcion f, pelicula p, sala s "
                + "WHERE f.id_pelicula=p.idpelicula "
                + "AND f.id_sala=s.id";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setIdpelicula(rs.getInt("pelicula_id"));
                p.setTitulo(rs.getString("titulo"));
                p.setGenero(rs.getString("genero"));
                p.setDuracion(rs.getInt("duracion"));
                p.setClasificacion(rs.getString("clasificacion"));

                Sala s = new Sala();
                s.setIdsala(rs.getInt("sala_id"));
                s.setNombre(rs.getString("sala_nombre"));
                s.setTipo(rs.getString("sala_tipo"));
                s.setCapacidad(rs.getInt("capacidad"));

                Funcion fu = new Funcion();
                fu.setIdfuncion(rs.getInt("funcion_id"));
                fu.setHorario(rs.getString("horario"));
                fu.setPrecio(rs.getDouble("precio"));
                fu.setPelicula(p);
                fu.setSala(s);

                lista.add(fu);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al listar funciones: " + e.getMessage());
        }

        return lista;
    }
}