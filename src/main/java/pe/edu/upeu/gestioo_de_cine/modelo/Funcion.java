package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pe.edu.upeu.gestioo_de_cine.utils.Duracion;

public class Funcion {
    private final IntegerProperty idfuncion = new SimpleIntegerProperty();
    //fecha de la funcion en texto (yyyy-MM-dd): se muestra junto con la hora
    private final StringProperty fecha = new SimpleStringProperty();
    private final StringProperty horario = new SimpleStringProperty();
    private final DoubleProperty precio = new SimpleDoubleProperty();
    //asociacion de las llaves foraneas
    private final ObjectProperty<Pelicula> pelicula = new SimpleObjectProperty<>();
    private final ObjectProperty<Sala> sala = new SimpleObjectProperty<>();
    //MEJORA 7: 'activa' o 'cancelada'. Una funcion cancelada ya no se vende.
    private final StringProperty estado = new SimpleStringProperty("activa");

    public Funcion() {
    }

    public Funcion(int idfuncion, Pelicula pelicula, Sala sala, String horario, double precio) {
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

    public final String getHorario() {
        return horario.get();
    }

    public final void setHorario(String value) {
        horario.set(value);
    }

    public final StringProperty horarioProperty() {
        return horario;
    }

    public final String getFecha() {
        return fecha.get();
    }

    public final void setFecha(String value) {
        fecha.set(value);
    }

    public final StringProperty fechaProperty() {
        return fecha;
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

    public final String getEstado() {
        return estado.get();
    }

    public final void setEstado(String value) {
        estado.set(value == null || value.isBlank() ? "activa" : value);
    }

    public final StringProperty estadoProperty() {
        return estado;
    }

    /** true si la funcion fue cancelada (no se pueden vender entradas). */
    public boolean isCancelada() {
        return "cancelada".equalsIgnoreCase(getEstado());
    }

    /**
     * MEJORA 13: hora en que termina la funcion.
     * Se suma la duracion de la pelicula (ya normalizada a minutos) al horario
     * de inicio. Devuelve "" si falta el horario o la duracion.
     */
    public String getHoraFin() {
        if (getHorario() == null || getHorario().isBlank()) {
            return "";
        }
        int minutos = getPelicula() != null ? Duracion.aMinutos(getPelicula().getDuracion()) : 0;
        if (minutos <= 0) {
            return "";
        }
        try {
            String hora = getHorario().trim();
            if (hora.length() > 5) {
                hora = hora.substring(0, 5);
            }
            return java.time.LocalTime.parse(hora).plusMinutes(minutos).toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String toString() {
        String nombrePelicula = getPelicula() != null ? getPelicula().getTitulo() : "Sin pelicula";
        //se muestra tambien la fecha y la sala para no confundir funciones con la misma hora
        String fecha = getFecha() != null ? getFecha() : "";
        String sala = getSala() != null ? getSala().getEtiqueta() : "";
        return nombrePelicula + " - " + fecha + " " + getHorario() + (sala.isEmpty() ? "" : " - " + sala);
    }
}
