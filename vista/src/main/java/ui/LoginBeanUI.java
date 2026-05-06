/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import facade.SistemaFacade;
import helper.LoginHelper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.puntodeventa.entity.Rol;
import mx.puntodeventa.entity.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;

@Named("loginUI")
@SessionScoped
public class LoginBeanUI extends HttpServlet implements Serializable{
    private Usuario usuario;
    LoginHelper helper;
    AuditoriaBean auditoriaBean;
    private Usuario usuarioLogeado;
    private SistemaFacade facade;
    @PostConstruct
    public void init(){
        facade = new SistemaFacade();
        usuarioLogeado = new Usuario();
        usuario= new Usuario();
        helper = new LoginHelper();
        auditoriaBean = new AuditoriaBean();
    }


     public void login() throws Exception {
        System.out.println("Entre al metodo de login");
        if(helper.inicioSesion(usuario.getNombre(),usuario.getPassword(),usuario.getRol())){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Exito","Inicio de sesion exitoso"));
            for(Usuario u: facade.listarUsuarios()){
                if(u.getNombre().equals(usuario.getNombre())){
                    usuarioLogeado = u;
                    break;
                }
            }
            System.out.println("Nombre del usuario logeado: " + usuarioLogeado.getNombre());
            FacesContext.getCurrentInstance().getExternalContext().redirect("Inventario.xhtml");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","Credenciales invalidas, verefique su usuario y/o contraseña"));
        }
    }

    public void logout() throws IOException {
        System.out.println("Entre a logout");
        usuarioLogeado = null;
        FacesContext.getCurrentInstance().getExternalContext().redirect("loginUI.xhtml");

    }

    public Rol[] getRoles() {
        return Rol.values();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuarioLogeado() {
        return usuarioLogeado;
    }

    public void setUsuarioLogeado(Usuario usuarioLogeado) {
        this.usuarioLogeado = usuarioLogeado;
    }
}
