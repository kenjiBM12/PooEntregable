package pe.edu.upeu.project_final.implementacion;

import pe.edu.upeu.project_final.interfaces.IFuncionDao;
import pe.edu.upeu.project_final.modelo.Funcion;
import pe.edu.upeu.project_final.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {

    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;
        String query = "INSERT INTO funcion(id_pelicula,id_sala,horario,precio)VALUES(?,?,?,?);";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdPelicula());
            st.setInt(2, f.getIdSala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar funcion:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;
        String query = " UPDATE funcion SET id_pelicula=?, id_sala=?, horario=?, precio=? " +
                " WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdPelicula());
            st.setInt(2, f.getIdSala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar funcion:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Funcion searchById(int id) {
        Funcion fn = null;
        String query = "SELECT * FROM funcion WHERE id=?;";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    fn = new Funcion();
                    fn.setId(rs.getInt("id"));
                    fn.setIdPelicula(rs.getInt("id_pelicula"));
                    fn.setIdSala(rs.getInt("id_sala"));
                    fn.setHorario(rs.getString("horario"));
                    fn.setPrecio(rs.getDouble("precio"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar funcion por Id:" + e.getMessage());
        }
        return fn;
    }

    @Override
    public boolean delete(Funcion f) {
        boolean flag = false;
        String query = "DELETE FROM funcion WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar funcion:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();
        String query = "SELECT * FROM funcion;";
        try (
                Connection cn = ConexionDB.getConnection();
                PreparedStatement st = cn.prepareStatement(query);
                ResultSet rs = st.executeQuery();
        ) {
            while (rs.next()) {
                Funcion fn = new Funcion();
                fn.setId(rs.getInt("id"));
                fn.setIdPelicula(rs.getInt("id_pelicula"));
                fn.setIdSala(rs.getInt("id_sala"));
                fn.setHorario(rs.getString("horario"));
                fn.setPrecio(rs.getDouble("precio"));
                lista.add(fn);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar funcion:" + e.getMessage());
        }
        return lista;
    }
}
