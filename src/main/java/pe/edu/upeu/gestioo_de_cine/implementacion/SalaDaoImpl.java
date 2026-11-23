package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;

import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl implements ISalaDao {
    @Override
    public boolean insert(Sala s) {
        boolean flag = false;
        //la tabla sala usa numero y tipo_sala; ya no existe la columna nombre
        String query = "INSERT INTO sala(numero,tipo_sala,capacidad,estado)"
                + " VALUES(?,?,?,?); ";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getNumero());
            st.setString(2, s.getTipoSala());
            st.setInt(3, s.getCapacidad());
            st.setString(4, s.getEstado());
            //true solo si se guardo una fila
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar sala:" + e.getMessage(), e);
        }
        return flag;
    }


    @Override
    public boolean update(Sala s) {
        boolean flag = false;
        String query = " UPDATE sala SET numero=?, tipo_sala=?, " +
                "capacidad=?, estado=? " +
                " WHERE idsala=?; ";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getNumero());
            st.setString(2, s.getTipoSala());
            st.setInt(3, s.getCapacidad());
            st.setString(4, s.getEstado());
            st.setInt(5, s.getIdsala());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar sala: " + e.getMessage(), e);
        }
        return flag;

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

        //la sala esta amarrada a sus funciones y butacas: se revisa antes de borrar
        String queryFunciones = "SELECT COUNT(*) FROM funcion WHERE id_sala=?";
        try (PreparedStatement st = cn.prepareStatement(queryFunciones)) {
            st.setInt(1, s.getIdsala());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("La sala " + s.getNumero() + " tiene "
                            + rs.getInt(1) + " función(es) programada(s).\n"
                            + "Elimine primero esas funciones en el módulo Funciones.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al revisar las funciones de la sala: "
                    + e.getMessage(), e);
        }

        //las butacas pertenecen a la sala, se borran junto con ella
        String queryButacas = "DELETE FROM butaca WHERE id_sala=?";
        String query = "DELETE FROM sala WHERE idsala=?";
        try {
            //si algo falla no queda la sala a medio borrar
            cn.setAutoCommit(false);
            try (PreparedStatement st = cn.prepareStatement(queryButacas)) {
                st.setInt(1, s.getIdsala());
                st.executeUpdate();
            }
            try (PreparedStatement st = cn.prepareStatement(query)) {
                st.setInt(1, s.getIdsala());
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
                s.setEstado(rs.getString("estado"));
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar salas: " + e.getMessage(), e);
        }

        return lista;
    }
}
