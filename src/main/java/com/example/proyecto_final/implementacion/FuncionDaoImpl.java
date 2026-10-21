// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.implementacion;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPLEMENTACIÓN DAO: ejecuta las operaciones SQL del contrato correspondiente.
 * Convierte objetos Java en parámetros SQL y filas SQL en objetos Java.
 * No controla los campos del formulario ni abre pantallas.
 */
public class FuncionDaoImpl implements IFuncionDao {

    private static final String SELECT = """
            SELECT f.id AS funcion_id, f.horario, f.precio,
                   p.id AS pelicula_id, p.titulo, p.genero,
                   p.duracion, p.clasificacion,
                   s.id AS sala_id, s.nombre AS sala_nombre,
                   s.tipo AS sala_tipo, s.capacidad
            FROM funcion f
            JOIN pelicula p ON f.id_pelicula = p.id
            JOIN sala s ON f.id_sala = s.id
            """;

    @Override
    /**
     * Inserta un registro utilizando los datos del objeto recibido.
     * Devuelve true si se escribió una fila; false si no se pudo registrar.
     */
    public boolean insert(Funcion f) {
        String query = "INSERT INTO funcion "
                + "(id_pelicula, id_sala, horario, precio) "
                + "SELECT ?, ?, ?, ? WHERE EXISTS (SELECT 1 FROM pelicula WHERE id=?) "
                + "AND EXISTS (SELECT 1 FROM sala WHERE id=?)";

        // try con recursos cierra automáticamente conexión y sentencias al terminar, incluso si hay error.
        try (Connection cn = ConexionDB.getConexion();
             // PreparedStatement prepara el SQL con parámetros; los ? reciben valores mediante setInt/setString/etc.
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getPelicula().getIdpelicula());
            st.setInt(6, f.getSala().getIdsala());

            // executeUpdate devuelve las filas afectadas; == 1 confirma una inserción, actualización o eliminación.
            return st.executeUpdate() == 1;

        // Aquí se atienden errores de JDBC/SQLite; getMessage() muestra el motivo.
        } catch (SQLException e) {
            System.err.println("Error al insertar función: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Actualiza el registro cuyo ID está en el objeto recibido.
     * Devuelve true si se modificó una fila; no crea un registro nuevo.
     */
    public boolean update(Funcion f) {
        String query = "UPDATE funcion SET id_pelicula=?, id_sala=?, "
                + "horario=?, precio=? WHERE id=? "
                + "AND EXISTS (SELECT 1 FROM pelicula WHERE id=?) "
                + "AND EXISTS (SELECT 1 FROM sala WHERE id=?)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getPelicula().getIdpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getIdfuncion());
            st.setInt(6, f.getPelicula().getIdpelicula());
            st.setInt(7, f.getSala().getIdsala());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar función: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Consulta un solo registro por su identificador.
     * Devuelve el objeto encontrado o null cuando no se obtiene un registro.
     */
    public Funcion searchById(int id) {
        String query = SELECT + " WHERE f.id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            // executeQuery ejecuta un SELECT y devuelve las filas dentro de ResultSet.
            try (ResultSet rs = st.executeQuery()) {
                // Para una búsqueda individual basta comprobar si existe la primera fila.
                if (rs.next()) {
                    return leerFuncion(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar función: " + e.getMessage());
        }

        return null;
    }

    @Override
    /**
     * Intenta eliminar el registro indicado por el ID del objeto.
     * Los DAO de película, sala y función impiden borrar registros que aún se usan.
     */
    public boolean delete(Funcion f) {
        String query = "DELETE FROM funcion WHERE id=? AND NOT EXISTS "
                + "(SELECT 1 FROM venta WHERE id_funcion=funcion.id)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, f.getIdfuncion());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar función: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Consulta y devuelve todos los registros que recupera el DAO.
     * Devuelve una List; el controlador la transforma en ObservableList para la tabla.
     */
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(SELECT);
             ResultSet rs = st.executeQuery()) {

            // next() avanza a la siguiente fila; el bucle procesa todas las que devuelve el SELECT.
            while (rs.next()) {
                lista.add(leerFuncion(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar funciones: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Convierte la fila actual del ResultSet en un modelo y sus objetos relacionados.
     * El cursor ya debe estar situado sobre una fila mediante rs.next().
     * throws SQLException deja que el método que hizo la consulta gestione el error.
     */
    private Funcion leerFuncion(ResultSet rs) throws SQLException {
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

        return f;
    }
}
