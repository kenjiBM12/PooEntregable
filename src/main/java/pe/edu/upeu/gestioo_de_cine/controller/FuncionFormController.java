package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
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

    private Funcion funcion;
    private boolean saved = false;

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private final ISalaDao salaDao = new SalaDaoImpl();
    private final IFuncionDao funcionDao = new FuncionDaoImpl();

    @FXML
    public void initialize() {
        //mostramos el nombre de la pelicula y no el objeto
        cbPelicula.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pelicula p) {
                return p == null ? "" : p.getNombre() + " (" + p.getClasificacion() + ")";
            }

            @Override
            public Pelicula fromString(String string) {
                return null;
            }
        });
        //mostramos el nombre de la sala y no el objeto
        cbSala.setConverter(new StringConverter<>() {
            @Override
            public String toString(Sala s) {
                return s == null ? "" : s.getNombre() + " - " + s.getTipo();
            }

            @Override
            public Sala fromString(String string) {
                return null;
            }
        });
        cbPelicula.getItems().setAll(peliculaDao.listAll());
        cbSala.getItems().setAll(salaDao.listAll());
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
                || txtHorario.getText().isBlank()
                || txtPrecio.getText().isBlank()) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.", "ERROR");
            alert.show();
            return false;
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
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
        if (funcion == null) {
            funcion = new Funcion();
        }
        // evitamos duplicar la misma pelicula en la misma sala y horario
        Funcion duplicada = buscarDuplicada();
        if (duplicada != null) {
            utilAlerts.mostrarAlerts("Funcion duplicada",
                    "Ya existe una funcion de esa pelicula en esa sala a la misma hora.",
                    "WARNING").showAndWait();
            return;
        }
        funcion.setPelicula(cbPelicula.getValue());
        funcion.setSala(cbSala.getValue());
        funcion.setHorario(txtHorario.getText().trim());
        funcion.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        saved = true;
        cerrar();
    }

    private Funcion buscarDuplicada() {
        for (Funcion f : funcionDao.listAll()) {
            boolean mismaFuncion = funcion != null && f.getIdfuncion() == funcion.getIdfuncion();
            if (mismaFuncion) {
                continue;
            }
            boolean mismaPelicula = f.getPelicula() != null
                    && f.getPelicula().getidpelicula().equals(cbPelicula.getValue().getidpelicula());
            boolean mismaSala = f.getSala() != null
                    && f.getSala().getIdsala() == cbSala.getValue().getIdsala();
            boolean mismoHorario = f.getHorario() != null
                    && f.getHorario().equalsIgnoreCase(txtHorario.getText().trim());
            if (mismaPelicula && mismaSala && mismoHorario) {
                return f;
            }
        }
        return null;
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
            txtHorario.setText(f.getHorario());
            txtPrecio.setText(String.valueOf(f.getPrecio()));
        }
    }
}
