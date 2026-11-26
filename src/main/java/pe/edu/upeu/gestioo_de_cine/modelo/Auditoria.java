package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * MEJORA 9: una fila de la bitacora de auditoria.
 *
 * Guarda quien hizo que y cuando: ingresos al sistema, ventas, anulaciones
 * y restauraciones de respaldo. Se muestran en una tabla de solo lectura,
 * por eso los campos son propiedades de JavaFX (la tabla avisa sola).
 */
public class Auditoria {

    private final IntegerProperty id = new SimpleIntegerProperty();
    //fecha y hora del movimiento (texto yyyy-MM-dd HH:mm:ss)
    private final StringProperty fecha = new SimpleStringProperty();
    private final IntegerProperty idUsuario = new SimpleIntegerProperty();
    //nombre de usuario que hizo el movimiento (o "sistema")
    private final StringProperty usuario = new SimpleStringProperty();
    //tipo corto del movimiento: LOGIN, VENTA, ANULACION, RESPALDO...
    private final StringProperty accion = new SimpleStringProperty();
    //explicacion larga del movimiento
    private final StringProperty detalle = new SimpleStringProperty();

    public Auditoria() {
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getFecha() {
        return fecha.get();
    }

    public void setFecha(String value) {
        fecha.set(value);
    }

    public StringProperty fechaProperty() {
        return fecha;
    }

    public int getIdUsuario() {
        return idUsuario.get();
    }

    public void setIdUsuario(int value) {
        idUsuario.set(value);
    }

    public IntegerProperty idUsuarioProperty() {
        return idUsuario;
    }

    public String getUsuario() {
        return usuario.get();
    }

    public void setUsuario(String value) {
        usuario.set(value);
    }

    public StringProperty usuarioProperty() {
        return usuario;
    }

    public String getAccion() {
        return accion.get();
    }

    public void setAccion(String value) {
        accion.set(value);
    }

    public StringProperty accionProperty() {
        return accion;
    }

    public String getDetalle() {
        return detalle.get();
    }

    public void setDetalle(String value) {
        detalle.set(value);
    }

    public StringProperty detalleProperty() {
        return detalle;
    }
}
