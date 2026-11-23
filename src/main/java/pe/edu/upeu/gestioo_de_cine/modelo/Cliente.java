package pe.edu.upeu.gestioo_de_cine.modelo;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {
    private final IntegerProperty idcliente = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty apellidos = new SimpleStringProperty();
    private final StringProperty dni = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty genero = new SimpleStringProperty();
    //telefono: se pide en la venta de entradas
    private final StringProperty telefono = new SimpleStringProperty();

    public Cliente() {}
    public Cliente(int idcliente, String nombre, String apellidos,
                   String dni, String email, String genero) {
        setIdcliente(idcliente);
        setNombre(nombre);
        setApellidos(apellidos);
        setDni(dni);
        setEmail(email);
        setGenero(genero);
    }

    public final int getIdcliente() {
        return idcliente.get();
    }

    public final void setIdcliente(int value) {
        idcliente.set(value);
    }

    public final IntegerProperty idclienteProperty() {
        return idcliente;
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


    public final String getGenero() {
        return genero.get();
    }

    public final void setGenero(String value) {
        genero.set(value);
    }

    public final StringProperty generoProperty() {
        return genero;
    }


    public final String getEmail() {
        return email.get();
    }

    public final void setEmail(String value) {
        email.set(value);
    }

    public final StringProperty emailProperty() {
        return email;
    }

    public final String getTelefono() {
        return telefono.get();
    }

    public final void setTelefono(String value) {
        telefono.set(value);
    }

    public final StringProperty telefonoProperty() {
        return telefono;
    }

}
