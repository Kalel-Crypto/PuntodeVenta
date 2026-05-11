/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.puntodeventa.entity.Rol;
import mx.puntodeventa.entity.Usuario;
import service.LoginService;
//import mx.desarrollo.integration.ServiceFacadeLocator;

import java.io.Serializable;

@ApplicationScoped
public class LoginHelper implements Serializable {
    LoginService loginService;

    public LoginHelper(){
        loginService = new LoginService();
    }


    public boolean inicioSesion(String nombre, String password, Rol rol) throws Exception {
         System.out.println("Entre al segundo metodo de inicio de sesion");
         System.out.println(nombre + "," + password + "," + rol);
        if(loginService.iniciarSesion(nombre,password,rol)){
            return true;
        }
        return false;
    }
    
    
    
}
