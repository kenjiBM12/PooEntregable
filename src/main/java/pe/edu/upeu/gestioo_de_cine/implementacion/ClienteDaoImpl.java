package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IClienteDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoImpl implements IClienteDao {

    @Override
    public boolean insert(Cliente c) {
        boolean flag = false;
        String query = "INSERT INTO cliente(nombre,apellidos,dni,email,genero,telefono)VALUES(?,?,?,?,?,?);";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1,c.getNombre());
            st.setString(2,c.getApellidos());
            st.setString(3,c.getDni());
            st.setString(4,c.getEmail());
            st.setString(5,c.getGenero());
            st.setString(6,c.getTelefono());
            int filas = st.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdcliente(rs.getInt(1));
                    }
                }
            }
            flag = filas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar Cliente:"+e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public boolean update(Cliente c) {
        boolean flag = false;
        String query = " UPDATE cliente SET nombre=?, apellidos =?, dni=?, email=?, genero=?, telefono=? " +
                " WHERE idcliente=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,c.getNombre());
            st.setString(2,c.getApellidos());
            st.setString(3,c.getDni());
            st.setString(4,c.getEmail());
            st.setString(5,c.getGenero());
            st.setString(6,c.getTelefono());
            st.setInt(7,c.getIdcliente());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente:"+e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public Cliente searchById(int id) {
        Cliente cl = null;
        String query = "SELECT * FROM cliente WHERE idcliente=?;";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,id);
            try( ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    cl = construirCliente(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cliente por Id:" + e.getMessage(), e);
        }
        return cl;
    }

    @Override
    public boolean delete(Cliente c) {
        boolean flag = false;
        Connection cn = ConexionDB.getConnection();

        String queryVentas = "SELECT COUNT(*) FROM venta WHERE id_cliente=?";
        try (PreparedStatement st = cn.prepareStatement(queryVentas)) {
            st.setInt(1, c.getIdcliente());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("El cliente " + c.getNombre()
                            + " tiene " + rs.getInt(1) + " boleto(s) vendido(s).\n"
                            + "No se puede eliminar: se perdería el historial de compras.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al revisar las ventas del cliente:"
                    + e.getMessage(), e);
        }

        String query = "DELETE FROM cliente WHERE idcliente=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,c.getIdcliente());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public List<Cliente> listAll() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT * FROM cliente;";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query);
        ResultSet rs = st.executeQuery()) {
            while (rs.next()){
                lista.add(construirCliente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cliente:" + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Cliente searchByDni(String dni) {
        Cliente cl = null;
        String query = "SELECT * FROM cliente WHERE dni=?;";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,dni);
            try( ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    cl = construirCliente(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por DNI:" + e.getMessage(), e);
        }
        return cl;
    }

    private Cliente construirCliente(ResultSet rs) throws SQLException {
        Cliente cl = new Cliente();
        cl.setIdcliente(rs.getInt("idcliente"));
        cl.setNombre(rs.getString("nombre"));
        cl.setApellidos(rs.getString("apellidos"));
        cl.setDni(rs.getString("dni"));
        cl.setEmail(rs.getString("email"));
        cl.setGenero(rs.getString("genero"));
        cl.setTelefono(rs.getString("telefono"));
        return cl;
    }

}
