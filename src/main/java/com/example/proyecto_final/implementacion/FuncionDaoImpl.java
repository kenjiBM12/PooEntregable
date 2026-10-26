package com.example.proyecto_final.implementacion;

import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {
    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;
        if (f == null || f.getPelicula() == null || f.getSala() == null
                || f.getHorario() == null || f.getHorario().isEmpty()
                || !(f.getPrecio() > 0) || f.getPrecio() > Double.MAX_VALUE) {
            return flag;
        }
        Pelicula pelicula = new PeliculaDaoImpl().searchById(f.getPelicula().getIdpelicula());
        Sala sala = new SalaDaoImpl().searchById(f.getSala().getIdsala());
        if (pelicula == null || sala == null) {
            return flag;
        }
        String query = "INSERT INTO funcion(id_pelicula,id_sala,horario,precio) VALUES(?,?,?,?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, pelicula.getIdpelicula());
            st.setInt(2, sala.getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al insertar funcion: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;
        if (f == null || f.getPelicula() == null || f.getSala() == null
                || f.getHorario() == null || f.getHorario().isEmpty()
                || !(f.getPrecio() > 0) || f.getPrecio() > Double.MAX_VALUE) {
            return flag;
        }
        Pelicula pelicula = new PeliculaDaoImpl().searchById(f.getPelicula().getIdpelicula());
        Sala sala = new SalaDaoImpl().searchById(f.getSala().getIdsala());
        if (pelicula == null || sala == null) {
            return flag;
        }
        long ocupados = 0;
        String query = "SELECT cantidad_asientos FROM venta WHERE id_funcion=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    ocupados += rs.getInt("cantidad_asientos");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar capacidad de funcion: " + e.getMessage());
            return flag;
        }
        if (ocupados > sala.getCapacidad()) {
            return flag;
        }
        query = "UPDATE funcion SET id_pelicula=?,id_sala=?,horario=?,precio=? WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, pelicula.getIdpelicula());
            st.setInt(2, sala.getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getIdfuncion());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al actualizar funcion: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public Funcion searchById(int id) {
        Funcion funcion = null;
        String query = "SELECT f.id AS idfuncion,f.horario,f.precio,"
                + "p.idpelicula,p.titulo,p.genero,p.duracion,p.clasificacion,"
                + "s.idsala,s.nombre AS sala_nombre,s.tipo AS sala_tipo,s.capacidad"
                + " FROM funcion f,pelicula p,sala s"
                + " WHERE f.id_pelicula=p.idpelicula AND f.id_sala=s.idsala AND f.id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Pelicula pelicula = new Pelicula();
                    pelicula.setIdpelicula(rs.getInt("idpelicula"));
                    pelicula.setTitulo(rs.getString("titulo"));
                    pelicula.setGenero(rs.getString("genero"));
                    pelicula.setDuracion(rs.getInt("duracion"));
                    pelicula.setClasificacion(rs.getString("clasificacion"));

                    Sala sala = new Sala();
                    sala.setIdsala(rs.getInt("idsala"));
                    sala.setNombre(rs.getString("sala_nombre"));
                    sala.setTipo(rs.getString("sala_tipo"));
                    sala.setCapacidad(rs.getInt("capacidad"));

                    funcion = new Funcion();
                    funcion.setIdfuncion(rs.getInt("idfuncion"));
                    funcion.setHorario(rs.getString("horario"));
                    funcion.setPrecio(rs.getDouble("precio"));
                    funcion.setPelicula(pelicula);
                    funcion.setSala(sala);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar funcion: " + e.getMessage());
        }
        return funcion;
    }

    @Override
    public boolean delete(Funcion f) {
        boolean flag = false;
        if (f == null) {
            return flag;
        }
        String query = "SELECT count(*) FROM venta WHERE id_funcion=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return flag;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar funcion: " + e.getMessage());
            return flag;
        }
        query = "DELETE FROM funcion WHERE id=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            flag = st.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar funcion: " + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();
        String query = "SELECT f.id AS idfuncion,f.horario,f.precio,"
                + "p.idpelicula,p.titulo,p.genero,p.duracion,p.clasificacion,"
                + "s.idsala,s.nombre AS sala_nombre,s.tipo AS sala_tipo,s.capacidad"
                + " FROM funcion f,pelicula p,sala s"
                + " WHERE f.id_pelicula=p.idpelicula AND f.id_sala=s.idsala";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Pelicula pelicula = new Pelicula();
                pelicula.setIdpelicula(rs.getInt("idpelicula"));
                pelicula.setTitulo(rs.getString("titulo"));
                pelicula.setGenero(rs.getString("genero"));
                pelicula.setDuracion(rs.getInt("duracion"));
                pelicula.setClasificacion(rs.getString("clasificacion"));

                Sala sala = new Sala();
                sala.setIdsala(rs.getInt("idsala"));
                sala.setNombre(rs.getString("sala_nombre"));
                sala.setTipo(rs.getString("sala_tipo"));
                sala.setCapacidad(rs.getInt("capacidad"));

                Funcion funcion = new Funcion();
                funcion.setIdfuncion(rs.getInt("idfuncion"));
                funcion.setHorario(rs.getString("horario"));
                funcion.setPrecio(rs.getDouble("precio"));
                funcion.setPelicula(pelicula);
                funcion.setSala(sala);
                lista.add(funcion);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar funciones: " + e.getMessage());
        }
        return lista;
    }
}
