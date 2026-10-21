// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.interfaces;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.modelo.Sala;

import java.util.List;

/**
 * INTERFAZ DAO: contrato de las operaciones de acceso a datos.
 * Declara qué puede hacerse; la clase DaoImpl contiene el SQL.
 * DAO significa Data Access Object (objeto de acceso a datos).
 */
public interface ISalaDao {

    /**
     * Inserta un registro utilizando los datos del objeto recibido.
     * Devuelve true si se escribió una fila; false si no se pudo registrar.
     */
    boolean insert(Sala s);

    /**
     * Actualiza el registro cuyo ID está en el objeto recibido.
     * Devuelve true si se modificó una fila; no crea un registro nuevo.
     */
    boolean update(Sala s);

    /**
     * Consulta un solo registro por su identificador.
     * Devuelve el objeto encontrado o null cuando no se obtiene un registro.
     */
    Sala searchById(int id);

    /**
     * Intenta eliminar el registro indicado por el ID del objeto.
     * Los DAO de película, sala y función impiden borrar registros que aún se usan.
     */
    boolean delete(Sala s);

    /**
     * Consulta y devuelve todos los registros que recupera el DAO.
     * Devuelve una List; el controlador la transforma en ObservableList para la tabla.
     */
    List<Sala> listAll();
}
