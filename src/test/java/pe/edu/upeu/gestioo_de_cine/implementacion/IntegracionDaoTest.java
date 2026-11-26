package pe.edu.upeu.gestioo_de_cine.implementacion;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import pe.edu.upeu.gestioo_de_cine.interfaces.ICierreCajaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.ITarifaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
import pe.edu.upeu.gestioo_de_cine.modelo.CierreCaja;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.modelo.ResumenPago;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;
import pe.edu.upeu.gestioo_de_cine.utils.Migraciones;
import pe.edu.upeu.gestioo_de_cine.utils.Seguridad;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MEJORA 2: pruebas de integracion de los DAO sobre una base temporal.
 *
 * Nunca tocan la base real: se cambia "user.home" a una carpeta de JUnit
 * (@TempDir), asi ConexionDB crea ahi el esquema completo y los respaldos.
 * Cubren lo que mueve dinero: venta en transaccion, butaca repetida,
 * anulacion por funcion, cierre de caja y tarifas.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegracionDaoTest {

    @TempDir
    static Path carpetaTemporal;

    private static String homeAnterior;
    private static int idSala;
    private static int idFuncion;
    private static final List<Integer> idButacas = new ArrayList<>();

    private static final IVentaDao ventaDao = new VentaDaoImpl();
    private static final ICierreCajaDao cierreDao = new CierreCajaDaoImpl();
    private static final ITarifaDao tarifaDao = new TarifaDaoImpl();
    private static String codigoCompra;

    @BeforeAll
    static void prepararBase() throws Exception {
        homeAnterior = System.getProperty("user.home");
        System.setProperty("user.home", carpetaTemporal.toString());
        ConexionDB.cerrar();

        Connection cn = ConexionDB.getConnection();
        idSala = insertar(cn, "INSERT INTO sala (numero, tipo_sala, capacidad, estado, filas, columnas) "
                + "VALUES (1,'3D',4,'ACTIVA',2,2)");
        for (int fila = 1; fila <= 2; fila++) {
            for (int numero = 1; numero <= 2; numero++) {
                idButacas.add(insertar(cn, "INSERT INTO butaca (id_sala, fila, numero) VALUES (?,?,?)",
                        idSala, fila, numero));
            }
        }
        int idPelicula = insertar(cn,
                "INSERT INTO pelicula (numero, titulo, duracion, categoria, clasificacion) "
                        + "VALUES (1,'Prueba','110','Drama','+14')");
        idFuncion = insertar(cn,
                "INSERT INTO funcion (id_pelicula, id_sala, fecha, horario, precio, estado) "
                        + "VALUES (?,?,?,?,?,'activa')",
                idPelicula, idSala, LocalDate.now().toString(), "18:00", 15.0);
    }

    @AfterAll
    static void cerrarBase() {
        ConexionDB.cerrar();
        System.setProperty("user.home", homeAnterior);
    }

    @Test
    @Order(1)
    void elEsquemaQuedaEnLaVersionTres() throws Exception {
        assertEquals(3, Migraciones.leerVersion(ConexionDB.getConnection()));
    }

    @Test
    @Order(2)
    void laVentaGuardaTodosLosBoletosConTransaccion() {
        Funcion f = new Funcion();
        f.setIdfuncion(idFuncion);
        f.setPrecio(15.0);
        Cliente c = new Cliente();
        c.setNombre("Ana");
        c.setApellidos("Quispe");
        c.setDni("70000001");
        c.setEmail("ana@correo.com");
        c.setGenero("Femenino");
        c.setTelefono("987654321");

        List<Butaca> butacas = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Butaca b = new Butaca();
            b.setIdbutaca(idButacas.get(i));
            butacas.add(b);
        }
        codigoCompra = ventaDao.registrarVentas(f, c, butacas, 1, "EFECTIVO");
        assertNotNull(codigoCompra);
        assertEquals(2, ventaDao.listarPorCodigo(codigoCompra).size());
    }

    @Test
    @Order(3)
    void noSePuedeVenderDosVecesLaMismaButaca() {
        Funcion f = new Funcion();
        f.setIdfuncion(idFuncion);
        f.setPrecio(15.0);
        Cliente c = new Cliente();
        c.setNombre("Luis");
        c.setApellidos("Ramos");
        c.setDni("70000002");
        c.setEmail("luis@correo.com");
        c.setGenero("Masculino");
        c.setTelefono("987111222");
        List<Butaca> butacas = new ArrayList<>();
        Butaca repetida = new Butaca();
        repetida.setIdbutaca(idButacas.get(0));
        butacas.add(repetida);

        //la transaccion se revierte y devuelve null
        assertNull(ventaDao.registrarVentas(f, c, butacas, 1, "TARJETA"));
    }

    @Test
    @Order(4)
    void cancelarLaFuncionAnulaSusBoletosActivos() {
        assertEquals(2, ventaDao.anularPorFuncion(idFuncion, 1, "Función cancelada"));
        for (Venta v : ventaDao.listarPorCodigo(codigoCompra)) {
            assertEquals("anulada", v.getEstado());
        }
    }

    @Test
    @Order(5)
    void elCierreDeCajaSeparaCobradoYDevuelto() {
        List<ResumenPago> filas = cierreDao.calcularDia(LocalDate.now().toString());
        assertFalse(filas.isEmpty());
        ResumenPago efectivo = filas.get(0);
        assertEquals("EFECTIVO", efectivo.getMetodo());
        //la funcion se cancelo en la prueba anterior: lo cobrado queda en 0 y
        //el importe aparece como devuelto (neto negativo), que es lo que ve el arqueo
        assertEquals(0.0, efectivo.getMonto(), 0.001);
        assertEquals(30.0, efectivo.getDevuelto(), 0.001);
        assertEquals(-30.0, efectivo.getNeto(), 0.001);
    }

    @Test
    @Order(6)
    void elCierreSeGuardaYSePuedeConsultar() {
        CierreCaja c = new CierreCaja();
        c.setFecha(LocalDate.now().toString());
        c.setMontoInicial(50);
        c.setTotalEsperado(50);
        c.setTotalContado(48);
        c.setDiferencia(-2);
        c.setUsuario("admin");
        c.setIdUsuario(1);
        assertTrue(cierreDao.registrar(c));
        CierreCaja guardado = cierreDao.buscarPorFecha(LocalDate.now().toString());
        assertNotNull(guardado);
        assertEquals(-2, guardado.getDiferencia(), 0.001);
    }

    @Test
    @Order(7)
    void lasTarifasSeSiembranYSeConsultanPorDia() {
        double precio = tarifaDao.precio("3D", LocalDate.now().getDayOfWeek().getValue());
        assertEquals(15.0, precio, 0.001);
        assertTrue(tarifaDao.guardar("VIP", 2, 20.0));
        assertEquals(20.0, tarifaDao.precio("VIP", 2), 0.001);
        //el martes usa la tarifa del dia y no la base
        assertEquals(20.0, tarifaDao.precio("VIP", 2), 0.001);
    }

    @Test
    @Order(9)
    void cancelarLaFuncionLaMarcaSinBorrarla() {
        FuncionDaoImpl funcionDao = new FuncionDaoImpl();
        assertTrue(funcionDao.cancelar(idFuncion));
        Funcion guardada = funcionDao.searchById(idFuncion);
        assertNotNull(guardada);
        assertTrue(guardada.isCancelada());
        //se puede volver a llamar sin romper nada
        assertFalse(funcionDao.cancelar(idFuncion));
    }

    @Test
    @Order(8)
    void elAdminInicialSeGuardaConSal() throws Exception {
        try (Statement st = ConexionDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT contrasena, sal FROM usuario WHERE usuario='admin'")) {
            assertTrue(rs.next());
            assertNotNull(rs.getString("sal"));
            assertTrue(Seguridad.verificar("admin123", rs.getString("contrasena"),
                    rs.getString("sal"), Seguridad.ITERACIONES));
        }
    }

    private static int insertar(Connection cn, String sql, Object... datos) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < datos.length; i++) {
                ps.setObject(i + 1, datos[i]);
            }
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : 0;
            }
        }
    }
}
