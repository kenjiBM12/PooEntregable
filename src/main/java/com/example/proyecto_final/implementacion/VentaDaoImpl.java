package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.*;
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
        if (v == null || v.getFuncion() == null || v.getCliente() == null || v.getCliente().isBlank() || v.getCantidadAsientos() <= 0 || !Double.isFinite(v.getTotalPagar()) || v.getTotalPagar() <= 0) {
            System.out.println("Datos de venta incorrectos");
            return flag;
        }

        if (v.getFechaVenta() == null) {
            v.setFechaVenta(LocalDate.now());
        }
        String query = "INSERT INTO venta (id_funcion, cliente, cantidad_asientos, total_pagar, fecha_venta) SELECT ?, ?, ?, ?, ? WHERE EXISTS (SELECT 1 FROM funcion f, pelicula p, sala s WHERE f.id_pelicula=p.id AND f.id_sala=s.id AND f.id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta().toString());
            st.setInt(6, v.getFuncion().getIdfuncion());

            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error en insert de venta: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Venta v) {
        boolean flag = false;
        if (v == null || v.getFuncion() == null || v.getCliente() == null || v.getCliente().isBlank() || v.getCantidadAsientos() <= 0 || !Double.isFinite(v.getTotalPagar()) || v.getTotalPagar() <= 0) {
            System.out.println("Datos de venta incorrectos");
            return flag;
        }

        if (v.getFechaVenta() == null) {
            return false;
        }
        String query = "UPDATE venta SET id_funcion=?, cliente=?, cantidad_asientos=?, total_pagar=?, fecha_venta=? WHERE id=? AND EXISTS (SELECT 1 FROM funcion f, pelicula p, sala s WHERE f.id_pelicula=p.id AND f.id_sala=s.id AND f.id=?)";

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
            System.out.println("Error en update de venta: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Venta searchById(int id) {
        Venta registro = null;
        String query = "SELECT v.id AS idventa, v.cliente, v.cantidad_asientos, v.total_pagar, "
                + "substr(v.fecha_venta,1,10) AS fecha_venta, "
                + "f.id AS idfuncion, substr(f.horario,1,10) AS fecha_funcion, f.precio, "
                + "p.id AS idpelicula, p.titulo, p.genero, p.duracion, p.clasificacion, "
                + "s.id AS idsala, s.nombre AS sala_nombre, s.tipo AS sala_tipo, s.capacidad "
                + "FROM venta v, funcion f, pelicula p, sala s "
                + "WHERE v.id_funcion=f.id AND f.id_pelicula=p.id AND f.id_sala=s.id AND v.id=?";
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

                    Funcion f = new Funcion();
                    f.setIdfuncion(rs.getInt("idfuncion"));
                    f.setHorario(LocalDate.parse(rs.getString("fecha_funcion")));
                    f.setPrecio(rs.getDouble("precio"));
                    f.setPelicula(p);
                    f.setSala(s);

                    registro = new Venta();
                    registro.setIdventa(rs.getInt("idventa"));
                    registro.setCliente(rs.getString("cliente"));
                    registro.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                    registro.setTotalPagar(rs.getDouble("total_pagar"));
                    registro.setFechaVenta(LocalDate.parse(rs.getString("fecha_venta")));
                    registro.setFuncion(f);
                }
            }
        } catch (SQLException | DateTimeParseException e) {
            System.out.println("Error al buscar venta: " + e.getMessage());
            registro = null;
        }
        return registro;
    }

    @Override
    public boolean delete(Venta v) {
        boolean flag = false;
        String query = "DELETE FROM venta WHERE id=?";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getIdventa());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar venta: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Venta> listAll() {
        List<Venta> lista = new ArrayList<>();
        String query = "SELECT v.id AS idventa, v.cliente, v.cantidad_asientos, v.total_pagar, "
                + "substr(v.fecha_venta,1,10) AS fecha_venta, "
                + "f.id AS idfuncion, substr(f.horario,1,10) AS fecha_funcion, f.precio, "
                + "p.id AS idpelicula, p.titulo, p.genero, p.duracion, p.clasificacion, "
                + "s.id AS idsala, s.nombre AS sala_nombre, s.tipo AS sala_tipo, s.capacidad "
                + "FROM venta v, funcion f, pelicula p, sala s "
                + "WHERE v.id_funcion=f.id AND f.id_pelicula=p.id AND f.id_sala=s.id";
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

                Funcion f = new Funcion();
                f.setIdfuncion(rs.getInt("idfuncion"));
                f.setHorario(LocalDate.parse(rs.getString("fecha_funcion")));
                f.setPrecio(rs.getDouble("precio"));
                f.setPelicula(p);
                f.setSala(s);

                Venta registro = new Venta();
                registro.setIdventa(rs.getInt("idventa"));
                registro.setCliente(rs.getString("cliente"));
                registro.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                registro.setTotalPagar(rs.getDouble("total_pagar"));
                registro.setFechaVenta(LocalDate.parse(rs.getString("fecha_venta")));
                registro.setFuncion(f);
                lista.add(registro);
            }
        } catch (SQLException | DateTimeParseException e) {
            System.out.println("Error al listar venta: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Venta> listByDate(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Seleccione una fecha");
        }
        List<Venta> lista = new ArrayList<>();
        String query = "SELECT v.id AS idventa, v.cliente, v.cantidad_asientos, v.total_pagar, "
                + "substr(v.fecha_venta,1,10) AS fecha_venta, "
                + "f.id AS idfuncion, substr(f.horario,1,10) AS fecha_funcion, f.precio, "
                + "p.id AS idpelicula, p.titulo, p.genero, p.duracion, p.clasificacion, "
                + "s.id AS idsala, s.nombre AS sala_nombre, s.tipo AS sala_tipo, s.capacidad "
                + "FROM venta v, funcion f, pelicula p, sala s "
                + "WHERE v.id_funcion=f.id AND f.id_pelicula=p.id AND f.id_sala=s.id AND substr(v.fecha_venta,1,10)=? ORDER BY v.id";
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, fecha.toString());
            try (ResultSet rs = st.executeQuery()) {
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

                    Funcion f = new Funcion();
                    f.setIdfuncion(rs.getInt("idfuncion"));
                    f.setHorario(LocalDate.parse(rs.getString("fecha_funcion")));
                    f.setPrecio(rs.getDouble("precio"));
                    f.setPelicula(p);
                    f.setSala(s);

                    Venta registro = new Venta();
                    registro.setIdventa(rs.getInt("idventa"));
                    registro.setCliente(rs.getString("cliente"));
                    registro.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                    registro.setTotalPagar(rs.getDouble("total_pagar"));
                    registro.setFechaVenta(LocalDate.parse(rs.getString("fecha_venta")));
                    registro.setFuncion(f);
                    lista.add(registro);
                }
            }
        } catch (SQLException | DateTimeParseException e) {
            throw new IllegalStateException("No se pudo cargar el reporte: " + e.getMessage(), e);
        }
        return lista;
    }
}
