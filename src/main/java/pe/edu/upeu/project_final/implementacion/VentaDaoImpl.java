package pe.edu.upeu.project_final.implementacion;

import pe.edu.upeu.project_final.interfaces.IVentaDao;
import pe.edu.upeu.project_final.modelo.Venta;
import pe.edu.upeu.project_final.utils.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    @Override
    public boolean insert(Venta v) {
        boolean flag = false;
        // CORREGIDO: INSERT registra los campos recibidos con sus nombres reales.
        String query = "INSERT INTO venta(id_funcion,cliente,cantidad_asientos,total_pagar,fecha_venta) VALUES(?,?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getIdFuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
                // AGREGADO: conserva en el objeto el ID generado por esta conexion.
                try (Statement consulta = cn.createStatement();
                     ResultSet rs = consulta.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        v.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al insertar venta.", e);
        }
        return flag;
    }

    @Override
    public boolean update(Venta v) {
        boolean flag = false;
        // CORREGIDO: usa idventa, que es la clave primaria de esta tabla.
        String query = "UPDATE venta SET id_funcion=?,cliente=?,cantidad_asientos=?,total_pagar=?,fecha_venta=? WHERE idventa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getIdFuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta());
            st.setInt(6, v.getId());
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar venta.", e);
        }
        return flag;
    }

    @Override
    public boolean delete(Venta v) {
        boolean flag = false;
        String query = "DELETE FROM venta WHERE idventa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getId());
            // CORREGIDO: un ID inexistente no debe indicar que hubo un borrado.
            int filasAfectadas = st.executeUpdate();
            if (filasAfectadas > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar venta.", e);
        }
        return flag;
    }

    @Override
    public Venta searchById(int id) {
        Venta dato = null;
        String query = "SELECT * FROM venta WHERE idventa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    dato = mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar venta.", e);
        }
        return dato;
    }

    @Override
    public List<Venta> listAll() {
        List<Venta> lista = new ArrayList<>();
        String query = "SELECT * FROM venta ORDER BY idventa";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            // CORREGIDO: se informa el error real en lugar de mostrar una lista vacia.
            throw new IllegalStateException("Error al listar venta.", e);
        }
        return lista;
    }

    @Override
    public List<Venta> listByDate(LocalDate fecha) {
        List<Venta> lista = new ArrayList<>();
        // Se conserva el reporte por dia, aunque fecha_venta tambien contenga hora.
        String query = "SELECT * FROM venta WHERE date(fecha_venta)=? ORDER BY idventa";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, fecha.toString());
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar ventas por fecha.", e);
        }
        return lista;
    }

    // Lectura comun para buscar por ID y listar.
    private Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("idventa"));
        v.setIdFuncion(rs.getInt("id_funcion"));
        v.setCliente(rs.getString("cliente"));
        v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
        v.setTotalPagar(rs.getDouble("total_pagar"));
        v.setFechaVenta(rs.getString("fecha_venta"));
        return v;
    }
}