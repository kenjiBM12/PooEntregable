package pe.edu.upeu.gestioo_de_cine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MEJORA 5: pruebas de PBKDF2 con sal.
 * Comprueban que la misma clave con sales distintas da hashes distintos y que
 * la verificacion acepta la clave correcta y rechaza la incorrecta.
 */
class SeguridadPbkdf2Test {

    @Test
    void mismaClaveConSalesDistintasDaHashesDistintos() {
        String sal1 = Seguridad.nuevaSal();
        String sal2 = Seguridad.nuevaSal();
        assertNotEquals(sal1, sal2);
        assertNotEquals(Seguridad.pbkdf2("admin123", sal1, Seguridad.ITERACIONES),
                Seguridad.pbkdf2("admin123", sal2, Seguridad.ITERACIONES));
    }

    @Test
    void verificarAceptaLaClaveCorrectaYRechazaLaIncorrecta() {
        String sal = Seguridad.nuevaSal();
        String guardada = Seguridad.pbkdf2("claveSegura", sal, Seguridad.ITERACIONES);
        assertTrue(Seguridad.verificar("claveSegura", guardada, sal, Seguridad.ITERACIONES));
        assertFalse(Seguridad.verificar("otraClave", guardada, sal, Seguridad.ITERACIONES));
    }

    @Test
    void verificarSigueAceptandoElHashSha256Viejo() {
        //una base vieja no tiene sal: se compara con SHA-256 y luego se migra
        String viejo = Seguridad.hash("admin123");
        assertTrue(Seguridad.verificar("admin123", viejo, null, 0));
        assertFalse(Seguridad.verificar("mala", viejo, null, 0));
    }

    @Test
    void esClaveGuardadaDistingueHashDeClaveNueva() {
        assertTrue(Seguridad.esClaveGuardada(Seguridad.hash("x")));
        String sal = Seguridad.nuevaSal();
        assertTrue(Seguridad.esClaveGuardada(Seguridad.pbkdf2("x", sal, Seguridad.ITERACIONES)));
        assertFalse(Seguridad.esClaveGuardada("claveNueva"));
    }
}
