package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    private static final String SELECT = """
            SELECT v.id AS venta_id, v.cliente, v.cantidad_asientos,
                   v.total_pagar, v.fecha_venta,
                   f.id AS funcion_id, f.horario, f.precio,
                   p.id AS pelicula_id, p.titulo, p.genero,
                   p.duracion, p.clasificacion,
                   s.id AS sala_id, s.nombre AS sala_nombre,
                   s.tipo AS sala_tipo, s.capacidad
            FROM venta v
            JOIN funcion f ON v.id_funcion = f.id
            JOIN pelicula p ON f.id_pelicula = p.id
            JOIN sala s ON f.id_sala = s.id
            """;

    @Override
    public boolean insert(Venta v) {
        String query = "INSERT INTO venta "
                + "(id_funcion, cliente, cantidad_asientos, total_pagar) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al insertar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Venta v) {
        String query = "UPDATE venta SET id_funcion=?, cliente=?, "
                + "cantidad_asientos=?, total_pagar=? WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setInt(5, v.getIdventa());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Venta searchById(int id) {
        String query = SELECT + " WHERE v.id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return leerVenta(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar venta: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean delete(Venta v) {
        String query = "DELETE FROM venta WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getIdventa());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Venta> listAll() {
        List<Venta> lista = new ArrayList<>();

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(SELECT);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                lista.add(leerVenta(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }

        return lista;
    }

    private Venta leerVenta(ResultSet rs) throws SQLException {
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

        Venta v = new Venta();
        v.setIdventa(rs.getInt("venta_id"));
        v.setCliente(rs.getString("cliente"));
        v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
        v.setTotalPagar(rs.getDouble("total_pagar"));
        v.setFechaVenta(rs.getString("fecha_venta"));
        v.setFuncion(f);

        return v;
    }
}
