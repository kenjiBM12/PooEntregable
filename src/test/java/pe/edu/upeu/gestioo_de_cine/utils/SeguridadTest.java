package pe.edu.upeu.gestioo_de_cine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MEJORA 4: pruebas automaticas de la clase Seguridad.
 *
 * Comprueban que la contraseña nunca se guarda tal cual, que el hash siempre
 * tiene 64 caracteres y que el sistema reconoce cuando un texto ya es un hash
 * (para no hashearlo dos veces al editar un usuario).
 */
class SeguridadTest {

    @Test
    void elHashTieneSesentaYCuatroCaracteres() {
        String hash = Seguridad.hash("admin123");
        assertEquals(64, hash.length());
        assertNotEquals("admin123", hash);
    }

    @Test
    void elMismoTextoDaElMismoHash() {
        assertEquals(Seguridad.hash("clave2026"), Seguridad.hash("clave2026"));
        assertNotEquals(Seguridad.hash("clave2026"), Seguridad.hash("clave2027"));
    }

    @Test
    void reconoceUnHashYNoUnaClaveNormal() {
        assertTrue(Seguridad.esHash(Seguridad.hash("admin123")));
        assertFalse(Seguridad.esHash("admin123"));
        assertFalse(Seguridad.esHash(""));
        assertFalse(Seguridad.esHash(null));
    }
}
