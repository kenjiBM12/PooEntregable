package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {
    @Override
    public boolean insert(Venta v) {
        boolean flag = false;
        if (v == null || v.getFuncion() == null || v.getCliente() == null
                || v.getCliente().isEmpty() || v.getCantidadAsientos() <= 0) {
            return flag;
        }
        Funcion funcion = new FuncionDaoImpl().searchById(v.getFuncion().getIdfuncion());
        if (funcion == null) {
            return flag;
        }
        double total = funcion.getPrecio() * v.getCantidadAsientos();
        if (!(total > 0) || total > Double.MAX_VALUE) {
            return flag;
        }
        Venta nueva = new Venta();
        nueva.setFuncion(funcion);
        nueva.setCantidadAsientos(v.getCantidadAsientos());
        if (!ValidateCapacidad(nueva)) {
            return flag;
        }
        if (v.getFechaVenta() == null) {
            v.setFechaVenta(LocalDate.now());
        }
        v.setTotalPagar(total);
        String query = "INSERT INTO venta(id_funcion,cliente,cantidad_asientos,total_pagar,fecha_venta) VALUES(?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, funcion.getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta().toString());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al insertar venta: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Venta v) {
        boolean flag = false;
        if (v == null || v.getFuncion() == null || v.getCliente() == null
                || v.getCliente().isEmpty() || v.getCantidadAsientos() <= 0
                || v.getFechaVenta() == null) {
            return flag;
        }
        Venta anterior = searchById(v.getIdventa());
        Funcion funcion = new FuncionDaoImpl().searchById(v.getFuncion().getIdfuncion());
        if (anterior == null || funcion == null || !ValidateCapacidad(v)) {
            return flag;
        }
        double total;
        if (anterior.getFuncion().getIdfuncion() == funcion.getIdfuncion()) {
            if (anterior.getCantidadAsientos() <= 0) {
                return flag;
            }
            if (anterior.getCantidadAsientos() == v.getCantidadAsientos()) {
                total = anterior.getTotalPagar();
            } else {
                total = anterior.getTotalPagar() / anterior.getCantidadAsientos()
                        * v.getCantidadAsientos();
            }
        } else {
            total = funcion.getPrecio() * v.getCantidadAsientos();
        }
        if (!(total > 0) || total > Double.MAX_VALUE) {
            return flag;
        }
        v.setTotalPagar(total);
        String query = "UPDATE venta SET id_funcion=?,cliente=?,cantidad_asientos=?,total_pagar=?,fecha_venta=? WHERE idventa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, funcion.getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta().toString());
            st.setInt(6, v.getIdventa());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Venta searchById(int id) {
        Venta venta = null;
        String query = "SELECT v.idventa,v.cliente,v.cantidad_asientos,v.total_pagar,v.fecha_venta,"
                + "f.id AS idfuncion,f.horario,f.precio,"
                + "p.idpelicula,p.titulo,p.genero,p.duracion,p.clasificacion,"
                + "s.idsala,s.nombre AS sala_nombre,s.tipo AS sala_tipo,s.capacidad"
                + " FROM venta v,funcion f,pelicula p,sala s"
                + " WHERE v.id_funcion=f.id AND f.id_pelicula=p.idpelicula"
                + " AND f.id_sala=s.idsala AND v.idventa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Pelicula pelicula = new Pelicula();
                    pelicula.setIdpelicula(rs.getInt("idpelicula"));
                    pelicula.setTitulo(rs.getString("titulo"));
                    pelicula.setGenero(rs.getString("genero"));
                    pelicula.setDuracion(rs.getInt("duracion"));
                    pelicula.setClasificacion(rs.getString("clasificacion"));

                    Sala sala = new Sala();
                    sala.setIdsala(rs.getInt("idsala"));
                    sala.setNombre(rs.getString("sala_nombre"));
                    sala.setTipo(rs.getString("sala_tipo"));
                    sala.setCapacidad(rs.getInt("capacidad"));

                    Funcion funcion = new Funcion();
                    funcion.setIdfuncion(rs.getInt("idfuncion"));
                    funcion.setHorario(rs.getString("horario"));
                    funcion.setPrecio(rs.getDouble("precio"));
                    funcion.setPelicula(pelicula);
                    funcion.setSala(sala);

                    venta = new Venta();
                    venta.setIdventa(rs.getInt("idventa"));
                    venta.setCliente(rs.getString("cliente"));
                    venta.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                    venta.setTotalPagar(rs.getDouble("total_pagar"));
                    venta.setFechaVenta(LocalDate.parse(rs.getString("fecha_venta").substring(0, 10)));
                    venta.setFuncion(funcion);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar venta: " + e.getMessage());
        }
        return venta;
    }

    @Override
    public boolean delete(Venta v) {
        boolean flag = false;
        if (v == null) {
            return flag;
        }
        String query = "DELETE FROM venta WHERE idventa=?";
        try (Connection cn = ConexionDB.getConnection();
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
        String query = "SELECT v.idventa,v.cliente,v.cantidad_asientos,v.total_pagar,v.fecha_venta,"
                + "f.id AS idfuncion,f.horario,f.precio,"
                + "p.idpelicula,p.titulo,p.genero,p.duracion,p.clasificacion,"
                + "s.idsala,s.nombre AS sala_nombre,s.tipo AS sala_tipo,s.capacidad"
                + " FROM venta v,funcion f,pelicula p,sala s"
                + " WHERE v.id_funcion=f.id AND f.id_pelicula=p.idpelicula AND f.id_sala=s.idsala";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Pelicula pelicula = new Pelicula();
                pelicula.setIdpelicula(rs.getInt("idpelicula"));
                pelicula.setTitulo(rs.getString("titulo"));
                pelicula.setGenero(rs.getString("genero"));
                pelicula.setDuracion(rs.getInt("duracion"));
                pelicula.setClasificacion(rs.getString("clasificacion"));

                Sala sala = new Sala();
                sala.setIdsala(rs.getInt("idsala"));
                sala.setNombre(rs.getString("sala_nombre"));
                sala.setTipo(rs.getString("sala_tipo"));
                sala.setCapacidad(rs.getInt("capacidad"));

                Funcion funcion = new Funcion();
                funcion.setIdfuncion(rs.getInt("idfuncion"));
                funcion.setHorario(rs.getString("horario"));
                funcion.setPrecio(rs.getDouble("precio"));
                funcion.setPelicula(pelicula);
                funcion.setSala(sala);

                Venta venta = new Venta();
                venta.setIdventa(rs.getInt("idventa"));
                venta.setCliente(rs.getString("cliente"));
                venta.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                venta.setTotalPagar(rs.getDouble("total_pagar"));
                venta.setFechaVenta(LocalDate.parse(rs.getString("fecha_venta").substring(0, 10)));
                venta.setFuncion(funcion);
                lista.add(venta);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Venta> listByDate(LocalDate fecha) {
        List<Venta> lista = new ArrayList<>();
        if (fecha == null) {
            return lista;
        }
        List<Venta> ventas = listAll();
        for (Venta venta : ventas) {
            if (fecha.equals(venta.getFechaVenta())) {
                lista.add(venta);
            }
        }
        return lista;
    }

    @Override
    public boolean ValidateCapacidad(Venta v) {
        boolean flag = false;
        if (v == null || v.getFuncion() == null || v.getCantidadAsientos() <= 0) {
            return flag;
        }
        Funcion funcion = new FuncionDaoImpl().searchById(v.getFuncion().getIdfuncion());
        if (funcion == null) {
            return flag;
        }
        long ocupados = 0;
        String query = "SELECT cantidad_asientos FROM venta WHERE id_funcion=?";
        if (v.getIdventa() > 0) {
            query = query + " AND idventa<>?";
        }
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, funcion.getIdfuncion());
            if (v.getIdventa() > 0) {
                st.setInt(2, v.getIdventa());
            }
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    ocupados += rs.getInt("cantidad_asientos");
                }
            }
            flag = ocupados + v.getCantidadAsientos() <= funcion.getSala().getCapacidad();
        } catch (SQLException e) {
            System.out.println("Error al validar capacidad: " + e.getMessage());
        }
        return flag;
    }
}
