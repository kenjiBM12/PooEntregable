package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IEstadisticaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Estadistica;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 3: consultas de agregacion para los gráficos de Reportes.
 * Se reusan los indices que ya existen sobre venta (funcion/butaca y codigo).
 */
public class EstadisticaDaoImpl implements IEstadisticaDao {

    @Override
    public List<Estadistica> ventasPorDia(LocalDate desde, LocalDate hasta) {
        List<Estadistica> lista = new ArrayList<>();
        String sql = "SELECT substr(fecha_venta,1,10) AS dia, COUNT(*) AS boletos, "
                + " SUM(total_pagar) AS monto FROM venta "
                + " WHERE estado='activa' AND substr(fecha_venta,1,10) BETWEEN ? AND ? "
                + " GROUP BY dia ORDER BY dia";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, desde.toString());
            ps.setString(2, hasta.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    //en el eje X se muestra solo el dia (dd/MM)
                    String dia = rs.getString("dia");
                    String etiqueta = dia != null && dia.length() >= 10
                            ? dia.substring(8, 10) + "/" + dia.substring(5, 7) : dia;
                    lista.add(new Estadistica(etiqueta, rs.getInt("boletos"), rs.getDouble("monto")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular las ventas por día: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Estadistica> topPeliculas(int limite) {
        List<Estadistica> lista = new ArrayList<>();
        String sql = "SELECT p.titulo, COUNT(*) AS boletos, SUM(v.total_pagar) AS monto "
                + " FROM venta v JOIN funcion f ON f.idfuncion = v.id_funcion "
                + " JOIN pelicula p ON p.idpelicula = f.id_pelicula "
                + " WHERE v.estado='activa' GROUP BY p.titulo "
                + " ORDER BY boletos DESC LIMIT ?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite <= 0 ? 5 : limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Estadistica(rs.getString("titulo"),
                            rs.getInt("boletos"), rs.getDouble("monto")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular el top de películas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Estadistica> ocupacionPorFuncion(int limite) {
        List<Estadistica> lista = new ArrayList<>();
        //la ocupacion es boletos activos / capacidad de la sala, en porcentaje
        String sql = "SELECT p.titulo, f.fecha, f.horario, s.capacidad, "
                + " COUNT(v.idventa) AS vendidos FROM funcion f "
                + " JOIN pelicula p ON p.idpelicula = f.id_pelicula "
                + " JOIN sala s ON s.idsala = f.id_sala "
                + " LEFT JOIN venta v ON v.id_funcion = f.idfuncion AND v.estado='activa' "
                + " GROUP BY f.idfuncion ORDER BY vendidos DESC LIMIT ?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite <= 0 ? 5 : limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int capacidad = rs.getInt("capacidad");
                    int vendidos = rs.getInt("vendidos");
                    double porcentaje = capacidad > 0 ? (vendidos * 100.0) / capacidad : 0;
                    String etiqueta = rs.getString("titulo") + " " + rs.getString("horario");
                    lista.add(new Estadistica(etiqueta, vendidos, porcentaje));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular la ocupación: " + e.getMessage(), e);
        }
        return lista;
    }
}
