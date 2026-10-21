// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.interfaces;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.modelo.Venta;

import java.util.List;
import java.time.LocalDate;

/**
 * INTERFAZ DAO: contrato de las operaciones de acceso a datos.
 * Declara qué puede hacerse; la clase DaoImpl contiene el SQL.
 * DAO significa Data Access Object (objeto de acceso a datos).
 */
public interface IVentaDao {

    /**
     * Inserta un registro utilizando los datos del objeto recibido.
     * Devuelve true si se escribió una fila; false si no se pudo registrar.
     */
    boolean insert(Venta v);

    /**
     * Actualiza el registro cuyo ID está en el objeto recibido.
     * Devuelve true si se modificó una fila; no crea un registro nuevo.
     */
    boolean update(Venta v);

    /**
     * Consulta un solo registro por su identificador.
     * Devuelve el objeto encontrado o null cuando no se obtiene un registro.
     */
    Venta searchById(int id);

    /**
     * Intenta eliminar el registro indicado por el ID del objeto.
     * Los DAO de película, sala y función impiden borrar registros que aún se usan.
     */
    boolean delete(Venta v);

    /**
     * Consulta y devuelve todos los registros que recupera el DAO.
     * Devuelve una List; el controlador la transforma en ObservableList para la tabla.
     */
    List<Venta> listAll();

    /**
     * Consulta las ventas del día elegido en la zona America/Lima.
     * Convierte el inicio del día y el inicio del siguiente día a UTC.
     * Incluye desde el primer límite y excluye el segundo; no mezcla días.
     */
    List<Venta> listByDate(LocalDate fecha);
}
