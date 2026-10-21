// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.implementacion;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPLEMENTACIÓN DAO: ejecuta las operaciones SQL del contrato correspondiente.
 * Convierte objetos Java en parámetros SQL y filas SQL en objetos Java.
 * No controla los campos del formulario ni abre pantallas.
 */
public class VentaDaoImpl implements IVentaDao {

    // SQLite CURRENT_TIMESTAMP guarda UTC; las pantallas muestran la hora peruana.
    private static final ZoneId ZONA = ZoneId.of("America/Lima");
    private static final DateTimeFormatter SQL_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String SELECT = """
            SELECT v.id AS venta_id, v.cliente, v.cantidad_asientos,
                   v.total_pagar, v.fecha_venta,
                   f.id AS funcion_id, f.horario, f.precio,
                   p.id AS pelicula_id, p.titulo, p.genero,
                   p.duracion, p.clasificacion,
                   s.id AS sala_id, s.nombre AS sala_nombre,
                   s.tipo AS sala_tipo, s.capacidad
            FROM venta v
            JOIN funcion f ON v.id_funcion = f.id
            JOIN pelicula p ON f.id_pelicula = p.id
            JOIN sala s ON f.id_sala = s.id
            """;

    @Override
    /**
     * Inserta un registro utilizando los datos del objeto recibido.
     * Devuelve true si se escribió una fila; false si no se pudo registrar.
     */
    public boolean insert(Venta v) {
        if (v == null || v.getFuncion() == null || v.getCliente() == null
                || v.getCliente().isBlank() || v.getCantidadAsientos() <= 0
                || !Double.isFinite(v.getTotalPagar()) || v.getTotalPagar() <= 0) {
            System.err.println("Venta inválida: revise cliente, función, cantidad y total");
            return false;
        }
        String query = "INSERT INTO venta "
                + "(id_funcion, cliente, cantidad_asientos, total_pagar) "
                + "SELECT ?, ?, ?, ? WHERE EXISTS ("
                + "SELECT 1 FROM funcion f JOIN pelicula p ON p.id=f.id_pelicula "
                + "JOIN sala s ON s.id=f.id_sala WHERE f.id=?)";

        // try con recursos cierra automáticamente conexión y sentencias al terminar, incluso si hay error.
        try (Connection cn = ConexionDB.getConexion();
             // PreparedStatement prepara el SQL con parámetros; los ? reciben valores mediante setInt/setString/etc.
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setInt(5, v.getFuncion().getIdfuncion());

            // executeUpdate devuelve las filas afectadas; == 1 confirma una inserción, actualización o eliminación.
            return st.executeUpdate() == 1;

        // Aquí se atienden errores de JDBC/SQLite; getMessage() muestra el motivo.
        } catch (SQLException e) {
            System.err.println("Error al insertar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Actualiza el registro cuyo ID está en el objeto recibido.
     * Devuelve true si se modificó una fila; no crea un registro nuevo.
     */
    public boolean update(Venta v) {
        if (v == null || v.getFuncion() == null || v.getCliente() == null
                || v.getCliente().isBlank() || v.getCantidadAsientos() <= 0
                || !Double.isFinite(v.getTotalPagar()) || v.getTotalPagar() <= 0) {
            System.err.println("Venta inválida: revise cliente, función, cantidad y total");
            return false;
        }
        String query = "UPDATE venta SET id_funcion=?, cliente=?, "
                + "cantidad_asientos=?, total_pagar=? WHERE id=? AND EXISTS ("
                + "SELECT 1 FROM funcion f JOIN pelicula p ON p.id=f.id_pelicula "
                + "JOIN sala s ON s.id=f.id_sala WHERE f.id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getFuncion().getIdfuncion());
            st.setString(2, v.getCliente());
            st.setInt(3, v.getCantidadAsientos());
            st.setDouble(4, v.getTotalPagar());
            st.setInt(5, v.getIdventa());
            st.setInt(6, v.getFuncion().getIdfuncion());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Consulta un solo registro por su identificador.
     * Devuelve el objeto encontrado o null cuando no se obtiene un registro.
     */
    public Venta searchById(int id) {
        String query = SELECT + " WHERE v.id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            // executeQuery ejecuta un SELECT y devuelve las filas dentro de ResultSet.
            try (ResultSet rs = st.executeQuery()) {
                // Para una búsqueda individual basta comprobar si existe la primera fila.
                if (rs.next()) {
                    return leerVenta(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar venta: " + e.getMessage());
        }

        return null;
    }

    @Override
    /**
     * Intenta eliminar el registro indicado por el ID del objeto.
     * Los DAO de película, sala y función impiden borrar registros que aún se usan.
     */
    public boolean delete(Venta v) {
        String query = "DELETE FROM venta WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, v.getIdventa());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Consulta y devuelve todos los registros que recupera el DAO.
     * Devuelve una List; el controlador la transforma en ObservableList para la tabla.
     */
    public List<Venta> listAll() {
        List<Venta> lista = new ArrayList<>();

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(SELECT);
             ResultSet rs = st.executeQuery()) {

            // next() avanza a la siguiente fila; el bucle procesa todas las que devuelve el SELECT.
            while (rs.next()) {
                lista.add(leerVenta(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }

        return lista;
    }

    @Override
    /**
     * Consulta las ventas del día elegido en la zona America/Lima.
     * Convierte el inicio del día y el inicio del siguiente día a UTC.
     * Incluye desde el primer límite y excluye el segundo; no mezcla días.
     */
    public List<Venta> listByDate(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Seleccione una fecha");
        }
        // Inicio del día peruano convertido a UTC para compararlo con fecha_venta de SQLite.
        String desde = fecha.atStartOfDay(ZONA).withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime().format(SQL_FECHA);
        // Inicio del día siguiente: límite excluido para no contar ventas de otro día.
        String hasta = fecha.plusDays(1).atStartOfDay(ZONA).withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime().format(SQL_FECHA);

        String query = SELECT + " WHERE datetime(v.fecha_venta) >= datetime(?)"
                + " AND datetime(v.fecha_venta) < datetime(?)"
                + " ORDER BY v.fecha_venta, v.id";
        List<Venta> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, desde);
            st.setString(2, hasta);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(leerVenta(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo cargar el reporte: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Convierte la fila actual del ResultSet en un modelo y sus objetos relacionados.
     * El cursor ya debe estar situado sobre una fila mediante rs.next().
     * throws SQLException deja que el método que hizo la consulta gestione el error.
     */
    private Venta leerVenta(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();
        p.setIdpelicula(rs.getInt("pelicula_id"));
        p.setTitulo(rs.getString("titulo"));
        p.setGenero(rs.getString("genero"));
        p.setDuracion(rs.getInt("duracion"));
        p.setClasificacion(rs.getString("clasificacion"));

        Sala s = new Sala();
        s.setIdsala(rs.getInt("sala_id"));
        s.setNombre(rs.getString("sala_nombre"));
        s.setTipo(rs.getString("sala_tipo"));
        s.setCapacidad(rs.getInt("capacidad"));

        Funcion f = new Funcion();
        f.setIdfuncion(rs.getInt("funcion_id"));
        f.setHorario(rs.getString("horario"));
        f.setPrecio(rs.getDouble("precio"));
        f.setPelicula(p);
        f.setSala(s);

        Venta v = new Venta();
        v.setIdventa(rs.getInt("venta_id"));
        v.setCliente(rs.getString("cliente"));
        v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
        v.setTotalPagar(rs.getDouble("total_pagar"));
        String fechaSql = rs.getString("fecha_venta");
        if (fechaSql == null || fechaSql.isBlank()) {
            throw new SQLException("La venta " + v.getIdventa() + " no tiene fecha_venta");
        }
        try {
            // Interpreta el timestamp guardado por SQLite, que usa UTC.
            LocalDateTime fechaUtc = fechaSql.length() == 10
                    ? LocalDate.parse(fechaSql).atStartOfDay()
                    : LocalDateTime.parse(fechaSql.replace(' ', 'T'));
            // Convierte el mismo instante a Perú antes de separar su fecha y su hora.
            LocalDateTime fechaLocal = fechaUtc.atOffset(ZoneOffset.UTC)
                    .atZoneSameInstant(ZONA).toLocalDateTime();
            v.setFechaVenta(fechaLocal.toLocalDate());
            v.setHoraVenta(fechaLocal.toLocalTime());
        } catch (DateTimeParseException e) {
            throw new SQLException("Fecha de venta inválida: " + fechaSql, e);
        }
        v.setFuncion(f);

        return v;
    }
}
