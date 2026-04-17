package ui;


import facade.SistemaFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.puntodeventa.dao.ProductoDAO;
import mx.puntodeventa.entity.Proveedor;

import java.io.Serializable;

@Named("proveedorBean")
@SessionScoped
public class ProveedorBean implements Serializable {

    private SistemaFacade facade;
    private Proveedor proveedor;
    private ProductoDAO productoDao;
    @PostConstruct
    public void inicio(){
        facade = new SistemaFacade();
        proveedor = new Proveedor();
        productoDao = new ProductoDAO();
    }

    public void registrar() throws Exception {
        System.out.println("Nombre del proveedor: " + proveedor.getNombre());
        System.out.println("Numero del proveedor: " + proveedor.getContacto());


        if(proveedor.getNombre().trim().isEmpty()){
            msgWarn("Asegurese de ingresar un nombre ");
        }
        if(!proveedor.getContacto().matches("^686\\d{7}$")){
            msgWarn("Ingrese un numero de telefono valido");
        }

        try{
            facade.registrarProveedor(proveedor.getNombre(), proveedor.getContacto());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Proveedor registrado correctamente"));
            this.proveedor = new Proveedor();
            proveedor.setNombre("");
            proveedor.setContacto("");
        }catch(Exception e){
            e.printStackTrace();
        }



    }
    private void msgWarn(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", mensaje));
    }
    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
