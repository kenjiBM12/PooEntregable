package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pelicula {
    private final IntegerProperty idpelicula = new SimpleIntegerProperty();
    private final IntegerProperty numero = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty categoria = new SimpleStringProperty();
    private final StringProperty duracion = new SimpleStringProperty();
    private final StringProperty clasificacion = new SimpleStringProperty();

    public Pelicula() {
    }

    public Pelicula(int idpelicula, int numero, String nombre,
                    String duracion, String categoria, String clasisficacion) {
        setidpelicula(idpelicula);
        setNumero(numero);
        setNombre(nombre);
        setDuracion(duracion);
        setCategoria(categoria);
        setClasificacion(clasisficacion);
    }



    public final String getClasificacion() {
        return clasificacion.get();
    }

    public final void setClasificacion(String value) {
        clasificacion.set(value);
    }

    public final StringProperty clasificacionProperty() {
        return clasificacion;
    }

    public final String getDuracion() {
        return duracion.get();
    }

    public final void setDuracion(String value) {
        duracion.set(value);
    }

    public final StringProperty duracionProperty() {
        return duracion;
    }

    public final String getCategoria() {
        return categoria.get();
    }

    public final void setCategoria(String value) {
        categoria.set(value);
    }

    public final StringProperty categoriaProperty() {
        return categoria;
    }


    public final Integer getNumero() {
        return numero.get();
    }

    public final void setNumero(int value) {
        numero.set(value);
    }

    public final IntegerProperty numeroProperty() {
        return numero;
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

    public final Integer getidpelicula() {
        return idpelicula.get();
    }

    public final void setidpelicula(int value) {
        idpelicula.set(value);
    }

    public final IntegerProperty idpeliculaProperty() {
        return idpelicula;
    }

}
