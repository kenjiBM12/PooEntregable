package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {

    //las ventas viejas no tienen codigo_compra: se les muestra "V-" mas su id
    private static final String QUERY_BASE =
            " SELECT v.idventa,v.id_funcion,v.id_cliente,v.cliente,v.cantidad_asientos, " +
            " v.total_pagar,v.fecha_venta,v.id_butaca,v.id_usuario,v.estado, " +
            " COALESCE(v.codigo_compra,'V-' || v.idventa) AS codigo_compra, " +
            " v.metodo_pago,v.anulada_por,v.fecha_anulacion,v.motivo_anulacion, " +
            " c.nombre AS nombre_cliente,c.apellidos AS apellidos_cliente,c.dni AS dni_cliente, " +
            " u.nombre AS nombre_empleado,ua.nombre AS nombre_anulador, " +
            " f.horario,f.precio,f.fecha AS fecha_funcion,f.id_pelicula,f.id_sala, " +
            " p.titulo AS nombre_pelicula,p.numero AS numero_pelicula, " +
            " p.duracion,p.categoria,p.clasificacion, " +
            " s.numero AS numero_sala,s.tipo_sala AS tipo,s.capacidad,s.estado AS estado_sala, " +
            " s.filas AS filas_sala,s.columnas AS columnas_sala, " +
            " b.fila AS butaca_fila,b.numero AS butaca_numero " +
            " FROM venta v " +
            " LEFT JOIN cliente c ON v.id_cliente = c.idcliente " +
            " LEFT JOIN usuario u ON v.id_usuario = u.idusuario " +
            " LEFT JOIN usuario ua ON v.anulada_por = ua.idusuario " +
            " LEFT JOIN funcion f ON v.id_funcion = f.idfuncion " +
            " LEFT JOIN pelicula p ON f.id_pelicula = p.idpelicula " +
            " LEFT JOIN sala s ON f.id_sala = s.idsala " +
            " LEFT JOIN butaca b ON v.id_butaca = b.idbutaca ";

    @Override
    public boolean insert(Venta v) {
        //MEJORA 6: se agrega la columna metodo_pago al INSERT
        String query = "INSERT INTO venta(id_funcion,id_cliente,cliente,cantidad_asientos,total_pagar," +
                "fecha_venta,id_butaca,estado,id_usuario,codigo_compra,metodo_pago)" +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?);";
        //la conexion es compartida: solo se cierra el statement
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getFuncion().getIdfuncion());
            if (v.getIdCliente() > 0) {
                st.setInt(2, v.getIdCliente());
            } else {
                st.setNull(2, Types.INTEGER);
            }
            st.setString(3, v.getCliente());
            st.setInt(4, v.getCantidadAsientos());
            st.setDouble(5, v.getTotalPagar());
            //LocalDate.toString devuelve "yyyy-MM-dd"
            st.setString(6, v.getFechaVenta().toString());
            st.setInt(7, v.getIdButaca());
            st.setString(8, v.getEstado());
            st.setInt(9, v.getIdUsuario());
            if (v.getCodigoCompra() != null && !v.getCodigoCompra().isBlank()) {
                st.setString(10, v.getCodigoCompra());
            } else {
                st.setNull(10, Types.VARCHAR);
            }
            st.setString(11, v.getMetodoPago() != null ? v.getMetodoPago() : "EFECTIVO");
            //true solo si la fila se guardo de verdad
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar venta:" + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Venta v) {
        String query = " UPDATE venta SET id_funcion=?, id_cliente=?, cliente=?, cantidad_asientos=?, " +
                " total_pagar=?, fecha_venta=?, id_butaca=?, estado=? WHERE idventa=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, v.getFuncion().getIdfuncion());
            if (v.getIdCliente() > 0) {
                st.setInt(2, v.getIdCliente());
            } else {
                st.setNull(2, Types.INTEGER);
            }
            st.setString(3, v.getCliente());
            st.setInt(4, v.getCantidadAsientos());
            st.setDouble(5, v.getTotalPagar());
            st.setString(6, v.getFechaVenta().toString());
            st.setInt(7, v.getIdButaca());
            st.setString(8, v.getEstado());
            st.setInt(9, v.getIdventa());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar venta:" + e.getMessage(), e);
        }
    }

    @Override
    public Venta searchById(int id) {
        Venta venta = null;
        String query = QUERY_BASE + " WHERE v.idventa=?;";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    venta = construirVenta(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta por Id:" + e.getMessage(), e);
        }
        return venta;
    }

    /**
     * Registra una venta por cada butaca elegida dentro de UNA sola transaccion.
     * Primero se apaga el autocommit para que nada se guarde hasta el final:
     *  - si el cliente no existe se inserta aqui mismo (si la venta falla, no queda el cliente solo)
     *  - se vuelve a comprobar que cada butaca siga libre
     *  - se genera el codigo de compra una sola vez para todos los boletos
     *  - al final se hace commit; si algo falla se hace rollback y no queda nada a medias
     */
    @Override
    public String registrarVentas(Funcion f, Cliente cliente, List<Butaca> butacas, int idUsuario,
                                  String metodoPago) {
        if (f == null || cliente == null || butacas == null || butacas.isEmpty()) {
            return null;
        }
        Connection cn = ConexionDB.getConnection();
        try {
            cn.setAutoCommit(false);

            //1) se reutiliza el cliente si ya existe un DNI igual
            int idCliente = cliente.getIdcliente();
            boolean clienteExistente = idCliente > 0;
            if (!clienteExistente) {
                idCliente = buscarIdClientePorDni(cn, cliente.getDni());
                if (idCliente > 0) {
                    clienteExistente = true;
                }
            }
            if (!clienteExistente) {
                idCliente = insertarCliente(cn, cliente);
            } else {
                //MEJORA 4b: si el cliente ya existia, sus datos se actualizan con
                //lo que se escribio en la venta (telefono, correo, nombres...).
                //Antes solo se reutilizaba su id y los datos nuevos se perdian.
                actualizarCliente(cn, cliente, idCliente);
            }
            //si no se pudo obtener el id del cliente no se vende nada:
            //nunca se guarda una venta con id_cliente = 0
            if (idCliente <= 0) {
                throw new SQLException("No se pudo obtener el id del cliente para la venta.");
            }

            //2) antes de confirmar se vuelve a comprobar que las butacas esten libres
            for (Butaca b : butacas) {
                if (butacaOcupada(cn, f.getIdfuncion(), b.getIdbutaca())) {
                    cn.rollback();
                    return null;
                }
            }

            //3) un codigo de compra para todos los boletos de esta venta
            String codigo = generarCodigoCompra(cn);

            //4) una venta por butaca (un boleto por fila), todas con el mismo codigo
            String nombreCliente = nombreCompleto(cliente);
            for (Butaca b : butacas) {
                boolean guardada = insertarVenta(cn, codigo, f, idCliente, nombreCliente,
                        b.getIdbutaca(), idUsuario, metodoPago);
                //si la butaca ya estaba vendida se deshace TODA la compra:
                //no quedan boletos sueltos ni el cliente creado
                if (!guardada) {
                    cn.rollback();
                    return null;
                }
            }

            cn.commit();
            cliente.setIdcliente(idCliente);
            return codigo;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException e2) {
                System.out.println("Error al revertir la venta:" + e2.getMessage());
            }
            throw new RuntimeException("Error al registrar la venta:" + e.getMessage(), e);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error al restaurar autocommit:" + e.getMessage());
            }
        }
    }

    //anular no borra la fila: solo cambia el estado y libera la butaca
    //MEJORA 6: ademas se guarda quien anulo, cuando y por que (auditoria)
    @Override
    public boolean anular(Venta v, int idUsuario, String motivo) {
        //CORREGIDO: el WHERE exige estado='activa'. Asi un boleto ya anulado no
        //se puede volver a anular: el UPDATE no toca ninguna fila y devuelve
        //false, aunque dos ventanas del sistema intenten anularlo a la vez.
        String query = "UPDATE venta SET estado='anulada', anulada_por=?, " +
                "fecha_anulacion=?, motivo_anulacion=? WHERE idventa=? AND estado='activa'";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idUsuario);
            st.setString(2, LocalDateTime.now().withNano(0).toString());
            st.setString(3, motivo);
            st.setInt(4, v.getIdventa());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al anular venta:" + e.getMessage(), e);
        }
    }

    //anula todos los boletos que comparten el mismo codigo de compra
    @Override
    public boolean anularCompra(String codigoCompra, int idUsuario, String motivo) {
        String query = "UPDATE venta SET estado='anulada', anulada_por=?, " +
                "fecha_anulacion=?, motivo_anulacion=? " +
                "WHERE COALESCE(codigo_compra,'V-' || idventa)=? AND estado='activa'";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idUsuario);
            st.setString(2, LocalDateTime.now().withNano(0).toString());
            st.setString(3, motivo);
            st.setString(4, codigoCompra);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al anular la compra:" + e.getMessage(), e);
        }
    }

    /**
     * MEJORA 3: historial de compras de un cliente.
     * Se reutiliza la consulta base (la misma de listAll) y solo se agrega
     * el filtro por id_cliente. Primero salen las compras mas nuevas.
     */
    @Override
    public List<Venta> listarPorCliente(int idCliente) {
        List<Venta> lista = new ArrayList<>();
        String query = QUERY_BASE + " WHERE v.id_cliente=? "
                + "ORDER BY v.fecha_venta DESC, v.idventa DESC";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idCliente);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirVenta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las compras del cliente: "
                    + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * MEJORA 9: todos los boletos de una compra, para reimprimir el comprobante.
     * Se acepta el codigo guardado o el "V-<id>" de las ventas viejas.
     */
    @Override
    public List<Venta> listarPorCodigo(String codigoCompra) {
        List<Venta> lista = new ArrayList<>();
        String query = QUERY_BASE
                + " WHERE COALESCE(v.codigo_compra,'V-' || v.idventa)=? ORDER BY v.idventa";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, codigoCompra);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirVenta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar la compra: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * MEJORA 7: anula todos los boletos activos de una funcion cancelada.
     * Se hace en una transaccion: o se anulan todos o no se anula ninguno.
     * Devuelve la cantidad de boletos anulados (0 si no habia ventas).
     */
    @Override
    public int anularPorFuncion(int idFuncion, int idUsuario, String motivo) {
        String query = "UPDATE venta SET estado='anulada', anulada_por=?, "
                + "fecha_anulacion=?, motivo_anulacion=? "
                + "WHERE id_funcion=? AND estado='activa'";
        Connection cn = ConexionDB.getConnection();
        try {
            cn.setAutoCommit(false);
            try (PreparedStatement st = cn.prepareStatement(query)) {
                st.setInt(1, idUsuario);
                st.setString(2, LocalDateTime.now().withNano(0).toString());
                st.setString(3, motivo);
                st.setInt(4, idFuncion);
                int anulados = st.executeUpdate();
                cn.commit();
                return anulados;
            }
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.out.println("No se pudo deshacer la anulacion: " + ex.getMessage());
            }
            throw new RuntimeException("Error al anular los boletos de la funcion: "
                    + e.getMessage(), e);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("No se pudo restaurar el autocommit: " + ex.getMessage());
            }
        }
    }

    @Override
    public List<Venta> listAll() {
        List<Venta> lista = new ArrayList<>();
        //se ordena por codigo para que los boletos de una misma compra salgan juntos
        String query = QUERY_BASE
                + " ORDER BY COALESCE(v.codigo_compra,'V-' || v.idventa), v.idventa";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(construirVenta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas:" + e.getMessage(), e);
        }
        return lista;
    }

    //busca el id del cliente por su DNI para no crear clientes repetidos
    private int buscarIdClientePorDni(Connection cn, String dni) throws SQLException {
        String query = "SELECT idcliente FROM cliente WHERE dni=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, dni);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idcliente");
                }
            }
        }
        return 0;
    }

    /**
     * MEJORA 4b: actualiza los datos de un cliente que ya existia.
     * Se hace DENTRO de la misma transaccion de la venta: si la venta falla,
     * el rollback tambien deshace este cambio.
     */
    private void actualizarCliente(Connection cn, Cliente c, int idCliente) throws SQLException {
        String query = "UPDATE cliente SET nombre=?, apellidos=?, email=?, genero=?, telefono=? " +
                "WHERE idcliente=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, c.getNombre());
            st.setString(2, c.getApellidos() != null ? c.getApellidos() : "");
            st.setString(3, c.getEmail());
            st.setString(4, c.getGenero());
            st.setString(5, c.getTelefono());
            st.setInt(6, idCliente);
            st.executeUpdate();
        }
    }

    //inserta el cliente y devuelve el id que genero la base de datos
    private int insertarCliente(Connection cn, Cliente c) throws SQLException {
        String query = "INSERT INTO cliente(nombre,apellidos,dni,email,genero,telefono)" +
                "VALUES(?,?,?,?,?,?)";
        try (PreparedStatement st = cn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, c.getNombre());
            st.setString(2, c.getApellidos() != null ? c.getApellidos() : "");
            st.setString(3, c.getDni());
            st.setString(4, c.getEmail());
            st.setString(5, c.getGenero());
            st.setString(6, c.getTelefono());
            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    if (id > 0) {
                        return id;
                    }
                }
            }
        }
        //sin id no se puede registrar la venta: se avisa para revertir la compra
        throw new SQLException("No se pudo obtener el id del cliente nuevo.");
    }

    private boolean butacaOcupada(Connection cn, int idFuncion, int idButaca) throws SQLException {
        String query = "SELECT COUNT(*) FROM venta WHERE id_funcion=? AND id_butaca=? AND estado='activa'";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idFuncion);
            st.setInt(2, idButaca);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Genera el codigo de la compra: toma el mayor numero usado ("C-0007" -> 7)
     * y le suma uno. Antes de devolverlo se revisa que ese codigo no exista, y si
     * existe se prueba el siguiente. Asi dos ventas seguidas no repiten codigo.
     */
    private String generarCodigoCompra(Connection cn) throws SQLException {
        int ultimo = 0;
        String query = "SELECT MAX(CAST(SUBSTR(codigo_compra,3) AS INTEGER)) FROM venta " +
                "WHERE codigo_compra LIKE 'C-%'";
        try (PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                ultimo = rs.getInt(1);
            }
        }
        //se prueban varios numeros por si el siguiente ya fue usado
        for (int intento = 0; intento < 50; intento++) {
            String codigo = String.format("C-%04d", ultimo + 1 + intento);
            if (!codigoExiste(cn, codigo)) {
                return codigo;
            }
        }
        throw new SQLException("No se pudo generar un codigo de compra libre.");
    }

    private boolean codigoExiste(Connection cn, String codigo) throws SQLException {
        String query = "SELECT COUNT(*) FROM venta WHERE codigo_compra=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, codigo);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Inserta un boleto solo si la butaca sigue libre en esa funcion.
     * El WHERE NOT EXISTS se revisa dentro del mismo INSERT, asi dos programas
     * abiertos a la vez no pueden guardar la misma butaca dos veces.
     * Devuelve true solo si se guardo la fila.
     */
    private boolean insertarVenta(Connection cn, String codigo, Funcion f, int idCliente,
                                  String nombreCliente, int idButaca, int idUsuario,
                                  String metodoPago) throws SQLException {
        String query = "INSERT INTO venta(id_funcion,id_cliente,cliente,cantidad_asientos,total_pagar," +
                "fecha_venta,id_butaca,estado,id_usuario,codigo_compra,metodo_pago) " +
                "SELECT ?,?,?,?,?,?,?,?,?,?,? " +
                "WHERE NOT EXISTS (SELECT 1 FROM venta " +
                "WHERE id_funcion=? AND id_butaca=? AND estado='activa')";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            st.setInt(2, idCliente);
            st.setString(3, nombreCliente);
            //cada fila es un boleto, por eso cantidad_asientos siempre vale 1
            st.setInt(4, 1);
            st.setDouble(5, f.getPrecio());
            st.setString(6, LocalDate.now().toString());
            st.setInt(7, idButaca);
            st.setString(8, "activa");
            st.setInt(9, idUsuario);
            st.setString(10, codigo);
            st.setString(11, metodoPago != null ? metodoPago : "EFECTIVO");
            st.setInt(12, f.getIdfuncion());
            st.setInt(13, idButaca);
            return st.executeUpdate() > 0;
        }
    }

    private String nombreCompleto(Cliente c) {
        String apellidos = c.getApellidos() != null ? c.getApellidos().trim() : "";
        if (apellidos.isEmpty()) {
            return c.getNombre();
        }
        return c.getNombre() + " " + apellidos;
    }

    private Venta construirVenta(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();
        p.setidpelicula(rs.getInt("id_pelicula"));
        p.setNumero(rs.getInt("numero_pelicula"));
        p.setTitulo(rs.getString("nombre_pelicula"));
        p.setDuracion(rs.getString("duracion"));
        p.setCategoria(rs.getString("categoria"));
        p.setClasificacion(rs.getString("clasificacion"));

        Sala s = new Sala();
        s.setIdsala(rs.getInt("id_sala"));
        s.setNumero(rs.getInt("numero_sala"));

        s.setTipoSala(rs.getString("tipo"));
        s.setCapacidad(rs.getInt("capacidad"));
        s.setFilas(rs.getInt("filas_sala"));
        s.setColumnas(rs.getInt("columnas_sala"));
        s.setEstado(rs.getString("estado_sala"));

        Funcion f = new Funcion();
        f.setIdfuncion(rs.getInt("id_funcion"));
        f.setFecha(rs.getString("fecha_funcion"));
        f.setHorario(rs.getString("horario"));
        f.setPrecio(rs.getDouble("precio"));
        f.setPelicula(p);
        f.setSala(s);

        Venta v = new Venta();
        v.setIdventa(rs.getInt("idventa"));
        v.setIdCliente(rs.getInt("id_cliente"));
        v.setCodigoCompra(rs.getString("codigo_compra"));
        //el nombre se toma del cliente por su id; si no hay id se usa el texto viejo
        String nombreCliente = rs.getString("nombre_cliente");
        String apellidos = rs.getString("apellidos_cliente");
        if (nombreCliente != null && apellidos != null && !apellidos.isBlank()) {
            nombreCliente = nombreCliente + " " + apellidos;
        }
        v.setCliente(nombreCliente != null ? nombreCliente : rs.getString("cliente"));
        v.setDniCliente(rs.getString("dni_cliente") != null ? rs.getString("dni_cliente") : "");
        v.setEmpleado(rs.getString("nombre_empleado") != null ? rs.getString("nombre_empleado") : "-");
        v.setCantidadAsientos(rs.getInt("cantidad_asientos"));
        v.setTotalPagar(rs.getDouble("total_pagar"));
        v.setFechaVenta(parseFecha(rs.getString("fecha_venta")));
        v.setIdUsuario(rs.getInt("id_usuario"));
        v.setEstado(rs.getString("estado") != null ? rs.getString("estado") : "activa");
        //MEJORA 6: forma de pago y resumen de la anulacion para la tabla
        v.setMetodoPago(rs.getString("metodo_pago") != null
                ? rs.getString("metodo_pago") : "EFECTIVO");
        String nombreAnulador = rs.getString("nombre_anulador");
        String motivoAnulacion = rs.getString("motivo_anulacion");
        String textoAnulacion = "";
        if (nombreAnulador != null) {
            textoAnulacion = nombreAnulador;
        }
        if (motivoAnulacion != null && !motivoAnulacion.isBlank()) {
            textoAnulacion = textoAnulacion.isEmpty()
                    ? motivoAnulacion : textoAnulacion + " - " + motivoAnulacion;
        }
        v.setAnulacion(textoAnulacion);
        v.setIdButaca(rs.getInt("id_butaca"));
        //butaca solo para mostrar fila y numero
        int fila = rs.getInt("butaca_fila");
        int numero = rs.getInt("butaca_numero");
        if (fila > 0 && numero > 0) {
            Butaca b = new Butaca(fila, numero);
            b.setIdbutaca(rs.getInt("id_butaca"));
            v.setButaca(b);
        }
        v.setFuncion(f);
        return v;
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha.substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }
}
