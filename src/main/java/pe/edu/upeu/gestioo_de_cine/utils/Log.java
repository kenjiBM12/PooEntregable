package pe.edu.upeu.gestioo_de_cine.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * MEJORA 11: registro de errores en archivo y aviso amigable.
 *
 * Antes las excepciones solo salian por la consola (que el usuario final no
 * ve). Ahora se agregan a:
 *   C:\Users\<usuario>\SistemaCineUPeU\logs\error.log
 * y, si el error llega sin control, se muestra una alerta generica en vez de
 * dejar la ventana trabada.
 *
 * El log nunca interrumpe la operacion principal: si no se puede escribir el
 * archivo, el error se imprime y se sigue.
 */
public class Log {

    public static File archivo() {
        File carpeta = new File(ConexionDB.carpetaSistema(), "logs");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        return new File(carpeta, "error.log");
    }

    /** Guarda un error con su contexto y la traza completa. */
    public static void error(String contexto, Throwable e) {
        escribir("ERROR", contexto, e);
    }

    /** Guarda un mensaje informativo (por ejemplo, una migracion aplicada). */
    public static void info(String mensaje) {
        escribir("INFO", mensaje, null);
    }

    private static void escribir(String nivel, String contexto, Throwable e) {
        File archivo = archivo();
        try (PrintWriter salida = new PrintWriter(new FileWriter(archivo, true))) {
            salida.println(LocalDateTime.now() + " [" + nivel + "] " + contexto);
            if (e != null) {
                salida.println("  " + e.getClass().getName() + ": " + e.getMessage());
                for (StackTraceElement linea : e.getStackTrace()) {
                    salida.println("    at " + linea);
                }
            }
        } catch (Exception ex) {
            System.out.println("No se pudo escribir el log: " + ex.getMessage());
        }
        //ademas se deja en consola para las pruebas
        System.out.println("[" + nivel + "] " + contexto
                + (e != null ? " -> " + e.getMessage() : ""));
    }

    /**
     * Instala el manejador de errores no controlados. Se llama una sola vez al
     * arrancar la aplicacion (HelloApplication).
     */
    public static void instalar() {
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> {
            error("Error no controlado en " + hilo.getName(), error);
            try {
                Platform.runLater(() -> {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Error inesperado");
                    alerta.setHeaderText("Ocurrió un problema en el sistema");
                    alerta.setContentText("La operación no se pudo completar.\n"
                            + "El detalle quedó guardado en:\n" + archivo().getAbsolutePath());
                    alerta.showAndWait();
                });
            } catch (Exception e) {
                //si no hay interfaz disponible (por ejemplo en pruebas) no se hace nada
            }
        });
    }
}
