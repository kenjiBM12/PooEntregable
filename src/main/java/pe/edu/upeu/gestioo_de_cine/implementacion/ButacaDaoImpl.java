package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IButacaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ButacaDaoImpl implements IButacaDao {

    @Override
    public List<Butaca> listarPorSala(int idSala) {
        List<Butaca> lista = new ArrayList<>();
        String query = "SELECT * FROM butaca WHERE id_sala=? ORDER BY fila, numero";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idSala);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Butaca b = new Butaca();
                    b.setIdbutaca(rs.getInt("idbutaca"));
                    b.setIdSala(rs.getInt("id_sala"));
                    b.setFila(rs.getInt("fila"));
                    b.setNumero(rs.getInt("numero"));
                    lista.add(b);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar butacas:" + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Butaca> listarOcupadas(int idFuncion) {
        List<Butaca> lista = new ArrayList<>();
        String query = "SELECT b.idbutaca,b.id_sala,b.fila,b.numero FROM butaca b " +
                "INNER JOIN venta v ON v.id_butaca = b.idbutaca " +
                "WHERE v.id_funcion=? AND v.estado='activa'";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idFuncion);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Butaca b = new Butaca();
                    b.setIdbutaca(rs.getInt("idbutaca"));
                    b.setIdSala(rs.getInt("id_sala"));
                    b.setFila(rs.getInt("fila"));
                    b.setNumero(rs.getInt("numero"));
                    b.setOcupada(true);
                    lista.add(b);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar butacas ocupadas:" + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public boolean tieneButacas(int idSala) {
        return contarPorSala(idSala) > 0;
    }

    @Override
    public int contarPorSala(int idSala) {
        String query = "SELECT COUNT(*) FROM butaca WHERE id_sala=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idSala);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar butacas:" + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean insertar(Butaca b) {
        Connection cn = ConexionDB.getConnection();
        try {
            return insertarButaca(cn, b.getIdSala(), b.getFila(), b.getNumero());
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar butaca:" + e.getMessage(), e);
        }
    }

    @Override
    public int generarButacas(int idSala, int filas, int columnas, int capacidad) {
        if (filas <= 0 || columnas <= 0) {
            throw new RuntimeException("Las filas y las columnas deben ser mayores que cero.");
        }
        long total = (long) filas * (long) columnas;
        if (capacidad > 0 && total > capacidad) {
            throw new RuntimeException("La sala solo tiene capacidad para " + capacidad + " butacas.");
        }

        Connection cn = ConexionDB.getConnection();
        int creadas = 0;
        try {
            cn.setAutoCommit(false);
            for (int f = 1; f <= filas; f++) {
                for (int c = 1; c <= columnas; c++) {
                    if (!existeButaca(cn, idSala, f, c)) {
                        insertarButaca(cn, idSala, f, c);
                        creadas++;
                    }
                }
            }
            cn.commit();
            return creadas;
        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException e2) {
                System.out.println("Error al revertir butacas:" + e2.getMessage());
            }
            throw new RuntimeException("Error al generar butacas:" + e.getMessage(), e);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error al restaurar autocommit:" + e.getMessage());
            }
        }
    }

    private boolean existeButaca(Connection cn, int idSala, int fila, int numero) throws SQLException {
        String query = "SELECT COUNT(*) FROM butaca WHERE id_sala=? AND fila=? AND numero=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idSala);
            st.setInt(2, fila);
            st.setInt(3, numero);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean insertarButaca(Connection cn, int idSala, int fila, int numero) throws SQLException {
        boolean flag = false;
        String query = "INSERT INTO butaca(id_sala,fila,numero)VALUES(?,?,?)";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idSala);
            st.setInt(2, fila);
            st.setInt(3, numero);
            flag = st.executeUpdate() > 0;
        }
        return flag;
    }
}
