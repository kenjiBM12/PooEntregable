package pe.edu.upeu.gestioo_de_cine.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MEJORA 7: contraseñas guardadas con SHA-256.
 *
 * Antes la tabla usuario guardaba la contraseña tal como se escribia
 * ("admin123"). Ahora se guarda un HASH: un texto de 64 caracteres que
 * se obtiene al pasar la contraseña por el algoritmo SHA-256.
 *
 * Un hash NO se puede "deshacer": no existe forma de leer la contraseña
 * original a partir del hash. Para comprobar el login se vuelve a calcular
 * el hash de lo que escribe el usuario y se compara con el guardado.
 *
 * Es la misma idea que usan los sistemas reales, pero sin librerias
 * externas: solo java.security.MessageDigest, que viene con Java.
 */
public class Seguridad {

    /**
     * Devuelve el hash SHA-256 del texto, en minusculas y en hexadecimal.
     * Ejemplo: hash("admin123") siempre devuelve el mismo texto de 64 letras.
     */
    public static String hash(String texto) {
        if (texto == null) {
            texto = "";
        }
        try {
            //1. se pide a Java el algoritmo SHA-256
            MessageDigest algoritmo = MessageDigest.getInstance("SHA-256");
            //2. se convierte el texto a bytes y se calcula el resumen
            byte[] resumen = algoritmo.digest(texto.getBytes(StandardCharsets.UTF_8));
            //3. se pasa cada byte a dos digitos hexadecimales
            StringBuilder textoHash = new StringBuilder();
            for (int i = 0; i < resumen.length; i++) {
                //"& 0xff" convierte el byte (que puede ser negativo) a 0..255
                textoHash.append(String.format("%02x", resumen[i] & 0xff));
            }
            return textoHash.toString();
        } catch (NoSuchAlgorithmException e) {
            //SHA-256 siempre existe en Java; si no, es un error grave del sistema
            throw new RuntimeException("No se encontro el algoritmo SHA-256", e);
        }
    }

    /**
     * Revisa si un texto YA es un hash SHA-256 (64 caracteres hexadecimales).
     * Sirve para no hashear dos veces la misma contraseña al editar un usuario.
     */
    public static boolean esHash(String texto) {
        if (texto == null || texto.length() != 64) {
            return false;
        }
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            boolean hexadecimal = (c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'f')
                    || (c >= 'A' && c <= 'F');
            if (!hexadecimal) {
                return false;
            }
        }
        return true;
    }
}
