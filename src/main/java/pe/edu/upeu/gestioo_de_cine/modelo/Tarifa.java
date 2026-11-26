package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * MEJORA 4: tarifa de entrada por tipo de sala y dia de la semana.
 *
 * diaSemana = 0 significa "todos los dias" (tarifa base). Los valores 1..7
 * son lunes..domingo y permiten una promocion, por ejemplo martes mas barato.
 * El codigo del sistema usa esta tabla para proponer el precio de la funcion
 * en lugar de escribirlo a mano en cada una.
 *
 * Como el resto de modelos, usa propiedades de JavaFX (getX/setX y xProperty).
 */
public class Tarifa {

    private final IntegerProperty idtarifa = new SimpleIntegerProperty();
    private final StringProperty tipoSala = new SimpleStringProperty();
    private final IntegerProperty diaSemana = new SimpleIntegerProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();

    public Tarifa() {
    }

    public Tarifa(int idtarifa, String tipoSala, int diaSemana, double precio) {
        setIdtarifa(idtarifa);
        setTipoSala(tipoSala);
        setDiaSemana(diaSemana);
        setPrecio(precio);
    }

    public final int getIdtarifa() {
        return idtarifa.get();
    }

    public final void setIdtarifa(int value) {
        idtarifa.set(value);
    }

    public final IntegerProperty idtarifaProperty() {
        return idtarifa;
    }

    public final String getTipoSala() {
        return tipoSala.get();
    }

    public final void setTipoSala(String value) {
        tipoSala.set(value);
    }

    public final StringProperty tipoSalaProperty() {
        return tipoSala;
    }

    public final int getDiaSemana() {
        return diaSemana.get();
    }

    public final void setDiaSemana(int value) {
        diaSemana.set(value);
    }

    public final IntegerProperty diaSemanaProperty() {
        return diaSemana;
    }

    public final double getPrecio() {
        return precio.get();
    }

    public final void setPrecio(double value) {
        precio.set(value);
    }

    public final DoubleProperty precioProperty() {
        return precio;
    }

    /** Nombre del dia para mostrarlo en una tabla. */
    public String getDiaTexto() {
        switch (getDiaSemana()) {
            case 1: return "Lunes";
            case 2: return "Martes";
            case 3: return "Miércoles";
            case 4: return "Jueves";
            case 5: return "Viernes";
            case 6: return "Sábado";
            case 7: return "Domingo";
            default: return "Todos los días";
        }
    }

    @Override
    public String toString() {
        return getTipoSala() + " - " + getDiaTexto() + " - S/ "
                + String.format("%.2f", getPrecio());
    }
}
