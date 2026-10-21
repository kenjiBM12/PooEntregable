// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.modelo;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * MODELO: representa una venta: función, cliente, entradas, total, fecha y hora.
 * Guarda datos en memoria; no contiene consultas SQL ni abre ventanas.
 * Las propiedades JavaFX permiten que la interfaz observe sus valores.
 */
public class Venta {

    // Propiedad observable de un entero (int). final fija la referencia; el valor sí puede cambiar con set().
    private final IntegerProperty idventa =
            new SimpleIntegerProperty();

    // Asociación a otro modelo: se guarda el objeto; el DAO utiliza su ID al escribir SQL.
    private final ObjectProperty<Funcion> funcion =
            new SimpleObjectProperty<>();

    // Propiedad observable de un texto (String).
    private final StringProperty cliente =
            new SimpleStringProperty();

    // Propiedad observable de un entero (int). final fija la referencia; el valor sí puede cambiar con set().
    private final IntegerProperty cantidadAsientos =
            new SimpleIntegerProperty();

    // Propiedad observable de un número decimal (double).
    private final DoubleProperty totalPagar =
            new SimpleDoubleProperty();
    // Propiedad de fecha sin hora; permite trabajar como la Reserva del hotel.
    private final ObjectProperty<LocalDate> fechaVenta = new SimpleObjectProperty<>();
    // Propiedad de hora sin fecha, utilizada por el reporte diario.
    private final ObjectProperty<LocalTime> horaVenta = new SimpleObjectProperty<>();

    /**
     * Getter: lee el hora de venta en Perú guardado en la propiedad.
     */
    public final LocalTime getHoraVenta() {
        return horaVenta.get();
    }

    /**
     * Setter: asigna el hora de venta en Perú a la propiedad.
     */
    public final void setHoraVenta(LocalTime value) {
        horaVenta.set(value);
    }

    /**
     * Devuelve la propiedad observable del hora de venta en Perú.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final ObjectProperty<LocalTime> horaVentaProperty() {
        return horaVenta;
    }



    /**
     * Constructor: permite crear un objeto de esta clase.
     * Si recibe parámetros, asigna los datos iniciales mediante los setters.
     */
    public Venta() {
    }

    /**
     * Constructor: permite crear un objeto de esta clase.
     * Si recibe parámetros, asigna los datos iniciales mediante los setters.
     */
    public Venta(int idventa, Funcion funcion, String cliente,
                 int cantidadAsientos, double totalPagar, LocalDate fechaVenta) {
        setIdventa(idventa);
        setFuncion(funcion);
        setCliente(cliente);
        setCantidadAsientos(cantidadAsientos);
        setTotalPagar(totalPagar);
        setFechaVenta(fechaVenta);

    }

    /**
     * Getter: lee el fecha de venta en Perú guardado en la propiedad.
     */
    public final LocalDate getFechaVenta() {
        return fechaVenta.get();
    }

    /**
     * Setter: asigna el fecha de venta en Perú a la propiedad.
     */
    public final void setFechaVenta(LocalDate value) {
        fechaVenta.set(value);
    }

    /**
     * Devuelve la propiedad observable del fecha de venta en Perú.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final ObjectProperty<LocalDate> fechaVentaProperty() {
        return fechaVenta;
    }


    /**
     * Getter: lee el identificador de la venta guardado en la propiedad.
     */
    public final int getIdventa() {
        return idventa.get();
    }

    /**
     * Setter: asigna el identificador de la venta a la propiedad.
     */
    public final void setIdventa(int value) {
        idventa.set(value);
    }

    /**
     * Devuelve la propiedad observable del identificador de la venta.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final IntegerProperty idventaProperty() {
        return idventa;
    }

    /**
     * Getter: lee el función asociada guardado en la propiedad.
     */
    public final Funcion getFuncion() {
        return funcion.get();
    }

    /**
     * Setter: asigna el función asociada a la propiedad.
     */
    public final void setFuncion(Funcion value) {
        funcion.set(value);
    }

    /**
     * Devuelve la propiedad observable del función asociada.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final ObjectProperty<Funcion> funcionProperty() {
        return funcion;
    }

    /**
     * Getter: lee el nombre del cliente guardado en la propiedad.
     */
    public final String getCliente() {
        return cliente.get();
    }

    /**
     * Setter: asigna el nombre del cliente a la propiedad.
     */
    public final void setCliente(String value) {
        cliente.set(value);
    }

    /**
     * Devuelve la propiedad observable del nombre del cliente.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final StringProperty clienteProperty() {
        return cliente;
    }

    /**
     * Getter: lee el cantidad de entradas guardado en la propiedad.
     */
    public final int getCantidadAsientos() {
        return cantidadAsientos.get();
    }

    /**
     * Setter: asigna el cantidad de entradas a la propiedad.
     */
    public final void setCantidadAsientos(int value) {
        cantidadAsientos.set(value);
    }

    /**
     * Devuelve la propiedad observable del cantidad de entradas.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final IntegerProperty cantidadAsientosProperty() {
        return cantidadAsientos;
    }

    /**
     * Getter: lee el importe total de la venta guardado en la propiedad.
     */
    public final double getTotalPagar() {
        return totalPagar.get();
    }

    /**
     * Setter: asigna el importe total de la venta a la propiedad.
     */
    public final void setTotalPagar(double value) {
        totalPagar.set(value);
    }

    /**
     * Devuelve la propiedad observable del importe total de la venta.
     * La tabla puede observarla para reflejar cambios en ese valor.
     */
    public final DoubleProperty totalPagarProperty() {
        return totalPagar;
    }

}
