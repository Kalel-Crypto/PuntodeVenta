package ui;

import java.io.Serializable;
import java.util.List;

import facade.SistemaFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.puntodeventa.entity.Rol;
import mx.puntodeventa.entity.Usuario;

@Named("usuarioBean")
@SessionScoped
public class UsuarioBean implements Serializable {
    private Usuario usuario;
    private SistemaFacade facade;
    private List<Usuario> listaUsuarios;
    @PostConstruct
    public void inicio(){
        facade = new SistemaFacade();
        usuario = new Usuario();
        /*try {
            usuario = facade.login("tu_nombre_usuario", "1234567");
        } catch(Exception e) {

            usuario = new Usuario();
            usuario.setId(1);
            usuario.setNombre("Admin Temporal");
        }
        try {
            listaUsuarios = facade.listarUsuarios();
        } catch(Exception e) {
            e.printStackTrace();
        }*/
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
        }catch (Exception msg){
            msg.printStackTrace();
        }
    }
    private void msgWarn(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", mensaje));
    }

    public Rol[] getRoles() {
        return Rol.values();
    }
    public Usuario getUsuario() {
        return usuario;
    }

    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
}
