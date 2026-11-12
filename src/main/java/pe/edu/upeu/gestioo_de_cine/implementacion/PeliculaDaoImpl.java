package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IPeliculaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDaoImpl implements IPeliculaDao {
    @Override
    public boolean insert(Pelicula p) {
        boolean flag = false;
        String query = "INSERT INTO pelicula(numero,nombre,duracion,categoria,clasificacion)"
                + " VALUES(?,?,?,?,?); ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getNumero());
            st.setString(2, p.getNombre());
            st.setString(3, p.getDuracion());
            st.setString(4, p.getCategoria());
            st.setString(5, p.getClasificacion());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar pelicula:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Pelicula p) {
        boolean flag = false;
        String query = " UPDATE pelicula SET numero=?, nombre =?, duracion=?, " +
                "categoria=?, clasificacion=? " +
                " WHERE idpelicula=?; ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getNumero());
            st.setString(2, p.getNombre());
            st.setString(3, p.getDuracion());
            st.setString(4, p.getCategoria());
            st.setString(5, p.getClasificacion());
            st.setInt(6, p.getidpelicula());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar película: " + e.getMessage());
        }
        return flag;

    }

    @Override
    public Pelicula searchById(int id) {
        Pelicula p = null;

        String query = "SELECT * FROM pelicula WHERE idpelicula = ?; ";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    p = new Pelicula();
                    p.setidpelicula(rs.getInt("idpelicula"));
                    p.setNumero(rs.getInt("numero"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDuracion(rs.getString("duracion"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setClasificacion(rs.getString("clasificacion"));

                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar películapor ID: " + e.getMessage());
        }

        return p;
    }

    @Override
    public Pelicula searchByNum(int num) {
        Pelicula p = null;
        String query = " SELECT * FROM pelicula WHERE numero = ?; ";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, num);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    p = new Pelicula();
                    p.setidpelicula(rs.getInt("idpelicula"));
                    p.setNumero(rs.getInt("numero"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDuracion(rs.getString("duracion"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setClasificacion(rs.getString("clasificacion"));

                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar película por numero: " + e.getMessage());
        }

        return p;
    }


    @Override
    public boolean delete(Pelicula c) {
        boolean flag = false;
        String query = " DELETE FROM pelicula WHERE idpelicula=?; ";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, c.getidpelicula());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar película: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Pelicula> listAll() {
        List<Pelicula> lista = new ArrayList<>();
        String query = "SELECT * FROM pelicula";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setidpelicula(rs.getInt("idpelicula"));
                p.setNumero(rs.getInt("numero"));
                p.setNombre(rs.getString("nombre"));
                p.setDuracion(rs.getString("duracion"));
                p.setCategoria(rs.getString("categoria"));
                p.setClasificacion(rs.getString("clasificacion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar películas: " + e.getMessage());
        }

        return lista;
    }
}

