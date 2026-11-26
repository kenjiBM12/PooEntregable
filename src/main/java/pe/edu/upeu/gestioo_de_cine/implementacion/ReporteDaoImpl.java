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

/**
 * MEJORA 3: cierre de caja con SQL de agregacion.
 *
 * Se usa una sola consulta con SUM y CASE para obtener cuatro numeros:
 *   - cuantos boletos estan activos
 *   - cuantos boletos estan anulados
 *   - cuanto dinero suman los activos
 *   - cuanto dinero suman los anulados
 *
 * "CASE WHEN ... THEN 1 ELSE 0 END" es un if dentro del SQL: suma 1 solo
 * cuando la condicion se cumple. Asi no hace falta traer todas las ventas
 * a Java y contarlas con un for.
 *
 * MEJORA 2: ademas se hace un segundo GROUP BY por forma de pago, para que
 * el administrador vea cuanto dinero entro en efectivo, con tarjeta o por
 * Yape, y pueda cuadrar la caja fisica.
 */
public class ReporteDaoImpl implements IReporteDao {

    @Override
    public Reporte generar(LocalDate desde, LocalDate hasta) {
        Reporte reporte = new Reporte();

        //si el controlador no manda fechas se usa un rango que incluye todo
        String fechaDesde = desde != null ? desde.toString() : "0000-01-01";
        String fechaHasta = hasta != null ? hasta.toString() : "9999-12-31";
        reporte.setDesde(fechaDesde);
        reporte.setHasta(fechaHasta);

        //CORREGIDO: el cierre de caja usa la FECHA DE VENTA (cuando entro el
        //dinero), no la fecha de la funcion. Antes una entrada vendida hoy para
        //una funcion del mes siguiente no aparecia en el cierre de hoy.
        //substr(...,1,10) toma solo "yyyy-MM-dd" por si el dato trae hora.
        //Las ventas muy viejas sin fecha_venta usan la fecha de la funcion como
        //respaldo para no desaparecer del reporte.
        String query = "SELECT "
                + " SUM(CASE WHEN v.estado='activa' THEN 1 ELSE 0 END) AS activos, "
                + " SUM(CASE WHEN v.estado='anulada' THEN 1 ELSE 0 END) AS anulados, "
                + " COALESCE(SUM(CASE WHEN v.estado='activa' THEN v.total_pagar ELSE 0 END),0) AS ingresos, "
                + " COALESCE(SUM(CASE WHEN v.estado='anulada' THEN v.total_pagar ELSE 0 END),0) AS anulado_monto "
                + " FROM venta v "
                + " LEFT JOIN funcion f ON v.id_funcion = f.idfuncion "
                + " WHERE substr(COALESCE(NULLIF(v.fecha_venta,''), f.fecha),1,10) BETWEEN ? AND ?";

        //la conexion es compartida: solo se cierra el PreparedStatement
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
            //igual que los demas DAO: el error se avisa, no se devuelve un reporte vacio
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }

        //MEJORA 2: el desglose por forma de pago se calcula aparte para no
        //complicar la consulta de totales
        reporte.setPorMetodoPago(desglosePorMetodo(fechaDesde, fechaHasta));
        return reporte;
    }

    /**
     * MEJORA 2: agrupa las ventas ACTIVAS del rango por forma de pago.
     * Las ventas viejas sin metodo_pago se cuentan como EFECTIVO para que el
     * dinero no se pierda del cierre, y las anuladas no aparecen porque no
     * representan dinero en caja.
     */
    private List<ResumenPago> desglosePorMetodo(String fechaDesde, String fechaHasta) {
        List<ResumenPago> lista = new ArrayList<>();
        //CORREGIDO: el WHERE exige estado='activa'. Si no, una venta anulada
        //creaba una fila con 0 boletos (por ejemplo YAPE 0) y ensuciaba la caja.
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
            //el desglose es informativo: si falla, el reporte igual se muestra
            System.out.println("No se pudo calcular el desglose por forma de pago: "
                    + e.getMessage());
        }
        return lista;
    }
}
