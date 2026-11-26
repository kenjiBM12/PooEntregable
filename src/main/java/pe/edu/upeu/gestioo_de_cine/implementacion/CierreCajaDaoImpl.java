package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.ICierreCajaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.CierreCaja;
import pe.edu.upeu.gestioo_de_cine.modelo.ResumenPago;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 1: DAO del cierre de caja.
 *
 * calcularDia agrupa los boletos por forma de pago y separa lo que se cobro
 * (estado 'activa') de lo que se devolvio (estado 'anulada') en ese dia.
 * La fecha del cierre es la fecha de VENTA, igual que en Reportes.
 */
public class CierreCajaDaoImpl implements ICierreCajaDao {

    @Override
    public List<ResumenPago> calcularDia(String fecha) {
        List<ResumenPago> lista = new ArrayList<>();
        String sql = "SELECT UPPER(COALESCE(NULLIF(TRIM(metodo_pago),''),'EFECTIVO')) AS metodo, "
                + " SUM(CASE WHEN estado='activa' THEN 1 ELSE 0 END) AS activos, "
                + " SUM(CASE WHEN estado='activa' THEN total_pagar ELSE 0 END) AS cobrado, "
                + " SUM(CASE WHEN estado<>'activa' THEN total_pagar ELSE 0 END) AS devuelto "
                + " FROM venta WHERE substr(fecha_venta,1,10)=? GROUP BY metodo ORDER BY metodo";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResumenPago r = new ResumenPago(rs.getString("metodo"),
                            rs.getInt("activos"), rs.getDouble("cobrado"));
                    r.setDevuelto(rs.getDouble("devuelto"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular el cierre del dia: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Guarda el arqueo del dia. La tabla tiene UNIQUE(fecha), asi que un
     * segundo cierre del mismo dia reemplaza al anterior (se corrige, no se
     * duplica). Queda registrado quien lo hizo y cuando.
     */
    @Override
    public boolean registrar(CierreCaja c) {
        String sql = "INSERT OR REPLACE INTO cierre_caja (fecha, monto_inicial, efectivo, "
                + "tarjeta, yape, otros, devuelto, total_esperado, total_contado, diferencia, "
                + "id_usuario, usuario, observacion, fecha_registro) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getFecha());
            ps.setDouble(2, c.getMontoInicial());
            ps.setDouble(3, c.getEfectivo());
            ps.setDouble(4, c.getTarjeta());
            ps.setDouble(5, c.getYape());
            ps.setDouble(6, c.getOtros());
            ps.setDouble(7, c.getDevuelto());
            ps.setDouble(8, c.getTotalEsperado());
            ps.setDouble(9, c.getTotalContado());
            ps.setDouble(10, c.getDiferencia());
            ps.setInt(11, c.getIdUsuario());
            ps.setString(12, c.getUsuario());
            ps.setString(13, c.getObservacion());
            ps.setString(14, c.getFechaRegistro() == null || c.getFechaRegistro().isBlank()
                    ? LocalDateTime.now().toString() : c.getFechaRegistro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el cierre de caja: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CierreCaja> listar(int limite) {
        List<CierreCaja> lista = new ArrayList<>();
        String sql = "SELECT * FROM cierre_caja ORDER BY fecha DESC LIMIT ?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite <= 0 ? 30 : limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(construir(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los cierres: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public CierreCaja buscarPorFecha(String fecha) {
        String sql = "SELECT * FROM cierre_caja WHERE fecha=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construir(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el cierre: " + e.getMessage(), e);
        }
        return null;
    }

    private CierreCaja construir(ResultSet rs) throws SQLException {
        CierreCaja c = new CierreCaja();
        c.setIdcierre(rs.getInt("idcierre"));
        c.setFecha(rs.getString("fecha"));
        c.setMontoInicial(rs.getDouble("monto_inicial"));
        c.setEfectivo(rs.getDouble("efectivo"));
        c.setTarjeta(rs.getDouble("tarjeta"));
        c.setYape(rs.getDouble("yape"));
        c.setOtros(rs.getDouble("otros"));
        c.setDevuelto(rs.getDouble("devuelto"));
        c.setTotalEsperado(rs.getDouble("total_esperado"));
        c.setTotalContado(rs.getDouble("total_contado"));
        c.setDiferencia(rs.getDouble("diferencia"));
        c.setIdUsuario(rs.getInt("id_usuario"));
        c.setUsuario(rs.getString("usuario"));
        c.setObservacion(rs.getString("observacion"));
        c.setFechaRegistro(rs.getString("fecha_registro"));
        return c;
    }

    /** Fecha de hoy en el formato que usa la tabla (yyyy-MM-dd). */
    public static String hoy() {
        return LocalDate.now().toString();
    }
}
