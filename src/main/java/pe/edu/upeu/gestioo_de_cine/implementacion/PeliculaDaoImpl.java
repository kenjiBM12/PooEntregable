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
        //la columna de la base se llama titulo, igual que la propiedad de Java
        String query = "INSERT INTO pelicula(numero,titulo,duracion,categoria,clasificacion)"
                + " VALUES(?,?,?,?,?); ";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getNumero());
            st.setString(2, p.getTitulo());
            st.setString(3, p.getDuracion());
            st.setString(4, p.getCategoria());
            st.setString(5, p.getClasificacion());
            //true solo si se guardo una fila
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar pelicula:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public boolean update(Pelicula p) {
        boolean flag = false;
        String query = " UPDATE pelicula SET numero=?, titulo=?, duracion=?, " +
                "categoria=?, clasificacion=? " +
                " WHERE idpelicula=?; ";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, p.getNumero());
            st.setString(2, p.getTitulo());
            st.setString(3, p.getDuracion());
            st.setString(4, p.getCategoria());
            st.setString(5, p.getClasificacion());
            st.setInt(6, p.getidpelicula());
            st.executeUpdate();
            flag=true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar película: " + e.getMessage(), e);
        }
        return flag;

    }

    @Override
    public Pelicula searchById(int id) {
        Pelicula p = null;

        String query = "SELECT * FROM pelicula WHERE idpelicula = ?; ";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    p = new Pelicula();
                    p.setidpelicula(rs.getInt("idpelicula"));
                    p.setNumero(rs.getInt("numero"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setDuracion(rs.getString("duracion"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setClasificacion(rs.getString("clasificacion"));

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar películapor ID: " + e.getMessage(), e);
        }

        return p;
    }

    @Override
    public Pelicula searchByNum(int num) {
        Pelicula p = null;
        String query = " SELECT * FROM pelicula WHERE numero = ?; ";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {

            st.setInt(1, num);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    p = new Pelicula();
                    p.setidpelicula(rs.getInt("idpelicula"));
                    p.setNumero(rs.getInt("numero"));
                    p.setTitulo(rs.getString("titulo"));
                    p.setDuracion(rs.getString("duracion"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setClasificacion(rs.getString("clasificacion"));

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar película por numero: " + e.getMessage(), e);
        }

        return p;
    }


    @Override
    public boolean delete(Pelicula c) {
        boolean flag = false;
        Connection cn = ConexionDB.getConnection();
        try {
            //todo el borrado va en una sola transaccion: si algo falla no queda nada a medias
            cn.setAutoCommit(false);

            //1. se borran las ventas de las funciones de esta pelicula
            String queryVentas = "DELETE FROM venta WHERE id_funcion IN "
                    + "(SELECT idfuncion FROM funcion WHERE id_pelicula=?)";
            try (PreparedStatement st = cn.prepareStatement(queryVentas)) {
                st.setInt(1, c.getidpelicula());
                st.executeUpdate();
            }

            //2. se borran las funciones de la pelicula
            String queryFunciones = "DELETE FROM funcion WHERE id_pelicula=?";
            try (PreparedStatement st = cn.prepareStatement(queryFunciones)) {
                st.setInt(1, c.getidpelicula());
                st.executeUpdate();
            }

            //3. recien ahora se puede borrar la pelicula
            String query = "DELETE FROM pelicula WHERE idpelicula=?";
            try (PreparedStatement st = cn.prepareStatement(query)) {
                st.setInt(1, c.getidpelicula());
                //true solo si realmente se borro una fila
                flag = st.executeUpdate() > 0;
            }
            cn.commit();
        } catch (SQLException e) {
            //si algo fallo se deshace todo
            try {
                cn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error al deshacer el borrado: " + ex.getMessage());
            }
            throw new RuntimeException("Error al eliminar pelicula: " + e.getMessage(), e);
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error al cerrar la transaccion: " + ex.getMessage());
            }
        }
        return flag;
    }

    //cuenta cuantas funciones tiene la pelicula (para avisar antes de borrar)
    @Override
    public int contarFunciones(int idPelicula) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM funcion WHERE id_pelicula=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idPelicula);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar las funciones: " + e.getMessage(), e);
        }
        return total;
    }

    //cuenta los boletos vendidos de las funciones de la pelicula
    @Override
    public int contarVentas(int idPelicula) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM venta WHERE id_funcion IN "
                + "(SELECT idfuncion FROM funcion WHERE id_pelicula=?)";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idPelicula);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar las ventas: " + e.getMessage(), e);
        }
        return total;
    }
    @Override
    public List<Pelicula> listAll() {
        List<Pelicula> lista = new ArrayList<>();
        String query = "SELECT * FROM pelicula";

        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Pelicula p = new Pelicula();
                p.setidpelicula(rs.getInt("idpelicula"));
                p.setNumero(rs.getInt("numero"));
                p.setTitulo(rs.getString("titulo"));
                p.setDuracion(rs.getString("duracion"));
                p.setCategoria(rs.getString("categoria"));
                p.setClasificacion(rs.getString("clasificacion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar películas: " + e.getMessage(), e);
        }

        return lista;
    }
}

