package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;

import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISalaDao {
    @Override
    public boolean insert(Sala s) {
        boolean flag = false;
        //CORREGIDO: la capacidad no se escribe a mano, sale de filas x columnas
        validarCuadricula(s);
        //la tabla sala usa numero y tipo_sala; ya no existe la columna nombre
        String query = "INSERT INTO sala(numero,tipo_sala,capacidad,estado,filas,columnas)"
                + " VALUES(?,?,?,?,?,?); ";
        Connection cn = ConexionDB.getConnection();
        try {
            //CORREGIDO: antes solo se guardaba la sala. Ahora la sala, su
            //capacidad y TODAS sus butacas se guardan en una sola transaccion:
            //si algo falla se deshace todo y no queda una sala a medias.
            cn.setAutoCommit(false);
            int idSala = 0;
            try (PreparedStatement st = cn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                st.setInt(1, s.getNumero());
                st.setString(2, s.getTipoSala());
                st.setInt(3, s.getCapacidad());
                st.setString(4, s.getEstado());
                st.setInt(5, s.getFilas());
                st.setInt(6, s.getColumnas());
                //true solo si se guardo una fila de verdad
                flag = st.executeUpdate() > 0;
                //id que genero la base para la sala nueva
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        idSala = rs.getInt(1);
                    }
                }
            }
            if (idSala <= 0) {
                throw new SQLException("No se pudo obtener el id de la sala nueva.");
            }
            insertarButacas(cn, idSala, s.getFilas(), s.getColumnas());
            cn.commit();
            s.setIdsala(idSala);
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al revertir el alta de la sala: " + ex.getMessage());
            }
            throw new RuntimeException(mensajeErrorSala(e, s.getNumero()), e);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error al reactivar el autocommit: " + ex.getMessage());
            }
        }
        return flag;
    }


    @Override
    public boolean update(Sala s) {
        boolean flag = false;
        validarCuadricula(s);
        Connection cn = ConexionDB.getConnection();

        //si la cuadricula se achica, las butacas que se quitarian no deben tener
        //ventas: si las tienen se rechaza el cambio para no perder el historial
        String queryVendidas = "SELECT COUNT(*) FROM butaca b "
                + "INNER JOIN venta v ON v.id_butaca=b.idbutaca "
                + "WHERE b.id_sala=? AND (b.fila>? OR b.numero>?)";
        try (PreparedStatement st = cn.prepareStatement(queryVendidas)) {
            st.setInt(1, s.getIdsala());
            st.setInt(2, s.getFilas());
            st.setInt(3, s.getColumnas());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("No se puede cambiar la cuadricula: "
                            + rs.getInt(1) + " butaca(s) que se quitarian ya tienen "
                            + "boletos vendidos.\nSe conserva el historial de ventas.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al revisar las butacas de la sala: "
                    + e.getMessage(), e);
        }

        String query = " UPDATE sala SET numero=?, tipo_sala=?, " +
                "capacidad=?, estado=?, filas=?, columnas=? " +
                " WHERE idsala=?; ";
        try {
            //CORREGIDO: al editar filas o columnas tambien se sincroniza la
            //cuadricula de butacas (se quitan las que sobran y se agregan las
            //que faltan) y se actualiza la capacidad, todo en una transaccion
            cn.setAutoCommit(false);
            try (PreparedStatement st = cn.prepareStatement(
                    "DELETE FROM butaca WHERE id_sala=? AND (fila>? OR numero>?)")) {
                st.setInt(1, s.getIdsala());
                st.setInt(2, s.getFilas());
                st.setInt(3, s.getColumnas());
                st.executeUpdate();
            }
            insertarButacas(cn, s.getIdsala(), s.getFilas(), s.getColumnas());
            try (PreparedStatement st = cn.prepareStatement(query)) {
                st.setInt(1, s.getNumero());
                st.setString(2, s.getTipoSala());
                st.setInt(3, s.getCapacidad());
                st.setString(4, s.getEstado());
                st.setInt(5, s.getFilas());
                st.setInt(6, s.getColumnas());
                st.setInt(7, s.getIdsala());
                //antes decia "guardado" aunque el id no existiera
                flag = st.executeUpdate() > 0;
            }
            cn.commit();
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al revertir el cambio de la sala: " + ex.getMessage());
            }
            throw new RuntimeException(mensajeErrorSala(e, s.getNumero()), e);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error al reactivar el autocommit: " + ex.getMessage());
            }
        }
        return flag;
    }

    //revisa filas y columnas antes de multiplicar y guarda la capacidad
    private void validarCuadricula(Sala s) {
        if (s.getFilas() <= 0 || s.getColumnas() <= 0) {
            throw new RuntimeException("Las filas y las columnas deben ser mayores que cero.");
        }
        //se multiplica con long para controlar valores enormes
        long total = (long) s.getFilas() * (long) s.getColumnas();
        if (total > 10000) {
            throw new RuntimeException("La cuadricula es demasiado grande. "
                    + "El maximo es 100 x 100 butacas.");
        }
        s.setCapacidad((int) total);
    }

    //inserta solo las butacas que falten dentro de la cuadricula de la sala
    private void insertarButacas(Connection cn, int idSala, int filas, int columnas)
            throws SQLException {
        String query = "INSERT INTO butaca(id_sala,fila,numero) SELECT ?,?,? "
                + "WHERE NOT EXISTS(SELECT 1 FROM butaca "
                + "WHERE id_sala=? AND fila=? AND numero=?)";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            for (int f = 1; f <= filas; f++) {
                for (int c = 1; c <= columnas; c++) {
                    st.setInt(1, idSala);
                    st.setInt(2, f);
                    st.setInt(3, c);
                    st.setInt(4, idSala);
                    st.setInt(5, f);
                    st.setInt(6, c);
                    st.executeUpdate();
                }
            }
        }
    }

    //mensaje claro cuando el numero de sala ya esta usado
    private String mensajeErrorSala(SQLException e, int numero) {
        if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
            return "Ya existe una sala con el numero " + numero + ".";
        }
        return "Error al guardar la sala: " + e.getMessage();
    }

    @Override
    public Sala searchById(int id) {
        Sala s = null;

        String query = "SELECT * FROM sala WHERE idsala = ?";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    s = new Sala();
                    s.setIdsala(rs.getInt("idsala"));
                    s.setNumero(rs.getInt("numero"));
                    s.setTipoSala(rs.getString("tipo_sala"));
                    s.setCapacidad(rs.getInt("capacidad"));
                    s.setFilas(rs.getInt("filas"));
                    s.setColumnas(rs.getInt("columnas"));
                    s.setEstado(rs.getString("estado"));

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar sala por ID: " + e.getMessage(), e);
        }

        return s;
    }

    @Override
    public Sala searchByNum(int num) {
        Sala s = null;
        String query = "SELECT * FROM sala WHERE numero = ?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, num);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    s = new Sala();
                    s.setIdsala(rs.getInt("idsala"));
                    s.setNumero(rs.getInt("numero"));
                    s.setTipoSala(rs.getString("tipo_sala"));
                    s.setCapacidad(rs.getInt("capacidad"));
                    s.setFilas(rs.getInt("filas"));
                    s.setColumnas(rs.getInt("columnas"));
                    s.setEstado(rs.getString("estado"));

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar sala por numero: " + e.getMessage(), e);
        }

        return s;
    }

    @Override
    public boolean delete(Sala s) {
        boolean flag = false;
        Connection cn = ConexionDB.getConnection();

        //CORREGIDO: antes no se podia borrar ninguna sala que tuviera funciones:
        //habia que borrarlas a mano una por una. Ahora solo se bloquea si alguna
        //de esas funciones ya vendio boletos (para no perder el historial); si
        //ninguna vendio, las funciones se borran junto con la sala.
        String queryVendidas = "SELECT COUNT(*) FROM funcion f WHERE f.id_sala=? "
                + "AND EXISTS(SELECT 1 FROM venta v WHERE v.id_funcion=f.idfuncion)";
        try (PreparedStatement st = cn.prepareStatement(queryVendidas)) {
            st.setInt(1, s.getIdsala());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("La sala " + s.getNumero() + " tiene "
                            + rs.getInt(1) + " función(es) con boletos vendidos\n"
                            + "y no se puede eliminar. Se conserva el historial de ventas.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al revisar las funciones de la sala: "
                    + e.getMessage(), e);
        }

        //las butacas y las funciones pertenecen a la sala, se borran junto con ella
        //(las funciones se borran primero por la llave foranea hacia sala)
        String queryFunciones = "DELETE FROM funcion WHERE id_sala=?";
        String queryButacas = "DELETE FROM butaca WHERE id_sala=?";
        String query = "DELETE FROM sala WHERE idsala=?";
        try {
            //si algo falla no queda la sala a medio borrar
            cn.setAutoCommit(false);
            try (PreparedStatement st = cn.prepareStatement(queryFunciones)) {
                st.setInt(1, s.getIdsala());
                st.executeUpdate();
            }
            try (PreparedStatement st = cn.prepareStatement(queryButacas)) {
                st.setInt(1, s.getIdsala());
                st.executeUpdate();
            }
            try (PreparedStatement st = cn.prepareStatement(query)) {
                st.setInt(1, s.getIdsala());
                //CORREGIDO: true solo si realmente se borro la sala
                flag = st.executeUpdate() > 0;
            }
            cn.commit();
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al revertir el borrado de la sala: " + ex.getMessage());
            }
            throw new RuntimeException("Error al eliminar sala: " + e.getMessage(), e);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error al reactivar el autocommit: " + ex.getMessage());
            }
        }
        return flag;
    }
    @Override
    public List<Sala> listAll() {
        List<Sala> lista = new ArrayList<>();
        String query = "SELECT * FROM sala ORDER BY numero";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Sala s = new Sala();
                s.setIdsala(rs.getInt("idsala"));
                s.setNumero(rs.getInt("numero"));
                s.setTipoSala(rs.getString("tipo_sala"));
                s.setCapacidad(rs.getInt("capacidad"));
                s.setFilas(rs.getInt("filas"));
                s.setColumnas(rs.getInt("columnas"));
                s.setEstado(rs.getString("estado"));
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar salas: " + e.getMessage(), e);
        }

        return lista;
    }
}
