// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.implementacion;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.interfaces.ISalaDao;
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
public class SalaDaoImpl implements ISalaDao {

    @Override
    /**
     * Inserta un registro utilizando los datos del objeto recibido.
     * Devuelve true si se escribió una fila; false si no se pudo registrar.
     */
    public boolean insert(Sala s) {
        String query = "INSERT INTO sala (nombre, tipo, capacidad) "
                + "VALUES (?, ?, ?)";

        // try con recursos cierra automáticamente conexión y sentencias al terminar, incluso si hay error.
        try (Connection cn = ConexionDB.getConexion();
             // PreparedStatement prepara el SQL con parámetros; los ? reciben valores mediante setInt/setString/etc.
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());

            // executeUpdate devuelve las filas afectadas; == 1 confirma una inserción, actualización o eliminación.
            return st.executeUpdate() == 1;

        // Aquí se atienden errores de JDBC/SQLite; getMessage() muestra el motivo.
        } catch (SQLException e) {
            System.err.println("Error al insertar sala: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Actualiza el registro cuyo ID está en el objeto recibido.
     * Devuelve true si se modificó una fila; no crea un registro nuevo.
     */
    public boolean update(Sala s) {
        String query = "UPDATE sala SET nombre=?, tipo=?, capacidad=? "
                + "WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setString(1, s.getNombre());
            st.setString(2, s.getTipo());
            st.setInt(3, s.getCapacidad());
            st.setInt(4, s.getIdsala());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al actualizar sala: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Consulta un solo registro por su identificador.
     * Devuelve el objeto encontrado o null cuando no se obtiene un registro.
     */
    public Sala searchById(int id) {
        String query = "SELECT * FROM sala WHERE id=?";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            // executeQuery ejecuta un SELECT y devuelve las filas dentro de ResultSet.
            try (ResultSet rs = st.executeQuery()) {
                // Para una búsqueda individual basta comprobar si existe la primera fila.
                if (rs.next()) {
                    return leerSala(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar sala: " + e.getMessage());
        }

        return null;
    }

    @Override
    /**
     * Intenta eliminar el registro indicado por el ID del objeto.
     * Los DAO de película, sala y función impiden borrar registros que aún se usan.
     */
    public boolean delete(Sala s) {
        String query = "DELETE FROM sala WHERE id=? AND NOT EXISTS "
                + "(SELECT 1 FROM funcion WHERE id_sala=sala.id)";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, s.getIdsala());

            return st.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar sala: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**
     * Consulta y devuelve todos los registros que recupera el DAO.
     * Devuelve una List; el controlador la transforma en ObservableList para la tabla.
     */
    public List<Sala> listAll() {
        List<Sala> lista = new ArrayList<>();
        String query = "SELECT * FROM sala";

        try (Connection cn = ConexionDB.getConexion();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            // next() avanza a la siguiente fila; el bucle procesa todas las que devuelve el SELECT.
            while (rs.next()) {
                lista.add(leerSala(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar salas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Convierte la fila actual del ResultSet en un modelo y sus objetos relacionados.
     * El cursor ya debe estar situado sobre una fila mediante rs.next().
     * throws SQLException deja que el método que hizo la consulta gestione el error.
     */
    private Sala leerSala(ResultSet rs) throws SQLException {
        Sala s = new Sala();

        s.setIdsala(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setTipo(rs.getString("tipo"));
        s.setCapacidad(rs.getInt("capacidad"));

        return s;
    }
}
