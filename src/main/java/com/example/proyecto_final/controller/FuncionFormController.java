// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.controller;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.proyecto_final.implementacion.*;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

/**
 * CONTROLADOR DEL FORMULARIO: recibe datos nuevos o los datos que se van a editar.
 * Valida campos y prepara el objeto; isSaved indica si el usuario pulsó Guardar.
 * El controlador de tabla realiza insert/update al cerrarse esta ventana modal.
 */
public class FuncionFormController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private Funcion registro;
    // Estado del formulario: empieza en false y solo pasa a true después de Guardar válido.
    private boolean saved = false;
    // Texto visible de la vista que este controlador puede cambiar.
    @FXML private Label lblTitulo;
    // Lista desplegable de objetos: getValue() devuelve el objeto seleccionado.
    @FXML private ComboBox<Pelicula> cbPelicula;
    // Lista desplegable de objetos: getValue() devuelve el objeto seleccionado.
    @FXML private ComboBox<Sala> cbSala;
    // Campo de texto del formulario: getText() devuelve String, incluso si se escribe un número.
    @FXML private TextField txtHorario, txtPrecio;

    @FXML
    /**
     * FXMLLoader llama a initialize después de inyectar los controles @FXML.
     * En formularios con combos se cargan sus opciones; si está vacío no hay preparación adicional.
     */
    public void initialize() {
        cbPelicula.setItems(FXCollections.observableArrayList(new PeliculaDaoImpl().listAll()));
        cbSala.setItems(FXCollections.observableArrayList(new SalaDaoImpl().listAll()));
        cbPelicula.setConverter(new StringConverter<>() {
            /**
             * Define el texto que el ComboBox muestra para un objeto.
             * El valor seleccionado sigue siendo el objeto, no este texto.
             */
            public String toString(Pelicula p) { return p == null ? "" : p.getIdpelicula() + " - " + p.getTitulo(); }
            /**
             * Conversión inversa requerida por StringConverter.
             * Los combos no son editables; aquí no se interpreta texto escrito a mano.
             */
            public Pelicula fromString(String text) { return null; }
        });
        cbSala.setConverter(new StringConverter<>() {
            /**
             * Define el texto que el ComboBox muestra para un objeto.
             * El valor seleccionado sigue siendo el objeto, no este texto.
             */
            public String toString(Sala s) { return s == null ? "" : s.getIdsala() + " - " + s.getNombre(); }
            /**
             * Conversión inversa requerida por StringConverter.
             * Los combos no son editables; aquí no se interpreta texto escrito a mano.
             */
            public Sala fromString(String text) { return null; }
        });
    }

    /**
     * Permite al controlador de tabla saber si el usuario confirmó Guardar.
     * Cancelar o cerrar sin guardar mantiene este valor en false.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Entrega al controlador de tabla el objeto preparado por este formulario.
     */
    public Funcion getFuncion() {
        return registro;
    }

    /**
     * Recibe el objeto que se editará; null significa registro nuevo.
     * Carga sus datos en los controles y reinicia saved=false.
     */
    public void setFuncion(Funcion value) {
        registro = value;
        saved = false;
        lblTitulo.setText(value == null ? "Nuevo registro" : "Editar registro");
        if (value != null) {
            cbPelicula.getItems().stream()
                    .filter(p -> p.getIdpelicula() == value.getPelicula().getIdpelicula())
                    .findFirst().ifPresent(p -> cbPelicula.setValue(p));
            cbSala.getItems().stream()
                    .filter(s -> s.getIdsala() == value.getSala().getIdsala())
                    .findFirst().ifPresent(s -> cbSala.setValue(s));
            txtHorario.setText(value.getHorario());
            txtPrecio.setText(String.valueOf(value.getPrecio()));
        }
    }

    /**
     * Comprueba campos obligatorios y valores numéricos antes de aceptar el formulario.
     * Devuelve false y muestra un mensaje si algún dato no cumple la validación.
     */
    private boolean validate() {
        if (cbPelicula.getValue() == null || cbSala.getValue() == null
                || txtHorario.getText().isBlank()) {
            return error("Seleccione película, sala y complete el horario");
        }
        try {
            // Convierte texto a double; usa punto decimal, por ejemplo 15.50.
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (!Double.isFinite(precio) || precio <= 0) {
                return error("El precio debe ser mayor que cero");
            }
        } catch (NumberFormatException e) {
            return error("Ingrese un precio numérico");
        }
        return true;
    }

    @FXML
    /**
     * Valida, crea el objeto si es nuevo y copia los campos mediante setters.
     * Marca saved=true y cierra; el insert/update lo hará el controlador de tabla.
     */
    private void handleSave() {
        if (!validate()) {
            return;
        }
        if (registro == null) {
            registro = new Funcion();
        }
        registro.setPelicula(cbPelicula.getValue());
        registro.setSala(cbSala.getValue());
        registro.setHorario(txtHorario.getText().trim());
        registro.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        saved = true;
        cerrar();
    }

    @FXML
    /**
     * Cierra el formulario sin marcarlo como guardado.
     */
    private void handleCancel() {
        cerrar();
    }

    /**
     * Obtiene la ventana a partir de un control y la cierra.
     * Control → Scene → Window; el cast Stage permite usar close().
     */
    private void cerrar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra el motivo por el que falló la validación y devuelve false.
     */
    private boolean error(String mensaje) {
        utilAlerts.mostrarAlerts("Datos incorrectos", mensaje, "WARNING").showAndWait();
        return false;
    }
}
