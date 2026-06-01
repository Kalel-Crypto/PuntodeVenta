package ui;

import facade.SistemaFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.puntodeventa.entity.Proveedor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("proveedorBean")
@SessionScoped
public class ProveedorBean implements Serializable {

    private SistemaFacade facade;

    private Proveedor proveedor;
    private Proveedor proveedorSeleccionado;
    private String busqueda;
    private List<Proveedor> listaProveedores;

    @PostConstruct
    public void inicio(){
        facade = new SistemaFacade();
        proveedor = new Proveedor();
        proveedorSeleccionado = null;
        listaProveedores = new ArrayList<>();
        cargarProveedores();
    }

    public void registrar() throws Exception {
        if(proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()){
            msgWarn("Asegúrese de ingresar un nombre.");
            return;
        }
        if(!proveedor.getContacto().matches("^686\\d{7}$")){
            msgWarn("Ingrese un número de teléfono válido.");
            return;
        }
        if(proveedor.getMarca() == null || proveedor.getMarca().trim().isEmpty()){
            msgWarn("Ingrese la marca del proveedor");
            return;
        }
        if(facade.existeMarca(proveedor)){
            msgWarn("La marca ya esta registrada");
            return;
        }

        try{
            facade.registrarProveedor(proveedor.getNombre(), proveedor.getContacto(), proveedor.getMarca());
            msgInfo("Proveedor registrado correctamente.");
            this.proveedor = new Proveedor();
            cargarProveedores();
        } catch(Exception e){
            msgError("Error al registrar el proveedor en la base de datos.");
            e.printStackTrace();
        }
    }

    public void cargarProveedores() {
        try {
            List<Proveedor> lista = facade.listarProveedores();
            this.listaProveedores = (lista != null) ? lista : new ArrayList<>();
        } catch (Exception e) {
            msgError("Error al cargar la lista de proveedores.");
            e.printStackTrace();
            this.listaProveedores = new ArrayList<>();
        }
    }

    public void buscarProveedor() {
        System.out.println("Entre al metodo con: " + busqueda);
        if(busqueda == null || busqueda.trim().isEmpty()){
            cargarProveedores();
            return;
        }
        try {
            List<Proveedor> lista = facade.buscarProveedores(busqueda);
            this.listaProveedores = (lista != null) ? lista : new ArrayList<>();
        } catch (Exception e) {
            msgError("Error al realizar la búsqueda.");
            e.printStackTrace();
        }
    }

    public void prepararModificacion(Proveedor prov) {
        this.proveedorSeleccionado = prov;
    }

    public void guardarModificacion() throws Exception {
        if(proveedorSeleccionado == null || proveedorSeleccionado.getNombre() == null || proveedorSeleccionado.getNombre().trim().isEmpty()){
            msgWarn("El nombre no puede estar vacío.");
            return;
        }
        if(!proveedorSeleccionado.getContacto().matches("^686\\d{7}$")){
            msgWarn("El campo contacto debe cumplir con el formato establecido.");
            return;
        }
        if(facade.existeMarca(proveedorSeleccionado)){
            msgWarn("Ya hay una marca registrada con ese nombre");
            return;
        }

        try {
            facade.modificarProveedor(proveedorSeleccionado);
            msgInfo("Proveedor modificado exitosamente.");
            cargarProveedores();
        } catch (Exception e) {
            msgError("Error al modificar el proveedor.");
            e.printStackTrace();
        }
    }

    public void eliminarProveedor(Proveedor prov) {
        try {
            facade.eliminarProveedor(prov.getId());
            msgInfo("Proveedor eliminado exitosamente.");
            cargarProveedores();
        } catch (Exception e) {
            msgError("Error: No se puede eliminar el proveedor. Es probable que tenga productos asociados.");
            e.printStackTrace();
        }
    }

    private void msgInfo(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensaje));
    }

    private void msgWarn(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", mensaje));
    }

    private void msgError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public Proveedor getProveedorSeleccionado() { return proveedorSeleccionado; }
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) { this.proveedorSeleccionado = proveedorSeleccionado; }

    public String getBusqueda() { return busqueda; }
    public void setBusqueda(String busqueda) { this.busqueda = busqueda; }


    public List<Proveedor> getListaProveedores() { return listaProveedores; }
    public void setListaProveedores(List<Proveedor> listaProveedores) { this.listaProveedores = listaProveedores; }
}