package pe.edu.upeu.gestioo_de_cine.controller;

import java.time.LocalDate; // CAMBIO: para saber la fecha de una funcion nueva
import java.time.LocalTime; // CAMBIO: el horario se compara como hora, no como texto
import java.time.format.DateTimeParseException; // CAMBIO: para validar formato de hora
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IPeliculaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.Duracion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

public class FuncionFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtHorario, txtPrecio;
    @FXML
    private ComboBox<Pelicula> cbPelicula;
    @FXML
    private ComboBox<Sala> cbSala;
    //selector de fecha: la tabla funcion exige una fecha
    @FXML
    private DatePicker dpFecha;

    private Funcion funcion;
    private boolean saved = false;

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private final ISalaDao salaDao = new SalaDaoImpl();
    private final IFuncionDao funcionDao = new FuncionDaoImpl();

    @FXML
    public void initialize() {
        cbPelicula.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pelicula p) {
                return p == null ? "" : p.getTitulo() + " (" + p.getClasificacion() + ")";
            }

            @Override
            public Pelicula fromString(String string) {
                return null;
            }
        });

        cbSala.setConverter(new StringConverter<>() {
            @Override
            public String toString(Sala s) {
                return s == null ? "" : s.getEtiqueta();
            }

            @Override
            public Sala fromString(String string) {
                return null;
            }
        });

        cbPelicula.getItems().setAll(peliculaDao.listAll());
        //MEJORA 6: solo se ofrecen salas activas. Una sala en mantenimiento
        //(estado INACTIVA) ya no se puede programar.
        cbSala.getItems().clear();
        for (Sala s : salaDao.listAll()) {
            if (salaActiva(s)) {
                cbSala.getItems().add(s);
            }
        }
        //fecha sugerida al crear una funcion nueva
        dpFecha.setValue(LocalDate.now());
    }

    public boolean isSaved() {
        return saved;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    private boolean validate() {
        if (cbPelicula.getValue() == null
                || cbSala.getValue() == null
                || dpFecha.getValue() == null
                || txtHorario.getText().isBlank()
                || txtPrecio.getText().isBlank()) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.", "ERROR");
            // CAMBIO: showAndWait() en vez de show()
            alert.showAndWait();
            return false;
        }
        // CAMBIO: se valida que txtHorario tenga un formato de hora válido (ej. 18:30)
        try {
            LocalTime.parse(txtHorario.getText().trim());
        } catch (DateTimeParseException e) {
            utilAlerts.mostrarAlerts("Horario invalido",
                    "Ingresa una hora valida (ejemplo: 18:30).", "WARNING").showAndWait();
            return false;
        }
        //MEJORA 5a: una funcion NUEVA no puede quedar en una fecha pasada.
        //Al editar se permite porque la funcion pudo haber sido programada antes.
        if (funcion == null && dpFecha.getValue().isBefore(LocalDate.now())) {
            utilAlerts.mostrarAlerts("Fecha invalida",
                    "No se puede programar una función en una fecha que ya pasó.",
                    "WARNING").showAndWait();
            return false;
        }
        try {
            double precio = leerPrecio();
            if (precio <= 0) {
                utilAlerts.mostrarAlerts("Precio invalido",
                        "El precio debe ser mayor que cero.", "WARNING").showAndWait();
                return false;
            }
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts("Precio invalido",
                    "Ingresa un precio valido (ejemplo: 12.50).", "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    //CORREGIDO: en espanol el precio se escribe con coma (12,50) y
    //Double.parseDouble solo acepta punto; aqui se cambia la coma por punto
    //para que el precio se pueda escribir de las dos formas
    private double leerPrecio() {
        return Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
    }

    private void cerrar() {
        Stage stage = (Stage) txtHorario.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        if (!validate()) {
            return;
        }

        // CAMBIO: se busca duplicado ANTES de crear o modificar el objeto 'funcion'
        Funcion duplicada;
        try {
            duplicada = buscarDuplicada();
        } catch (RuntimeException e) {
            utilAlerts.mostrarAlerts("Error",
                    "No se pudo revisar las funciones: " + e.getMessage(), "ERROR").showAndWait();
            return;
        }
        if (duplicada != null) {
            utilAlerts.mostrarAlerts("Función cruzada",
                    "Ya hay una función en esa sala a esa fecha y el horario se cruza "
                            + "con la duración de la película.", "WARNING").showAndWait();
            return;
        }

        if (funcion == null) {
            funcion = new Funcion();
        }

        funcion.setPelicula(cbPelicula.getValue());
        funcion.setSala(cbSala.getValue());
        funcion.setFecha(dpFecha.getValue().toString());
        funcion.setHorario(txtHorario.getText().trim());
        funcion.setPrecio(leerPrecio());
        saved = true;
        cerrar();
    }

    /**
     * MEJORA 5a: una sala no puede tener dos funciones que se CRUCEN.
     *
     * Antes solo se comparaba la hora exacta: se podia programar una pelicula
     * de 2 horas a las 18:00 y otra a las 19:00 en la misma sala, y las dos
     * se pisaban. Ahora se calcula a que hora termina cada funcion usando la
     * duracion de su pelicula y se revisa si los horarios se superponen.
     *
     * Dos rangos se cruzan cuando uno empieza antes de que termine el otro:
     *   nueva:   18:00 ---------------- 20:00
     *   existente:      19:00 ---------------- 21:00   -> se cruzan
     */
    private Funcion buscarDuplicada() {
        LocalTime horaNueva;
        try {
            horaNueva = LocalTime.parse(txtHorario.getText().trim());
        } catch (DateTimeParseException e) {
            return null;
        }
        //la fecha se toma del selector del formulario
        LocalDate fechaSeleccionada = dpFecha.getValue();
        if (fechaSeleccionada == null) {
            return null;
        }
        String fechaNueva = fechaSeleccionada.toString();
        int idSala = cbSala.getValue().getIdsala();
        //hora a la que termina la funcion nueva
        int duracionNueva = duracionMinutos(cbPelicula.getValue());
        LocalTime finNueva = horaNueva.plusMinutes(duracionNueva);

        for (Funcion f : funcionDao.listAll()) {
            //al editar, la propia funcion no se compara consigo misma
            if (funcion != null && f.getIdfuncion() == funcion.getIdfuncion()) {
                continue;
            }
            if (f.getSala() == null || f.getSala().getIdsala() != idSala) {
                continue;
            }
            if (f.getFecha() == null || !soloFecha(f.getFecha()).equals(fechaNueva)) {
                continue;
            }
            if (f.getHorario() == null) {
                continue;
            }
            LocalTime horaExistente;
            try {
                //se comparan horas, no el texto
                horaExistente = LocalTime.parse(f.getHorario().trim());
            } catch (DateTimeParseException e) {
                //si la hora guardada no tiene formato valido se compara como texto
                if (f.getHorario().trim().equals(txtHorario.getText().trim())) {
                    return f;
                }
                continue;
            }
            //misma hora de inicio: siempre es un choque
            if (horaNueva.equals(horaExistente)) {
                return f;
            }
            //se cruzan si una empieza antes de que la otra termine
            int duracionExistente = duracionMinutos(f.getPelicula());
            LocalTime finExistente = horaExistente.plusMinutes(duracionExistente);
            if (horaNueva.isBefore(finExistente) && horaExistente.isBefore(finNueva)) {
                return f;
            }
        }
        return null;
    }

    /**
     * MEJORA 1: los minutos se piden a la clase Duracion, que entiende
     * "120", "95 min", "1:50" y "2h".
     *
     * Antes este metodo tomaba solo los primeros digitos, asi que una
     * pelicula de "1:50" se leia como 1 minuto y el cruce de horarios no
     * detectaba el choque. Si no hay numeros devuelve 0: esa pelicula solo
     * choca cuando empieza a la misma hora que otra.
     */
    private int duracionMinutos(Pelicula p) {
        return p == null ? 0 : Duracion.aMinutos(p.getDuracion());
    }

    //MEJORA 6: una sala esta disponible si su estado no es INACTIVA
    private boolean salaActiva(Sala s) {
        if (s == null) {
            return false;
        }
        String estado = s.getEstado() != null ? s.getEstado().trim() : "ACTIVA";
        return !"INACTIVA".equalsIgnoreCase(estado);
    }

    //de una fecha tipo "2026-11-20" toma solo "2026-11-20" (por si viene con hora)
    private String soloFecha(String fecha) {
        String texto = fecha.trim();
        return texto.length() > 10 ? texto.substring(0, 10) : texto;
    }

    public void setFuncion(Funcion f) {
        this.funcion = f;
        if (f != null) {
            lblTitulo.setText("Editar Funcion");
            if (f.getPelicula() != null) {
                for (Pelicula p : cbPelicula.getItems()) {
                    if (p.getidpelicula().equals(f.getPelicula().getidpelicula())) {
                        cbPelicula.setValue(p);
                        break;
                    }
                }
            }
            if (f.getSala() != null) {
                boolean encontrada = false;
                for (Sala s : cbSala.getItems()) {
                    if (s.getIdsala() == f.getSala().getIdsala()) {
                        cbSala.setValue(s);
                        encontrada = true;
                        break;
                    }
                }
                //MEJORA 6: si la sala quedo inactiva ya no esta en la lista;
                //se agrega solo para mostrar la funcion que se esta editando
                if (!encontrada) {
                    cbSala.getItems().add(f.getSala());
                    cbSala.setValue(f.getSala());
                }
            }
            //el horario se guarda como texto, se muestra tal cual
            txtHorario.setText(f.getHorario() != null ? f.getHorario() : "");
            txtPrecio.setText(String.valueOf(f.getPrecio()));
            //se muestra la fecha que ya tenia guardada la funcion
            String fecha = f.getFecha();
            if (fecha != null && !fecha.isBlank()) {
                try {
                    dpFecha.setValue(LocalDate.parse(soloFecha(fecha)));
                } catch (Exception e) {
                    dpFecha.setValue(LocalDate.now());
                }
            } else {
                dpFecha.setValue(LocalDate.now());
            }
        }
    }
}