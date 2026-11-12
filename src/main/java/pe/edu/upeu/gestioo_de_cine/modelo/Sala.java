package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sala {
    private final IntegerProperty idsala = new SimpleIntegerProperty(this, "idsala");
    private final IntegerProperty numero = new SimpleIntegerProperty(this, "numero");
    private final StringProperty nombre = new SimpleStringProperty(this, "nombre");
    private final StringProperty tipo = new SimpleStringProperty(this, "tipo");
    private final IntegerProperty capacidad = new SimpleIntegerProperty(this, "capacidad");
    private final StringProperty estado = new SimpleStringProperty(this, "estado");


    public Sala() {
    }
    public Sala(int idsala, int numero, String nombre, String tipo,
                int capacidad, String estado) {
        setIdsala(idsala);
        setNumero(numero);
        setNombre(nombre);
        setTipo(tipo);
        setCapacidad(capacidad);
        setEstado(estado);
    }
    public final String getEstado() {
        return estado.get();
    }

    public final void setEstado(String value) {
        estado.set(value);
    }

    public final StringProperty estadoProperty() {
        return estado;
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

    public final String getTipo() {
        return tipo.get();
    }

    public final void setTipo(String value) {
        tipo.set(value);
    }

    public final StringProperty tipoProperty() {
        return tipo;
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

    public final int getNumero() {
        return numero.get();
    }

    public final void setNumero(int value) {
        numero.set(value);
    }

    public final IntegerProperty numeroProperty() {
        return numero;
    }

    public final int getIdsala() {
        return idsala.get();
    }

    public final void setIdsala(int value) {
        idsala.set(value);
    }

    public final IntegerProperty idsalaProperty() {
        return idsala;
    }

}