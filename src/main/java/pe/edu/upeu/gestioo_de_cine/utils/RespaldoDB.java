package pe.edu.upeu.gestioo_de_cine.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * MEJORA 2: respaldo automatico de la base de datos.
 *
 * Cada vez que el sistema abre la base (normalmente al iniciar el programa)
 * se guarda una COPIA del archivo .db en la carpeta "respaldos", con la
 * fecha y la hora en el nombre. Asi, si un dia se borra algo por error,
 * se puede recuperar una version anterior.
 *
 * Se guardan como maximo 10 copias: al crear la numero 11 se borra la
 * mas antigua para que la carpeta no crezca sin control.
 *
 * La copia se hace ANTES de abrir la conexion, para que la copia sea
 * exactamente la base que quedo guardada la ultima vez que se uso el sistema.
 */
public class RespaldoDB {

    //cuantas copias se conservan como maximo
    private static final int MAX_RESPALDOS = 10;

    /**
     * Copia el archivo indicado a la carpeta "respaldos".
     * Si la base todavia no existe (primera vez), no hace nada.
     */
    public static void respaldar(File baseDatos) {
        if (baseDatos == null || !baseDatos.exists()) {
            return;
        }
        try {
            //1. la carpeta de respaldos queda al lado del archivo .db
            File carpeta = new File(baseDatos.getParentFile(), "respaldos");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            //2. nombre unico usando la fecha y la hora: gestionCine_2026-11-26_18-30-05.db
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String nombre = "gestionCine_" + formato.format(new Date()) + ".db";
            File copia = new File(carpeta, nombre);

            //3. se copia el archivo completo (Files.copy no borra el original)
            Files.copy(baseDatos.toPath(), copia.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Respaldo creado: " + copia.getAbsolutePath());

            //4. se limpian los respaldos mas viejos
            borrarRespaldosViejos(carpeta);
        } catch (Exception e) {
            //un respaldo que falla NO debe impedir que el sistema funcione
            System.out.println("No se pudo crear el respaldo: " + e.getMessage());
        }
    }

    /**
     * MEJORA 8: lista los respaldos que existen, del mas nuevo al mas viejo.
     * Se ordenan por nombre al reves porque el nombre empieza con la fecha
     * (yyyy-MM-dd_HH-mm-ss) y el orden alfabetico es tambien el cronologico.
     */
    public static List<File> listar(File baseDatos) {
        List<File> lista = new ArrayList<>();
        if (baseDatos == null) {
            return lista;
        }
        File carpeta = new File(baseDatos.getParentFile(), "respaldos");
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return lista;
        }
        for (File archivo : archivos) {
            //solo se muestran las copias del sistema, no otros archivos
            if (archivo.isFile() && archivo.getName().startsWith("gestionCine_")
                    && archivo.getName().endsWith(".db")) {
                lista.add(archivo);
            }
        }
        Collections.sort(lista, Collections.reverseOrder());
        return lista;
    }

    /**
     * MEJORA 8: restaura una copia sobre la base actual.
     *
     * Antes de reemplazar se cierra la conexion compartida: en Windows el
     * archivo .db no se puede sobreescribir mientras el programa lo tiene
     * abierto. Despues de restaurar hay que volver a entrar al sistema.
     * Devuelve true solo si la copia se reemplazo de verdad.
     */
    public static boolean restaurar(File baseDatos, File respaldo) {
        if (baseDatos == null || respaldo == null || !respaldo.exists()) {
            return false;
        }
        try {
            ConexionDB.cerrar();
            Files.copy(respaldo.toPath(), baseDatos.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Base restaurada desde: " + respaldo.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.out.println("No se pudo restaurar el respaldo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deja solo los ultimos MAX_RESPALDOS archivos.
     * Arrays.sort ordena los nombres alfabeticamente; como el nombre empieza
     * con la fecha (anio-mes-dia), el orden alfabetico es tambien el orden
     * cronologico, asi que los primeros son los mas viejos.
     */
    private static void borrarRespaldosViejos(File carpeta) {
        File[] archivos = carpeta.listFiles();
        if (archivos == null || archivos.length <= MAX_RESPALDOS) {
            return;
        }
        Arrays.sort(archivos);
        int sobran = archivos.length - MAX_RESPALDOS;
        for (int i = 0; i < sobran; i++) {
            if (archivos[i].isFile()) {
                boolean borrado = archivos[i].delete();
                if (borrado) {
                    System.out.println("Respaldo viejo eliminado: " + archivos[i].getName());
                }
            }
        }
    }
}
