// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.controller;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


/**
 * CONTROLADOR DEL FORMULARIO: recibe datos nuevos o los datos que se van a editar.
 * Valida campos y prepara el objeto; isSaved indica si el usuario pulsó Guardar.
 * El controlador de tabla realiza insert/update al cerrarse esta ventana modal.
 */
public class SalaFormController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private Sala registro;
    // Estado del formulario: empieza en false y solo pasa a true después de Guardar válido.
    private boolean saved = false;
    // Texto visible de la vista que este controlador puede cambiar.
    @FXML private Label lblTitulo;
    // Campo de texto del formulario: getText() devuelve String, incluso si se escribe un número.
    @FXML private TextField txtNombre, txtTipo, txtCapacidad;

    @FXML
    /**
     * FXMLLoader llama a initialize después de inyectar los controles @FXML.
     * En formularios con combos se cargan sus opciones; si está vacío no hay preparación adicional.
     */
    public void initialize() {

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
    public Sala getSala() {
        return registro;
    }

    /**
     * Recibe el objeto que se editará; null significa registro nuevo.
     * Carga sus datos en los controles y reinicia saved=false.
     */
    public void setSala(Sala value) {
        registro = value;
        saved = false;
        lblTitulo.setText(value == null ? "Nuevo registro" : "Editar registro");
        if (value != null) {
            txtNombre.setText(value.getNombre());
            txtTipo.setText(value.getTipo());
            txtCapacidad.setText(String.valueOf(value.getCapacidad()));
        }
    }

    /**
     * Comprueba campos obligatorios y valores numéricos antes de aceptar el formulario.
     * Devuelve false y muestra un mensaje si algún dato no cumple la validación.
     */
    private boolean validate() {
        if (txtNombre.getText().isBlank() || txtTipo.getText().isBlank()) {
            return error("Complete nombre y tipo");
        }
        try {
            // Convierte texto a int; texto vacío o letras provocan NumberFormatException.
            if (Integer.parseInt(txtCapacidad.getText().trim()) <= 0) {
                return error("La capacidad debe ser mayor que cero");
            }
        } catch (NumberFormatException e) {
            return error("Ingrese una capacidad entera");
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
            registro = new Sala();
        }
        registro.setNombre(txtNombre.getText().trim());
        registro.setTipo(txtTipo.getText().trim());
        registro.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));
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
