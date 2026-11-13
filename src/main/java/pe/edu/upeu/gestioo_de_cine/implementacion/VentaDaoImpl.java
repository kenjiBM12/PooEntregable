package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    private static final String QUERY_BASE =
            " SELECT v.idventa,v.id_funcion,v.cliente,v.cantidad_asientos, " +
            " v.total_pagar,v.fecha_venta, " +
            " f.horario,f.precio,f.id_pelicula,f.id_sala, " +
            " p.nombre AS nombre_pelicula,p.numero AS numero_pelicula, " +
            " p.duracion,p.categoria,p.clasificacion, " +
            " s.nombre AS nombre_sala,s.numero AS numero_sala,s.tipo,s.capacidad,s.estado " +
            " FROM venta v " +
            " LEFT JOIN funcion f ON v.id_funcion = f.idfuncion " +
            " LEFT JOIN pelicula p ON f.id_pelicula = p.idpelicula " +
            " LEFT JOIN sala s ON f.id_sala = s.idsala ";

    @Override
    public boolean insert(Venta v) {
        boolean flag = false;
        String query = "INSERT INTO venta(id_funcion,cliente,cantidad_asientos,total_pagar,fecha_venta)" +
                "VALUES(?,?,?,?,?);";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            //LocalDate.toString devuelve "yyyy-MM-dd"
            st.setString(5, v.getFechaVenta().toString());
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
        String query = " UPDATE venta SET id_funcion=?, cliente=?, cantidad_asientos=?, " +
                " total_pagar=?, fecha_venta=? WHERE idventa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setString(5, v.getFechaVenta().toString());
            st.setInt(6, v.getIdventa());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar venta:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Venta searchById(int id) {
        Venta venta = null;
        String query = QUERY_BASE + " WHERE v.idventa=?;";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    venta = construirVenta(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar venta por Id:" + e.getMessage());
        }
        return venta;
    }

    @Override
    public boolean delete(Venta v) {
        boolean flag = false;
        String query = "DELETE FROM venta WHERE idventa=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getIdventa());
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
        String query = QUERY_BASE + " ORDER BY v.idventa DESC";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(construirVenta(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas:" + e.getMessage());
        }
        return lista;
    }

    private Venta construirVenta(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();
        p.setidpelicula(rs.getInt("id_pelicula"));
        p.setNumero(rs.getInt("numero_pelicula"));
        p.setNombre(rs.getString("nombre_pelicula"));
        p.setDuracion(rs.getString("duracion"));
        p.setCategoria(rs.getString("categoria"));
        p.setClasificacion(rs.getString("clasificacion"));

        Sala s = new Sala();
        s.setIdsala(rs.getInt("id_sala"));
        s.setNumero(rs.getInt("numero_sala"));
        s.setNombre(rs.getString("nombre_sala"));
        s.setTipo(rs.getString("tipo"));
        s.setCapacidad(rs.getInt("capacidad"));
        s.setEstado(rs.getString("estado"));

        Funcion f = new Funcion();
        f.setIdfuncion(rs.getInt("id_funcion"));
        f.setHorario(rs.getString("horario"));
        f.setPrecio(rs.getDouble("precio"));
        f.setPelicula(p);
        f.setSala(s);

        Venta v = new Venta();
        v.setIdventa(rs.getInt("idventa"));
        v.setCliente(rs.getString("cliente"));
        v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
        v.setTotalPagar(rs.getDouble("total_pagar"));
        v.setFechaVenta(parseFecha(rs.getString("fecha_venta")));
        v.setFuncion(f);
        return v;
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha.substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }
}
