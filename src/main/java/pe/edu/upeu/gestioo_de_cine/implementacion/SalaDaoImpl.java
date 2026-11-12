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
        /*
             boolean flag = false;
        String query = "INSERT INTO cliente(nombre,apellidos,dni,email,genero)VALUES(?,?,?,?,?);";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1,c.getNombre());
            st.setString(2,c.getApellidos());
            st.setString(3,c.getDni());
            st.setString(4,c.getEmail());
            st.setString(5,c.getGenero());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }
         */
        boolean flag = false;
        String query = "INSERT INTO sala(numero,nombre,tipo,capacidad,estado)"
                + " VALUES(?,?,?,?,?); ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getNumero());
            st.setString(2, s.getNombre());
            st.setString(3, s.getTipo());
            st.setInt(4, s.getCapacidad());
            st.setString(5, s.getEstado());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar sala:" + e.getMessage());
        }
        return flag;
    }


    @Override
    public boolean update(Sala s) {
        boolean flag = false;
        String query = " UPDATE sala SET numero=?, nombre =?, tipo=?, " +
                "capacidad=?, estado=? " +
                " WHERE idsala=?; ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getNumero());
            st.setString(2, s.getNombre());
            st.setString(3, s.getTipo());
            st.setInt(4, s.getCapacidad());
            st.setString(5, s.getEstado());
            st.setInt(6, s.getIdsala());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar sala: " + e.getMessage());
        }
        return flag;

    }

    @Override
    public Sala searchById(int id) {
        Sala s = null;

        String query = "SELECT * FROM sala WHERE idsala = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    s = new Sala();
                    s.setIdsala(rs.getInt("idsala"));
                    s.setNumero(rs.getInt("numero"));
                    s.setNombre(rs.getString("nombre"));
                    s.setTipo(rs.getString("tipo"));
                    s.setCapacidad(rs.getInt("capacidad"));
                    s.setEstado(rs.getString("estado"));

                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar sala por ID: " + e.getMessage());
        }

        return s;
    }

    @Override
    public Sala searchByNum(int num) {
        Sala s = null;

        String query = "SELECT * FROM sala WHERE numero = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, num);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    s = new Sala();
                    s.setIdsala(rs.getInt("idsala"));
                    s.setNumero(rs.getInt("numero"));
                    s.setNombre(rs.getString("nombre"));
                    s.setTipo(rs.getString("tipo"));
                    s.setCapacidad(rs.getInt("capacidad"));
                    s.setEstado(rs.getString("estado"));

                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar sala por numero: " + e.getMessage());
        }

        return s;
    }

    @Override
    public boolean delete(Sala s) {
        boolean flag = false;
        String query = "DELETE FROM sala WHERE idsala=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, s.getIdsala());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar sala: " + e.getMessage());
        }
        return flag;
    }
    @Override
    public List<Sala> listAll() {
        List<Sala> lista = new ArrayList<>();
        String query = "SELECT * FROM sala";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Sala s = new Sala();
                s.setIdsala(rs.getInt("idsala"));
                s.setNumero(rs.getInt("numero"));
                s.setNombre(rs.getString("nombre"));
                s.setTipo(rs.getString("tipo"));
                s.setCapacidad(rs.getInt("capacidad"));
                s.setEstado(rs.getString("estado"));
                lista.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar salas: " + e.getMessage());
        }

        return lista;
    }
}
