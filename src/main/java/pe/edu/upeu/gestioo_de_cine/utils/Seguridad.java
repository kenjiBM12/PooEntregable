package pe.edu.upeu.gestioo_de_cine.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

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
     * MEJORA 5: algoritmo lento con sal para las claves nuevas.
     * SHA-256 solo es rapidisimo: millones de intentos por segundo. PBKDF2
     * repite el calculo muchas veces y mezcla una sal aleatoria por usuario,
     * asi dos personas con la misma clave tienen hashes distintos y atacar
     * el archivo .db cuesta muchisimo mas.
     * Todo viene en el JDK (javax.crypto): no se agregan librerias.
     */
    private static final String ALGORITMO_PBKDF2 = "PBKDF2WithHmacSHA256";
    public static final int ITERACIONES = 120000;

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
     * MEJORA 5: genera una sal aleatoria nueva (16 bytes, en Base64).
     * Cada usuario tiene su propia sal; se guarda junto al hash.
     */
    public static String nuevaSal() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Calcula el hash PBKDF2 de la clave con la sal y las iteraciones dadas.
     * El resultado se guarda en Base64 (no se puede deshacer).
     */
    public static String pbkdf2(String clave, String sal, int iteraciones) {
        try {
            byte[] salBytes = Base64.getDecoder().decode(sal);
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), salBytes,
                    Math.max(iteraciones, ITERACIONES), 256);
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance(ALGORITMO_PBKDF2);
            byte[] hash = fabrica.generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo calcular PBKDF2", e);
        }
    }

    /**
     * Comprueba una clave contra lo guardado.
     * - Si hay sal, se usa PBKDF2 (formato nuevo).
     * - Si no hay sal, se compara con SHA-256 (bases viejas). El login se
     *   encarga despues de migrar esa clave a PBKDF2.
     */
    public static boolean verificar(String clave, String guardada, String sal, int iteraciones) {
        if (guardada == null) {
            return false;
        }
        if (sal == null || sal.isBlank()) {
            return guardada.equals(hash(clave));
        }
        return guardada.equals(pbkdf2(clave, sal, iteraciones));
    }

    /**
     * Revisa si un texto YA es una clave guardada (hash) y no una clave nueva.
     * Sirve al editar un usuario sin cambiarle la contraseña.
     */
    public static boolean esClaveGuardada(String texto) {
        if (esHash(texto)) {
            return true;
        }
        if (texto == null || texto.length() < 40) {
            return false;
        }
        try {
            return Base64.getDecoder().decode(texto).length == 32;
        } catch (IllegalArgumentException e) {
            return false;
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
