package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IMetricaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Metrica;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * MEJORA 10: numeros de la pantalla de Inicio.
 *
 * Se obtienen con UNA sola consulta. Cada subconsulta entre parentesis
 * cuenta algo distinto (peliculas, salas, clientes, funciones de hoy,
 * boletos de hoy e ingresos de hoy) y el resultado es una sola fila.
 *
 * "fecha de venta" es la misma que usa el cierre de caja; las ventas viejas
 * sin esa fecha no cuentan en los numeros del dia.
 */
public class MetricaDaoImpl implements IMetricaDao {

    @Override
    public Metrica obtener() {
        Metrica m = new Metrica();
        String hoy = LocalDate.now().toString();

        String query = "SELECT "
                + " (SELECT COUNT(*) FROM pelicula) AS peliculas, "
                + " (SELECT COUNT(*) FROM sala) AS salas, "
                + " (SELECT COUNT(*) FROM sala WHERE UPPER(COALESCE(estado,'ACTIVA')) <> 'INACTIVA') AS salas_activas, "
                + " (SELECT COUNT(*) FROM cliente) AS clientes, "
                + " (SELECT COUNT(*) FROM funcion WHERE substr(fecha,1,10)=?) AS funciones_hoy, "
                + " (SELECT COUNT(*) FROM venta WHERE estado='activa' "
                + "     AND substr(COALESCE(NULLIF(fecha_venta,''),''),1,10)=?) AS boletos_hoy, "
                + " (SELECT COALESCE(SUM(total_pagar),0) FROM venta WHERE estado='activa' "
                + "     AND substr(COALESCE(NULLIF(fecha_venta,''),''),1,10)=?) AS ingresos_hoy";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            //la fecha de hoy se usa tres veces: funciones, boletos e ingresos
            st.setString(1, hoy);
            st.setString(2, hoy);
            st.setString(3, hoy);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    m.setPeliculas(rs.getInt("peliculas"));
                    m.setSalas(rs.getInt("salas"));
                    m.setSalasActivas(rs.getInt("salas_activas"));
                    m.setClientes(rs.getInt("clientes"));
                    m.setFuncionesHoy(rs.getInt("funciones_hoy"));
                    m.setBoletosHoy(rs.getInt("boletos_hoy"));
                    m.setIngresosHoy(rs.getDouble("ingresos_hoy"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular las metricas: " + e.getMessage(), e);
        }
        return m;
    }
}
