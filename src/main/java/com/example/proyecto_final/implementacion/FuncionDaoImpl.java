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

    private static final String SELECT = """
            SELECT f.id AS funcion_id, f.horario, f.precio,
                   p.id AS pelicula_id, p.titulo, p.genero,
                   p.duracion, p.clasificacion,
                   s.id AS sala_id, s.nombre AS sala_nombre,
                   s.tipo AS sala_tipo, s.capacidad
            FROM funcion f
            JOIN pelicula p ON f.id_pelicula = p.id
            JOIN sala s ON f.id_sala = s.id
            """;

    @Override
    public boolean insert(Funcion f) {
        String query = "INSERT INTO funcion "
                + "(id_pelicula, id_sala, horario, precio) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al insertar función: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Funcion f) {
        String query = "UPDATE funcion SET id_pelicula=?, id_sala=?, "
                + "horario=?, precio=? WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getIdfuncion());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar función: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Funcion searchById(int id) {
        String query = SELECT + " WHERE f.id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return leerFuncion(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar función: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean delete(Funcion f) {
        String query = "DELETE FROM funcion WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getIdfuncion());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar función: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(SELECT);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                lista.add(leerFuncion(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar funciones: " + e.getMessage());
        }

        return lista;
    }

    private Funcion leerFuncion(ResultSet rs) throws SQLException {
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

        Funcion f = new Funcion();
        f.setIdfuncion(rs.getInt("funcion_id"));
        f.setHorario(rs.getString("horario"));
        f.setPrecio(rs.getDouble("precio"));
        f.setPelicula(p);
        f.setSala(s);

        return f;
    }
}
