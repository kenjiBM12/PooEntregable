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
        String query = "INSERT INTO cliente(nombre,apellidos,dni,email,genero,telefono)VALUES(?,?,?,?,?,?);";
        //la conexion es compartida: solo se cierra el statement
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1,c.getNombre());
            st.setString(2,c.getApellidos());
            st.setString(3,c.getDni());
            st.setString(4,c.getEmail());
            st.setString(5,c.getGenero());
            st.setString(6,c.getTelefono());
            //executeUpdate devuelve cuantas filas se guardaron: si es 0 no se hizo nada
            int filas = st.executeUpdate();
            if (filas > 0) {
                //se guarda el id generado para poder usarlo en la venta
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdcliente(rs.getInt(1));
                    }
                }
            }
            return filas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar Cliente:"+e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Cliente c) {
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
            //true solo si realmente se modifico una fila
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente:"+e.getMessage(), e);
        }
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
        String query = "DELETE FROM cliente WHERE idcliente=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1,c.getIdcliente());
            //true solo si realmente se borro una fila
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente:" + e.getMessage(), e);
        }
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
