package ui;

import java.io.Serializable;
import java.util.List;

import facade.SistemaFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.puntodeventa.entity.Rol;
import mx.puntodeventa.entity.Usuario;

@Named("usuarioBean")
@ViewScoped
public class UsuarioBean implements Serializable {
    private Usuario usuario;
    private SistemaFacade facade;
    private List<Usuario> listaUsuarios;
    private Usuario usuarioSeleccionado;
    private String busqueda;
    @PostConstruct
    public void inicio() {
        facade = new SistemaFacade();
        usuario = new Usuario();
        cargarUsuarios();
    }
    public void registrar(){
        System.out.println("Nombre del usuario: " + usuario.getNombre());
        System.out.println("Contraseña del usuario " + usuario.getNombre());

        if(usuario.getNombre().length() <= 4){
            msgWarn("Ingrese un usuario con una longitud mayor a 4 caracteres");
            return;
        }
        if(usuario.getPassword().length() <= 6){
            msgWarn("Ingrese una contraseña con una longitud mayor a 6 caracteres");
            return;
        }

        try {
            facade.registrarUsuario(usuario);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Exito","Usuario registrado con exito"));
        }catch (Exception msg){
            msg.printStackTrace();
        }
    }
    private void msgWarn(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", mensaje));
    }

    public void cargarUsuarios() {
        try {
            this.listaUsuarios = facade.listarUsuarios();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscarUsuario() {
        if(busqueda == null || busqueda.trim().isEmpty()){
            cargarUsuarios();
            return;
        }
        try {

            this.listaUsuarios = facade.buscarUsuarios(busqueda);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepararModificacion(Usuario user) {
        this.usuarioSeleccionado = user;
    }

    public void guardarModificacion() {
        try {
            facade.modificarUsuario(usuarioSeleccionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario modificado."));
            cargarUsuarios();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarUsuario(Usuario user) {
        try {
            facade.eliminarUsuario(user.getId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario eliminado."));
            cargarUsuarios();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Rol[] getRoles() {
        return Rol.values();
    }
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
    public Usuario getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }
    public String getBusqueda() { return busqueda; }
    public void setBusqueda(String busqueda) { this.busqueda = busqueda; }

    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
}
