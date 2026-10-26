package pe.edu.upeu.project_final.implementacion;

import pe.edu.upeu.project_final.interfaces.IVentaDao;
import pe.edu.upeu.project_final.modelo.Venta;
import pe.edu.upeu.project_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    @Override
    public boolean insert(Venta v) {
        boolean flag = false;
        String query = "INSERT INTO venta(id_funcion,cliente,cantidad_asientos,total_pagar,fecha_venta)VALUES(?,?,?,?,?);";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getIdFuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar venta:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Venta v) {
        boolean flag = false;
        String query = " UPDATE venta SET id_funcion=?, cliente=?, cantidad_asientos=?, total_pagar=?, fecha_venta=? " +
                " WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getIdFuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta());
            st.setInt(6, v.getId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Venta searchById(int id) {
        Venta ve = null;
        String query = "SELECT * FROM venta WHERE id=?;";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    ve = new Venta();
                    ve.setId(rs.getInt("id"));
                    ve.setIdFuncion(rs.getInt("id_funcion"));
                    ve.setCliente(rs.getString("cliente"));
                    ve.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                    ve.setTotalPagar(rs.getDouble("total_pagar"));
                    ve.setFechaVenta(rs.getString("fecha_venta"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar venta por Id:" + e.getMessage());
        }
        return ve;
    }

    @Override
    public boolean delete(Venta v) {
        boolean flag = false;
        String query = "DELETE FROM venta WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar venta:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Venta> listAll() {
        List<Venta> lista = new ArrayList<>();
        String query = "SELECT * FROM venta;";
        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
        ) {
            while (rs.next()) {
                Venta ve = new Venta();
                ve.setId(rs.getInt("id"));
                ve.setIdFuncion(rs.getInt("id_funcion"));
                ve.setCliente(rs.getString("cliente"));
                ve.setCantidadAsientos(rs.getInt("cantidad_asientos"));
                ve.setTotalPagar(rs.getDouble("total_pagar"));
                ve.setFechaVenta(rs.getString("fecha_venta"));
                lista.add(ve);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar venta:" + e.getMessage());
        }
        return lista;
    }
}
