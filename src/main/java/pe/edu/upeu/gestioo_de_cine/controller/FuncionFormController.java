package pe.edu.upeu.gestioo_de_cine.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
        cbSala.getItems().clear();
        for (Sala s : salaDao.listAll()) {
            if (salaActiva(s)) {
                cbSala.getItems().add(s);
            }
        }
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
            alert.showAndWait();
            return false;
        }
        try {
            LocalTime.parse(txtHorario.getText().trim());
        } catch (DateTimeParseException e) {
            utilAlerts.mostrarAlerts("Horario invalido",
                    "Ingresa una hora valida (ejemplo: 18:30).", "WARNING").showAndWait();
            return false;
        }
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

    private Funcion buscarDuplicada() {
        LocalTime horaNueva;
        try {
            horaNueva = LocalTime.parse(txtHorario.getText().trim());
        } catch (DateTimeParseException e) {
            return null;
        }
        LocalDate fechaSeleccionada = dpFecha.getValue();
        if (fechaSeleccionada == null) {
            return null;
        }
        String fechaNueva = fechaSeleccionada.toString();
        int idSala = cbSala.getValue().getIdsala();
        int duracionNueva = duracionMinutos(cbPelicula.getValue());
        LocalTime finNueva = horaNueva.plusMinutes(duracionNueva);

        for (Funcion f : funcionDao.listAll()) {
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
                horaExistente = LocalTime.parse(f.getHorario().trim());
            } catch (DateTimeParseException e) {
                if (f.getHorario().trim().equals(txtHorario.getText().trim())) {
                    return f;
                }
                continue;
            }
            if (horaNueva.equals(horaExistente)) {
                return f;
            }
            int duracionExistente = duracionMinutos(f.getPelicula());
            LocalTime finExistente = horaExistente.plusMinutes(duracionExistente);
            if (horaNueva.isBefore(finExistente) && horaExistente.isBefore(finNueva)) {
                return f;
            }
        }
        return null;
    }

    private int duracionMinutos(Pelicula p) {
        return p == null ? 0 : p.getDuracionMinutos();
    }

    private boolean salaActiva(Sala s) {
        if (s == null) {
            return false;
        }
        String estado = s.getEstado() != null ? s.getEstado().trim() : "ACTIVA";
        return !"INACTIVA".equalsIgnoreCase(estado);
    }

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
                if (!encontrada) {
                    cbSala.getItems().add(f.getSala());
                    cbSala.setValue(f.getSala());
                }
            }
            txtHorario.setText(f.getHorario() != null ? f.getHorario() : "");
            txtPrecio.setText(String.valueOf(f.getPrecio()));
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
