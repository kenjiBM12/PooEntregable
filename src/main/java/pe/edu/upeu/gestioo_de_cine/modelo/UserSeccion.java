package pe.edu.upeu.gestioo_de_cine.modelo;

public class UserSeccion {
    private static UserSeccion instance;
    private Usuario UserLogin;

    private UserSeccion() {}

    public static UserSeccion getInstance(){
        if (instance == null){
            instance = new UserSeccion();
        }
        return instance;
    }
    public Usuario getUserLogin(){
        return UserLogin;
    }
    public void setUserLogin(Usuario usuario){
        this.UserLogin=usuario ;
    }
    public void cleanSession(){
        UserLogin = null;
    }

}
