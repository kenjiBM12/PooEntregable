package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Butaca {
    private final IntegerProperty idbutaca = new SimpleIntegerProperty();
    private final IntegerProperty idSala = new SimpleIntegerProperty();
    private final IntegerProperty fila = new SimpleIntegerProperty();
    private final IntegerProperty numero = new SimpleIntegerProperty();
    private final BooleanProperty ocupada = new SimpleBooleanProperty();

    public Butaca() {
    }

    public Butaca(int fila, int numero) {
        setFila(fila);
        setNumero(numero);
    }

    public final int getIdbutaca() {
        return idbutaca.get();
    }

    public final void setIdbutaca(int value) {
        idbutaca.set(value);
    }

    public final IntegerProperty idbutacaProperty() {
        return idbutaca;
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

    public final int getFila() {
        return fila.get();
    }

    public final void setFila(int value) {
        fila.set(value);
    }

    public final IntegerProperty filaProperty() {
        return fila;
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

    public final boolean isOcupada() {
        return ocupada.get();
    }

    public final void setOcupada(boolean value) {
        ocupada.set(value);
    }

    public final BooleanProperty ocupadaProperty() {
        return ocupada;
    }

    @Override
    public String toString() {
        return "Fila " + getFila() + " - Butaca " + getNumero();
    }
}
