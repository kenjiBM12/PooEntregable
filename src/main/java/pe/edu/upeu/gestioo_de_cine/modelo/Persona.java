package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Persona {
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty apellidos = new SimpleStringProperty();
    private final StringProperty dni = new SimpleStringProperty();

    public Persona() {
    }
    public Persona(String nombre,String apellidos,String dni){
        setNombre(nombre);
        setApellidos(apellidos);
        setDni(dni);
    }

    public final String getDni() {
        return dni.get();
    }

    public final void setDni(String value) {
        dni.set(value);
    }

    public final StringProperty dniProperty() {
        return dni;
    }


    public final String getApellidos() {
        return apellidos.get();
    }

    public final void setApellidos(String value) {
        apellidos.set(value);
    }

    public final StringProperty apellidosProperty() {
        return apellidos;
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


}

