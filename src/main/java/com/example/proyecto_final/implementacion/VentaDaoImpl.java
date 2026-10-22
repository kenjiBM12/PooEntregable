package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    @Override
    public boolean insert(Venta v) {
        boolean flag = false;

        if (v == null || v.getFuncion() == null
                || v.getCliente() == null
                || v.getCliente().isBlank()
                || v.getCantidadAsientos() <= 0
                || !Double.isFinite(v.getTotalPagar())
                || v.getTotalPagar() <= 0) {

            System.out.println("Datos de venta incorrectos");
            return flag;
        }

        // Si no se indicó una fecha, utiliza la fecha de hoy.
        if (v.getFechaVenta() == null) {
            v.setFechaVenta(LocalDate.now());
        }

        String query = "INSERT INTO venta "
                + "(id_funcion, cliente, cantidad_asientos, "
                + "total_pagar, fecha_venta) "
                + "SELECT ?, ?, ?, ?, ? WHERE EXISTS ("
                + "SELECT 1 FROM funcion f, pelicula p, sala s "
                + "WHERE f.id_pelicula=p.idpelicula "
                + "AND f.id_sala=s.id AND f.id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());

            // LocalDate.toString() devuelve yyyy-MM-dd.
            st.setString(5, v.getFechaVenta().toString());

            st.setInt(6, v.getFuncion().getIdfuncion());

            flag = st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error al insertar venta: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public boolean update(Venta v) {
        boolean flag = false;

        if (v == null || v.getFuncion() == null
                || v.getCliente() == null
                || v.getCliente().isBlank()
                || v.getCantidadAsientos() <= 0
                || !Double.isFinite(v.getTotalPagar())
                || v.getTotalPagar() <= 0
                || v.getFechaVenta() == null) {

            System.out.println("Datos de venta incorrectos");
            return flag;
        }

        String query = "UPDATE venta SET id_funcion=?, cliente=?, "
                + "cantidad_asientos=?, total_pagar=?, fecha_venta=? "
                + "WHERE idventa=? AND EXISTS ("
                + "SELECT 1 FROM funcion f, pelicula p, sala s "
                + "WHERE f.id_pelicula=p.idpelicula "
                + "AND f.id_sala=s.id AND f.id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta().toString());
            st.setInt(6, v.getIdventa());
            st.setInt(7, v.getFuncion().getIdfuncion());

            flag = st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error al actualizar venta: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public Venta searchById(int id) {
        Venta ve = null;

        String query = "SELECT v.*, "
                + "substr(v.fecha_venta, 1, 10) AS fecha, "
                + "f.id AS funcion_id, f.horario, f.precio, "
                + "p.idpelicula AS pelicula_id, p.titulo, p.genero, "
                + "p.duracion, p.clasificacion, "
                + "s.id AS sala_id, s.nombre AS sala_nombre, "
                + "s.tipo AS sala_tipo, s.capacidad "
                + "FROM venta v, funcion f, pelicula p, sala s "
                + "WHERE v.id_funcion=f.id "
                + "AND f.id_pelicula=p.idpelicula "
                + "AND f.id_sala=s.id "
                + "AND v.idventa=?";

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

                    Funcion f = new Funcion();
                    f.setIdfuncion(rs.getInt("funcion_id"));

                    // Horario de la función: String.
                    f.setHorario(rs.getString("horario"));

                    f.setPrecio(rs.getDouble("precio"));
                    f.setPelicula(p);
                    f.setSala(s);

                    String fecha = rs.getString("fecha");

                    if (fecha == null || fecha.isBlank()) {
                        throw new SQLException("La venta no tiene fecha");
                    }

                    Venta v = new Venta();
                    v.setIdventa(rs.getInt("idventa"));
                    v.setCliente(rs.getString("cliente"));
                    v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                    v.setTotalPagar(rs.getDouble("total_pagar"));

                    // Convierte el texto de SQLite a LocalDate.
                    v.setFechaVenta(LocalDate.parse(fecha));

                    v.setFuncion(f);
                    ve = v;
                }
            }

        } catch (SQLException | DateTimeParseException e) {
            System.out.println(
                    "Error al buscar venta: " + e.getMessage());
        }

        return ve;
    }

    @Override
    public boolean delete(Venta v) {
        boolean flag = false;

        String query = "DELETE FROM venta WHERE idventa=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getIdventa());

            flag = st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println(
                    "Error al eliminar venta: " + e.getMessage());
        }

        return flag;
    }

    @Override
    public List<Venta> listAll() {
        List<Venta> lista = new ArrayList<>();

        String query = "SELECT v.*, "
                + "substr(v.fecha_venta, 1, 10) AS fecha, "
                + "f.id AS funcion_id, f.horario, f.precio, "
                + "p.idpelicula AS pelicula_id, p.titulo, p.genero, "
                + "p.duracion, p.clasificacion, "
                + "s.id AS sala_id, s.nombre AS sala_nombre, "
                + "s.tipo AS sala_tipo, s.capacidad "
                + "FROM venta v, funcion f, pelicula p, sala s "
                + "WHERE v.id_funcion=f.id "
                + "AND f.id_pelicula=p.idpelicula "
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

                Funcion f = new Funcion();
                f.setIdfuncion(rs.getInt("funcion_id"));
                f.setHorario(rs.getString("horario"));
                f.setPrecio(rs.getDouble("precio"));
                f.setPelicula(p);
                f.setSala(s);

                String fecha = rs.getString("fecha");

                if (fecha == null || fecha.isBlank()) {
                    throw new SQLException("La venta no tiene fecha");
                }

                Venta v = new Venta();
                v.setIdventa(rs.getInt("idventa"));
                v.setCliente(rs.getString("cliente"));
                v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                v.setTotalPagar(rs.getDouble("total_pagar"));
                v.setFechaVenta(LocalDate.parse(fecha));
                v.setFuncion(f);

                lista.add(v);
            }

        } catch (SQLException | DateTimeParseException e) {
            System.out.println(
                    "Error al listar ventas: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public List<Venta> listByDate(LocalDate fecha) {
        List<Venta> lista = new ArrayList<>();

        if (fecha == null) {
            throw new IllegalArgumentException("Seleccione una fecha");
        }

        String query = "SELECT v.*, "
                + "substr(v.fecha_venta, 1, 10) AS fecha, "
                + "f.id AS funcion_id, f.horario, f.precio, "
                + "p.idpelicula AS pelicula_id, p.titulo, p.genero, "
                + "p.duracion, p.clasificacion, "
                + "s.id AS sala_id, s.nombre AS sala_nombre, "
                + "s.tipo AS sala_tipo, s.capacidad "
                + "FROM venta v, funcion f, pelicula p, sala s "
                + "WHERE v.id_funcion=f.id "
                + "AND f.id_pelicula=p.idpelicula "
                + "AND f.id_sala=s.id "
                + "AND substr(v.fecha_venta, 1, 10)=? "
                + "ORDER BY v.idventa";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, fecha.toString());

            try (ResultSet rs = st.executeQuery()) {
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

                    Funcion f = new Funcion();
                    f.setIdfuncion(rs.getInt("funcion_id"));
                    f.setHorario(rs.getString("horario"));
                    f.setPrecio(rs.getDouble("precio"));
                    f.setPelicula(p);
                    f.setSala(s);

                    Venta v = new Venta();
                    v.setIdventa(rs.getInt("idventa"));
                    v.setCliente(rs.getString("cliente"));
                    v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                    v.setTotalPagar(rs.getDouble("total_pagar"));
                      v.setFechaVenta(
                            LocalDate.parse(rs.getString("fecha"))
                    );
                    v.setFuncion(f);

                    lista.add(v);
                }
            }

        } catch (SQLException | DateTimeParseException e) {
            throw new IllegalStateException(
                    "Error al cargar reporte: " + e.getMessage(), e);
        }

        return lista;
    }
}