package pe.edu.upeu.project_final.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Funcion {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty idPelicula = new SimpleIntegerProperty();
    private final IntegerProperty idSala = new SimpleIntegerProperty();
    private final StringProperty horario = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();

    public Funcion() {
    }

    public Funcion(int id, int idPelicula, int idSala, String horario, double precio) {
        setId(id);
        setIdPelicula(idPelicula);
        setIdSala(idSala);
        setHorario(horario);
        setPrecio(precio);
    }

    public final int getId() {
        return id.get();
    }

    public final void setId(int value) {
        id.set(value);
    }

    public final IntegerProperty idProperty() {
        return id;
    }


    public final int getIdPelicula() {
        return idPelicula.get();
    }

    public final void setIdPelicula(int value) {
        idPelicula.set(value);
    }

    public final IntegerProperty idPeliculaProperty() {
        return idPelicula;
    }


    public final int getIdSala() {
        return idSala.get();
    }

    public final void setIdSala(int value) {
        idSala.set(value);
    }

    public final IntegerProperty idSalaProperty() {
        return idSala;
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

}
