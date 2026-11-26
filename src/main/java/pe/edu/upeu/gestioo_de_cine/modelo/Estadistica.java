package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * MEJORA 3: fila de un grafico de Reportes.
 *
 * Se usa para las tres consultas nuevas: ventas por dia, peliculas mas
 * vendidas y ocupacion por funcion. "etiqueta" es el texto del eje X,
 * "cantidad" los boletos y "valor" el dinero o el porcentaje, segun la consulta.
 *
 * Usa propiedades de JavaFX igual que los modelos de entidad, para que se
 * pueda enlazar en una tabla o en un grafico sin pasos extra.
 */
public class Estadistica {

    private final StringProperty etiqueta = new SimpleStringProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    private final DoubleProperty valor = new SimpleDoubleProperty();

    public Estadistica() {
    }

    public Estadistica(String etiqueta, int cantidad, double valor) {
        setEtiqueta(etiqueta);
        setCantidad(cantidad);
        setValor(valor);
    }

    public final String getEtiqueta() {
        return etiqueta.get();
    }

    public final void setEtiqueta(String value) {
        etiqueta.set(value);
    }

    public final StringProperty etiquetaProperty() {
        return etiqueta;
    }

    public final int getCantidad() {
        return cantidad.get();
    }

    public final void setCantidad(int value) {
        cantidad.set(value);
    }

    public final IntegerProperty cantidadProperty() {
        return cantidad;
    }

    public final double getValor() {
        return valor.get();
    }

    public final void setValor(double value) {
        valor.set(value);
    }

    public final DoubleProperty valorProperty() {
        return valor;
    }
}
