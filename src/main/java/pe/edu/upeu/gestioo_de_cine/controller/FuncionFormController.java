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
        cbSala.getItems().setAll(salaDao.listAll());
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
            utilAlerts.mostrarAlerts("Funcion duplicada",
                    "Ya existe una funcion en esa sala a esa misma hora y fecha.",
                    "WARNING").showAndWait();
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
     * Una sala no puede tener dos funciones a la misma hora del mismo dia,
     * sin importar que pelicula sea. El horario se compara como LocalTime
     * para que "18:30" y "18:30:00" cuenten como la misma hora.
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
            try {
                //se comparan horas, no el texto
                if (horaNueva.equals(LocalTime.parse(f.getHorario().trim()))) {
                    return f;
                }
            } catch (DateTimeParseException e) {
                //si la hora guardada no tiene formato valido se compara como texto
                if (f.getHorario().trim().equals(txtHorario.getText().trim())) {
                    return f;
                }
            }
        }
        return null;
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
                for (Sala s : cbSala.getItems()) {
                    if (s.getIdsala() == f.getSala().getIdsala()) {
                        cbSala.setValue(s);
                        break;
                    }
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