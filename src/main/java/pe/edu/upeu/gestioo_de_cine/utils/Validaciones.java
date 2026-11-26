package pe.edu.upeu.gestioo_de_cine.utils;

/**
 * MEJORA 14: validadores compartidos.
 *
 * Antes cada controlador repetia sus propios if con mensajes distintos.
 * Ahora las reglas viven aqui y las pruebas (ValidacionesTest) las fijan.
 * Todos los metodos devuelven true cuando el dato es valido.
 */
public class Validaciones {

    /** DNI peruano: exactamente 8 digitos. */
    public static boolean dni(String valor) {
        return valor != null && valor.trim().matches("[0-9]{8}");
    }

    /** Telefono: de 6 a 15 digitos, admite +, espacios y guiones. */
    public static boolean telefono(String valor) {
        if (valor == null) {
            return false;
        }
        String limpio = valor.trim().replace("+", "").replace(" ", "").replace("-", "");
        return limpio.matches("[0-9]{6,15}");
    }

    /** Email sencillo: algo@algo.algo, sin espacios. Vacio se considera opcional. */
    public static boolean email(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return true;
        }
        return valor.trim().matches("[^@\\s]+@[^@\\s]+\\.[A-Za-z]{2,}");
    }

    /** Precio mayor que cero y razonable para una entrada. */
    public static boolean precio(double valor) {
        return valor > 0 && valor <= 9999;
    }

    /** Duracion en minutos entre 1 y 600 (10 horas). */
    public static boolean duracionMinutos(int minutos) {
        return minutos >= 1 && minutos <= 600;
    }

    /** Texto obligatorio (no vacio y con al menos 2 letras). */
    public static boolean texto(String valor, int minimo) {
        return valor != null && valor.trim().length() >= minimo;
    }

    /** Hora en formato HH:mm o HH:mm:ss. */
    public static boolean hora(String valor) {
        if (valor == null) {
            return false;
        }
        try {
            String texto = valor.trim();
            java.time.LocalTime.parse(texto.length() == 5 ? texto : texto.substring(0, 8));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
