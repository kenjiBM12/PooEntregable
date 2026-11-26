package pe.edu.upeu.gestioo_de_cine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MEJORA 14: pruebas de los validadores compartidos.
 * Fijan las reglas que usan todos los formularios.
 */
class ValidacionesTest {

    @Test
    void dniAceptaSoloOchoDigitos() {
        assertTrue(Validaciones.dni("74581236"));
        assertFalse(Validaciones.dni("7458123"));
        assertFalse(Validaciones.dni("745812369"));
        assertFalse(Validaciones.dni("7458123A"));
        assertFalse(Validaciones.dni(null));
    }

    @Test
    void emailAceptaVacioYRechazaSinArroba() {
        assertTrue(Validaciones.email(""));
        assertTrue(Validaciones.email(null));
        assertTrue(Validaciones.email("kenji@gmail.com"));
        assertFalse(Validaciones.email("kenji.gmail.com"));
        assertFalse(Validaciones.email("kenji@gmail"));
        assertFalse(Validaciones.email("ken ji@gmail.com"));
    }

    @Test
    void telefonoAceptaNumerosConSeparadores() {
        assertTrue(Validaciones.telefono("987654321"));
        assertTrue(Validaciones.telefono("+51 987-654-321"));
        assertFalse(Validaciones.telefono("123"));
        assertFalse(Validaciones.telefono("abcdefgh"));
    }

    @Test
    void precioDebeSerPositivoYRazonable() {
        assertTrue(Validaciones.precio(12.5));
        assertFalse(Validaciones.precio(0));
        assertFalse(Validaciones.precio(-3));
        assertFalse(Validaciones.precio(10000));
    }

    @Test
    void duracionEntreUnoYSeiscientosMinutos() {
        assertTrue(Validaciones.duracionMinutos(1));
        assertTrue(Validaciones.duracionMinutos(169));
        assertFalse(Validaciones.duracionMinutos(0));
        assertFalse(Validaciones.duracionMinutos(601));
    }

    @Test
    void horaAceptaFormatoCortoYCompleto() {
        assertTrue(Validaciones.hora("18:30"));
        assertTrue(Validaciones.hora("18:30:00"));
        assertFalse(Validaciones.hora("25:99"));
        assertFalse(Validaciones.hora(""));
    }
}
