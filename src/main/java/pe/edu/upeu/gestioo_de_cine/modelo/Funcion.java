package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Funcion {
    private final IntegerProperty idfuncion = new SimpleIntegerProperty();
    private final StringProperty horario = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();
    //asociacion de las llaves foraneas
    private final ObjectProperty<Pelicula> pelicula = new SimpleObjectProperty<>();
    private final ObjectProperty<Sala> sala = new SimpleObjectProperty<>();

    public Funcion() {
    }

    public Funcion(int idfuncion, Pelicula pelicula, Sala sala, String horario, double precio) {
        setIdfuncion(idfuncion);
        setPelicula(pelicula);
        setSala(sala);
        setHorario(horario);
        setPrecio(precio);
    }

    public final int getIdfuncion() {
        return idfuncion.get();
    }

    public final void setIdfuncion(int value) {
        idfuncion.set(value);
    }

    public final IntegerProperty idfuncionProperty() {
        return idfuncion;
    }

    public final String getHorario() {
        return horario.get();
    }

    public final void setHorario(String value) {
        horario.set(value);
    }

    public final StringProperty horarioProperty() {
        return horario;
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

    public final Pelicula getPelicula() {
        return pelicula.get();
    }

    public final void setPelicula(Pelicula value) {
        pelicula.set(value);
    }

    public final ObjectProperty<Pelicula> peliculaProperty() {
        return pelicula;
    }

    public final Sala getSala() {
        return sala.get();
    }

    public final void setSala(Sala value) {
        sala.set(value);
    }

    public final ObjectProperty<Sala> salaProperty() {
        return sala;
    }

    @Override
    public String toString() {
        String nombrePelicula = getPelicula() != null ? getPelicula().getNombre() : "Sin pelicula";
        return nombrePelicula + " - " + getHorario();
    }
}
