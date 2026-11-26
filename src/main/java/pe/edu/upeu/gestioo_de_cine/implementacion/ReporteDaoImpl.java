package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IReporteDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Reporte;
import pe.edu.upeu.gestioo_de_cine.modelo.ResumenPago;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDaoImpl implements IReporteDao {

    @Override
    public Reporte generar(LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte();

        String fechaDesde = desde != null ? desde.toString() : "0000-01-01";
        String fechaHasta = hasta != null ? hasta.toString() : "9999-12-31";
        reporte.setDesde(fechaDesde);
        reporte.setHasta(fechaHasta);

        String query = "SELECT "
                + " SUM(CASE WHEN v.estado='activa' THEN 1 ELSE 0 END) AS activos, "
                + " SUM(CASE WHEN v.estado='anulada' THEN 1 ELSE 0 END) AS anulados, "
                + " COALESCE(SUM(CASE WHEN v.estado='activa' THEN v.total_pagar ELSE 0 END),0) AS ingresos, "
                + " COALESCE(SUM(CASE WHEN v.estado='anulada' THEN v.total_pagar ELSE 0 END),0) AS anulado_monto "
                + " FROM venta v "
                + " LEFT JOIN funcion f ON v.id_funcion = f.idfuncion "
                + " WHERE substr(COALESCE(NULLIF(v.fecha_venta,''), f.fecha),1,10) BETWEEN ? AND ?";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, fechaDesde);
            st.setString(2, fechaHasta);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    reporte.setBoletosActivos(rs.getInt("activos"));
                    reporte.setBoletosAnulados(rs.getInt("anulados"));
                    reporte.setIngresos(rs.getDouble("ingresos"));
                    reporte.setMontoAnulado(rs.getDouble("anulado_monto"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }

        reporte.setPorMetodoPago(desglosePorMetodo(fechaDesde, fechaHasta));
        return reporte;
    }

    private List<ResumenPago> desglosePorMetodo(String fechaDesde, String fechaHasta) {
        List<ResumenPago> lista = new ArrayList<>();
        String query = "SELECT UPPER(COALESCE(NULLIF(TRIM(v.metodo_pago),''),'EFECTIVO')) AS metodo, "
                + " COUNT(*) AS boletos, "
                + " COALESCE(SUM(v.total_pagar),0) AS monto "
                + " FROM venta v "
                + " LEFT JOIN funcion f ON v.id_funcion = f.idfuncion "
                + " WHERE v.estado='activa' "
                + " AND substr(COALESCE(NULLIF(v.fecha_venta,''), f.fecha),1,10) BETWEEN ? AND ? "
                + " GROUP BY metodo ORDER BY monto DESC, metodo";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, fechaDesde);
            st.setString(2, fechaHasta);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ResumenPago(rs.getString("metodo"),
                            rs.getInt("boletos"), rs.getDouble("monto")));
                }
            }
        } catch (SQLException e) {
            System.out.println("No se pudo calcular el desglose por forma de pago: "
                    + e.getMessage());
        }
        return lista;
    }
}
