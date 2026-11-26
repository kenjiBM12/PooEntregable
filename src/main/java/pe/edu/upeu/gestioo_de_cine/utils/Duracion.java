package pe.edu.upeu.gestioo_de_cine.utils;

/**
 * MEJORA 1: utilidad para leer la duracion de una pelicula.
 *
 * La columna pelicula.duracion es de texto y en el formulario se podia
 * escribir de varias formas:
 *   "120"      -> 120 minutos
 *   "95 min"   -> 95 minutos
 *   "1:50"     -> 1 hora y 50 minutos = 110 minutos
 *   "2h"       -> 120 minutos
 *
 * Antes, el cruce de horarios tomaba solo los primeros digitos, asi que
 * "1:50" se leia como 1 minuto y dos funciones se podian pisar. Ahora toda
 * la aplicacion pide los minutos a este metodo, que entiende los formatos.
 *
 * La clase tiene solo metodos estaticos (no se crean objetos) para poder
 * usarla desde cualquier pantalla, igual que Seguridad.
 */
public class Duracion {

    /**
     * Convierte el texto escrito a minutos.
     * Devuelve 0 cuando no hay numeros o el texto esta vacio, para que una
     * pelicula sin duracion valida solo choque con la misma hora de inicio.
     */
    public static int aMinutos(String texto) {
        if (texto == null) {
            return 0;
        }
        //se pasa todo a minusculas para no repetir comparaciones con "H" y "h"
        String valor = texto.trim().toLowerCase();
        if (valor.isEmpty()) {
            return 0;
        }
        //se quitan las palabras que solo adornan el numero: "95 minutos" -> "95"
        valor = valor.replace("minutos", "")
                .replace("min", "")
                .replace("hrs", "")
                .replace("hr", "")
                .replace("hs", "")
                .trim();
        if (valor.isEmpty()) {
            return 0;
        }

        //formato "horas:minutos" (por ejemplo 1:50)
        if (valor.contains(":")) {
            String[] partes = valor.split(":");
            int horas = soloNumero(partes.length > 0 ? partes[0] : "");
            int minutos = partes.length > 1 ? soloNumero(partes[1]) : 0;
            return horas * 60 + minutos;
        }
        //formato "1h 50" o "2h"
        if (valor.contains("h")) {
            String[] partes = valor.split("h");
            int horas = soloNumero(partes.length > 0 ? partes[0] : "");
            int minutos = partes.length > 1 ? soloNumero(partes[1]) : 0;
            return horas * 60 + minutos;
        }
        //formato simple: el numero ya son minutos ("120")
        return soloNumero(valor);
    }

    /**
     * Devuelve la duracion SIEMPRE en minutos para guardarla en la base.
     * Si el texto no se puede leer se conserva tal cual (sin espacios) para
     * no perder lo que escribio el usuario.
     */
    public static String normalizar(String texto) {
        int minutos = aMinutos(texto);
        if (minutos > 0) {
            return String.valueOf(minutos);
        }
        return texto == null ? "" : texto.trim();
    }

    /**
     * Saca el primer grupo de digitos de un texto (" 50 " -> 50).
     * Si no hay digitos devuelve 0.
     */
    private static int soloNumero(String texto) {
        if (texto == null) {
            return 0;
        }
        int inicio = 0;
        while (inicio < texto.length() && !Character.isDigit(texto.charAt(inicio))) {
            inicio++;
        }
        int fin = inicio;
        while (fin < texto.length() && Character.isDigit(texto.charAt(fin))) {
            fin++;
        }
        if (inicio == fin) {
            return 0;
        }
        try {
            return Integer.parseInt(texto.substring(inicio, fin));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
