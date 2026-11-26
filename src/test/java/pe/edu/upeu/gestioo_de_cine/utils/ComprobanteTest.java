package pe.edu.upeu.gestioo_de_cine.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MEJORA 9: el comprobante se guarda como archivo .txt (sin HTML), dentro de
 * la carpeta SistemaCineUPeU/comprobantes del usuario. La prueba usa una
 * carpeta temporal para no escribir en la carpeta real.
 */
class ComprobanteTest {

    @Test
    void guardaElComprobanteComoArchivoDeTexto(@TempDir Path temporal) throws Exception {
        String anterior = System.getProperty("user.home");
        try {
            System.setProperty("user.home", temporal.toString());
            File archivo = Comprobante.guardar("C-0001", "CINEMAX\nTOTAL: S/ 25.00\n");
            assertNotNull(archivo);
            assertTrue(archivo.getName().endsWith(".txt"), "debe ser .txt: " + archivo.getName());
            assertEquals("CINEMAX\nTOTAL: S/ 25.00\n", Files.readString(archivo.toPath()));
            assertTrue(archivo.getAbsolutePath().contains("comprobantes"));
        } finally {
            System.setProperty("user.home", anterior);
        }
    }

    @Test
    void cambiaLosCaracteresRarosDelCodigoEnElNombre(@TempDir Path temporal) {
        String anterior = System.getProperty("user.home");
        try {
            System.setProperty("user.home", temporal.toString());
            File archivo = Comprobante.guardar("C/1 2", "x");
            assertNotNull(archivo);
            assertEquals("comprobante_C_1_2.txt", archivo.getName());
        } finally {
            System.setProperty("user.home", anterior);
        }
    }
}
