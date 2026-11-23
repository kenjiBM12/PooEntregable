package pe.edu.upeu.gestioo_de_cine.test;

import pe.edu.upeu.gestioo_de_cine.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IPeliculaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;

import java.util.List;

    public class TestPelicula {
        static IPeliculaDao pe = new PeliculaDaoImpl();

        public static void listar() {
            List<Pelicula> lista = pe.listAll();
            if (lista != null && !lista.isEmpty()) {
                for (Pelicula pelicula : lista) {
                    System.out.println("ID_CLIENTE:" + pelicula.getidpelicula());
                    System.out.println("Numero:" + pelicula.getNumero());
                    System.out.println("Nombre:" + pelicula.getTitulo());
                    System.out.println("Duracion:" + pelicula.getDuracion());
                    System.out.println("Categoria:" + pelicula.getCategoria());
                    System.out.println("Clasificacion:" + pelicula.getClasificacion());
                }
            } else {
                System.out.println("no hay registros");
            }
        }

        public static void insertar() {
            Pelicula p = new Pelicula();
            p.setTitulo("Ninja mericano");
            p.setNumero(123);
            p.setDuracion("123");
            p.setCategoria("Accion");
            p.setClasificacion("+12");
            boolean result = pe.insert(p);
            if (result) {
                System.out.println("insert success");
            } else {
                System.out.println("insert error");
            }
        }

        public static void eliminar() {
            Pelicula p = new Pelicula();
            p.setidpelicula(0);
            boolean result = pe.delete(p);
            if (result) {
                System.out.println("delete success");
            } else {
                System.out.println("delete error");
            }
        }

        public static void actualizar(int id) {
            Pelicula pelicula = pe.searchById(id);

            if (pelicula == null) {
                System.out.println("No se encontró la película con ID " + id);
                return;
            }

            pelicula.setTitulo("Película actualizada");
            pelicula.setNumero(10);
            pelicula.setDuracion("120");
            pelicula.setCategoria("Drama");
            pelicula.setClasificacion("+12");

            boolean resultado = pe.update(pelicula);

            if (resultado) {
                System.out.println("Actualización exitosa.");
                listar();
            } else {
                System.out.println("Error al actualizar la película.");
            }
        }

        public static void actualizarPorNumero(int num) {
            Pelicula pelicula = pe.searchByNum(num);

            if (pelicula == null) {
                System.out.println("No se encontró la película con Numero " + num);
                return;
            }

            pelicula.setTitulo("ninja americano");
            pelicula.setNumero(11);
            pelicula.setDuracion("120");
            pelicula.setCategoria("Drama");
            pelicula.setClasificacion("+12");

            boolean resultado = pe.update(pelicula);

            if (resultado) {
                System.out.println("Actualización exitosa.");
                listar();
            } else {
                System.out.println("Error al actualizar la película.");
            }
        }




        public static void main(String[] args) {
            System.out.println("************");
            insertar();
            System.out.println("\n**********");
            listar();
            System.out.println("\n**********");
            actualizar(1);
            System.out.println("\n**********");
            eliminar();
            System.out.println("\n**********");
            actualizarPorNumero(1);
        }


    }


