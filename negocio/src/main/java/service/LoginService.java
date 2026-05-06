package service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import mx.puntodeventa.dao.UsuarioDAO;
import mx.puntodeventa.entity.Rol;
import mx.puntodeventa.entity.Usuario;

@ApplicationScoped
public class LoginService {
    UsuarioDAO usuarioDAO;


    public LoginService(){
        usuarioDAO = new UsuarioDAO();
    }

    public boolean iniciarSesion(String nombre, String password, Rol rol) throws Exception {
        System.out.println("Entre al tercer metodo de inicio de sesion");
        if(nombre.trim().isEmpty()){
            throw new Exception("No deje espacios vacios");
        }
        if (password.trim().isEmpty()){
            throw new Exception("No deje espacios vacios");
        }
        if(rol == null){
            throw new Exception("Debe seleccionar un rol");
        }

        for(Usuario u: usuarioDAO.listar()){
            if(u.getNombre().equals(nombre) && u.getPassword().equals(password) && rol == u.getRol()){
                return true;
            }
        }
        return false;

    }
}
