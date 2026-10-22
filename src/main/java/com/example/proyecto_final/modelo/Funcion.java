package com.example.proyecto_final.modelo;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Funcion {

    private final IntegerProperty idfuncion =
            new SimpleIntegerProperty();

    private final ObjectProperty<Pelicula> pelicula =
            new SimpleObjectProperty<>();

    private final ObjectProperty<Sala> sala =
            new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDate> horario =
            new SimpleObjectProperty<>();

    private final DoubleProperty precio =
            new SimpleDoubleProperty();

    public Funcion() {
    }

public Funcion(int idfuncion, Pelicula pelicula, Sala sala,
               LocalDate horario, double precio){
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

    public final LocalDate getHorario() {
        return horario.get();
    }

    public final void setHorario(LocalDate value) {
        horario.set(value);
    }

    public final ObjectProperty<LocalDate> horarioProperty() {
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
