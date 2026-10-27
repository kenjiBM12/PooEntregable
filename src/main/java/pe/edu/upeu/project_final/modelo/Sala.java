package pe.edu.upeu.project_final.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sala {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty tipo = new SimpleStringProperty();
    private final IntegerProperty capacidad = new SimpleIntegerProperty();

    public Sala() {
    }

    public Sala(int id, String nombre, String tipo, int capacidad) {
        setId(id);
        setNombre(nombre);
        setTipo(tipo);
        setCapacidad(capacidad);
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


    public final String getNombre() {
        return nombre.get();
    }

    public final void setNombre(String value) {
        nombre.set(value);
    }

    public final StringProperty nombreProperty() {
        return nombre;
    }


    public final String getTipo() {
        return tipo.get();
    }

    public final void setTipo(String value) {
        tipo.set(value);
    }

    public final StringProperty tipoProperty() {
        return tipo;
    }


    public final int getCapacidad() {
        return capacidad.get();
    }

    public final void setCapacidad(int value) {
        capacidad.set(value);
    }

    public final IntegerProperty capacidadProperty() {
        return capacidad;
    }

}