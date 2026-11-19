package pe.edu.upeu.taller_colaborativo.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Funcion {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IntegerProperty idFuncion = new SimpleIntegerProperty();
    private final ObjectProperty<Pelicula> pelicula = new SimpleObjectProperty<>();
    private final ObjectProperty<Sala> sala = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final StringProperty hora = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();

    public Funcion() {
    }

    public Funcion(int idFuncion, Pelicula pelicula, Sala sala, LocalDate fecha,
                   String hora, double precio) {
        setIdFuncion(idFuncion);
        setPelicula(pelicula);
        setSala(sala);
        setFecha(fecha);
        setHora(hora);
        setPrecio(precio);
    }

    public final int getIdFuncion() {
        return idFuncion.get();
    }

    public final void setIdFuncion(int value) {
        idFuncion.set(value);
    }

    public final IntegerProperty idFuncionProperty() {
        return idFuncion;
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

    public final LocalDate getFecha() {
        return fecha.get();
    }

    public final void setFecha(LocalDate value) {
        fecha.set(value);
    }

    public final ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }

    public final String getHora() {
        return hora.get();
    }

    public final void setHora(String value) {
        hora.set(value);
    }

    public final StringProperty horaProperty() {
        return hora;
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

    // Texto de la funcion para tablas, alertas y el panel de inicio
    public String getDescripcion() {
        return getSala().getNombre() + " · " + getFecha().format(FORMATO_FECHA) + " · " + getHora();
    }
}
