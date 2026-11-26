package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Venta {
    private final IntegerProperty idventa = new SimpleIntegerProperty();
    //referencia estable al cliente: se guarda su id, no solo el nombre
    private final IntegerProperty idCliente = new SimpleIntegerProperty();
    private final StringProperty cliente = new SimpleStringProperty();
    private final IntegerProperty cantidadAsientos = new SimpleIntegerProperty();
    private final DoubleProperty totalPagar = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> fechaVenta = new SimpleObjectProperty<>();
    //asociacion de la llave foranea
    private final ObjectProperty<Funcion> funcion = new SimpleObjectProperty<>();
    //butaca vendida y empleado que vendio
    private final IntegerProperty idButaca = new SimpleIntegerProperty();
    //objeto butaca solo para mostrar fila y numero en la tabla
    private final ObjectProperty<Butaca> butaca = new SimpleObjectProperty<>();
    private final IntegerProperty idUsuario = new SimpleIntegerProperty();
    private final StringProperty estado = new SimpleStringProperty("activa");
    //codigo de compra: es el mismo para todos los boletos de una misma compra
    private final StringProperty codigoCompra = new SimpleStringProperty();
    //dni del cliente y nombre del empleado: solo se muestran en la tabla (vienen del JOIN)
    private final StringProperty dniCliente = new SimpleStringProperty();
    private final StringProperty empleado = new SimpleStringProperty();
    //MEJORA 6: forma de pago (Efectivo / Tarjeta / Yape) y datos de la anulacion
    private final StringProperty metodoPago = new SimpleStringProperty("EFECTIVO");
    //texto armado para la tabla: "quien anulo - motivo". Vacio si sigue activa
    private final StringProperty anulacion = new SimpleStringProperty("");

    public Venta() {
    }

    public Venta(int idventa, Funcion funcion, String cliente, int cantidadAsientos,
                 double totalPagar, LocalDate fechaVenta) {
        setIdventa(idventa);
        setFuncion(funcion);
        setCliente(cliente);
        setCantidadAsientos(cantidadAsientos);
        setTotalPagar(totalPagar);
        setFechaVenta(fechaVenta);
    }

    public final int getIdventa() {
        return idventa.get();
    }

    public final void setIdventa(int value) {
        idventa.set(value);
    }

    public final IntegerProperty idventaProperty() {
        return idventa;
    }

    public final int getIdCliente() {
        return idCliente.get();
    }

    public final void setIdCliente(int value) {
        idCliente.set(value);
    }

    public final IntegerProperty idClienteProperty() {
        return idCliente;
    }

    public final String getCliente() {
        return cliente.get();
    }

    public final void setCliente(String value) {
        cliente.set(value);
    }

    public final StringProperty clienteProperty() {
        return cliente;
    }

    public final int getCantidadAsientos() {
        return cantidadAsientos.get();
    }

    public final void setCantidadAsientos(int value) {
        cantidadAsientos.set(value);
    }

    public final IntegerProperty cantidadAsientosProperty() {
        return cantidadAsientos;
    }

    public final double getTotalPagar() {
        return totalPagar.get();
    }

    public final void setTotalPagar(double value) {
        totalPagar.set(value);
    }

    public final DoubleProperty totalPagarProperty() {
        return totalPagar;
    }

    public final LocalDate getFechaVenta() {
        return fechaVenta.get();
    }

    public final void setFechaVenta(LocalDate value) {
        fechaVenta.set(value);
    }

    public final ObjectProperty<LocalDate> fechaVentaProperty() {
        return fechaVenta;
    }

    public final Funcion getFuncion() {
        return funcion.get();
    }

    public final void setFuncion(Funcion value) {
        funcion.set(value);
    }

    public final ObjectProperty<Funcion> funcionProperty() {
        return funcion;
    }

    public final int getIdButaca() {
        return idButaca.get();
    }

    public final void setIdButaca(int value) {
        idButaca.set(value);
    }

    public final IntegerProperty idButacaProperty() {
        return idButaca;
    }

    public final Butaca getButaca() {
        return butaca.get();
    }

    public final void setButaca(Butaca value) {
        butaca.set(value);
    }

    public final ObjectProperty<Butaca> butacaProperty() {
        return butaca;
    }

    public final int getIdUsuario() {
        return idUsuario.get();
    }

    public final void setIdUsuario(int value) {
        idUsuario.set(value);
    }

    public final IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public final String getEstado() {
        return estado.get();
    }

    public final void setEstado(String value) {
        estado.set(value);
    }

    public final StringProperty estadoProperty() {
        return estado;
    }

    public final String getCodigoCompra() {
        return codigoCompra.get();
    }

    public final void setCodigoCompra(String value) {
        codigoCompra.set(value);
    }

    public final StringProperty codigoCompraProperty() {
        return codigoCompra;
    }

    public final String getDniCliente() {
        return dniCliente.get();
    }

    public final void setDniCliente(String value) {
        dniCliente.set(value);
    }

    public final StringProperty dniClienteProperty() {
        return dniCliente;
    }

    public final String getEmpleado() {
        return empleado.get();
    }

    public final void setEmpleado(String value) {
        empleado.set(value);
    }

    public final StringProperty empleadoProperty() {
        return empleado;
    }

    public final String getMetodoPago() {
        return metodoPago.get();
    }

    public final void setMetodoPago(String value) {
        metodoPago.set(value);
    }

    public final StringProperty metodoPagoProperty() {
        return metodoPago;
    }

    public final String getAnulacion() {
        return anulacion.get();
    }

    public final void setAnulacion(String value) {
        anulacion.set(value);
    }

    public final StringProperty anulacionProperty() {
        return anulacion;
    }

    @Override
    public String toString() {
        return getCliente() + " - " + getFechaVenta();
    }
}
