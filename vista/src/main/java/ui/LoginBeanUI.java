/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import helper.LoginHelper;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Rol;
import facade.SistemaFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;

@Named("loginUI")
@SessionScoped
public class LoginBeanUI implements Serializable{
    //private LoginHelper loginHelper;
    private Usuario usuario;
    private String nombre;
    private String password;
    private SistemaFacade facade;
    private Usuario usuarioLogueado;

    
    /**
     * Metodo postconstructor todo lo que este dentro de este metodo
     * sera la primero que haga cuando cargue la pagina
     */
    @PostConstruct
    public void init() {
        facade = new SistemaFacade();
    }

    public String login() {
        try {
            System.out.println("Nombre del usuario: " + this.usuario.getNombre());
            this.usuario = facade.login(nombre, password);
            if (this.usuario != null && this.usuario.getId() > 0) {
                if (this.usuario.getRol() == Rol.ADMINISTRADOR) {
                    return "Inventario.xhtml?faces-redirect=true";
                } else {
                    return "Ventas.xhtml?faces-redirect=true";
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de acceso:", e.getMessage()));
        }
        return null;
    }

    
    /* getters y setters*/

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    
}
