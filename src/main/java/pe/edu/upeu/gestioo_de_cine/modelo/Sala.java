package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sala {
    private final IntegerProperty idsala = new SimpleIntegerProperty(this, "idsala");
    private final IntegerProperty numero = new SimpleIntegerProperty(this, "numero");
    private final StringProperty tipoSala = new SimpleStringProperty(this, "tipo");
    private final IntegerProperty capacidad = new SimpleIntegerProperty(this, "capacidad");
    private final StringProperty estado = new SimpleStringProperty(this, "estado");
    //cuadricula de la sala: filas x columnas define las butacas que se generan
    private final IntegerProperty filas = new SimpleIntegerProperty(this, "filas");
    private final IntegerProperty columnas = new SimpleIntegerProperty(this, "columnas");


    public Sala() {
    }
    public Sala(int idsala, int numero, String tipoSala,
                int capacidad, String estado) {
        setIdsala(idsala);
        setNumero(numero);
        setTipoSala(tipoSala);
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

    public final int getFilas() {
        return filas.get();
    }

    public final void setFilas(int value) {
        filas.set(value);
    }

    public final IntegerProperty filasProperty() {
        return filas;
    }

    public final int getColumnas() {
        return columnas.get();
    }

    public final void setColumnas(int value) {
        columnas.set(value);
    }

    public final IntegerProperty columnasProperty() {
        return columnas;
    }

    //cuadricula que se escribe en el formulario: 10 x 12 por ejemplo
    public String getCuadricula() {
        return getFilas() + " x " + getColumnas();
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

    //etiqueta que se muestra al usuario: "Sala 2 — 3D"
    public String getEtiqueta() {
        String tipoSala = getTipoSala() != null ? getTipoSala() : "";
        return "Sala " + getNumero() + " — " + tipoSala;
    }

    @Override
    public String toString() {
        return getEtiqueta();
    }

}
