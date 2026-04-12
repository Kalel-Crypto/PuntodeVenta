package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import facade.SistemaFacade;
import mx.puntodeventa.dao.InventarioDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("InventarioBean")
@SessionScoped
public class InventarioBean implements Serializable {

    private List<InventarioDTO> listaInventario;
    private InventarioDTO seleccionado;
    private InventarioDTO productoEdit;

    private SistemaFacade facade;

    private String nombre;
    private int ID;
    @PostConstruct
    public void inicio() {
        facade = new SistemaFacade();
        listaInventario = new ArrayList<>();
        productoEdit = new InventarioDTO();
        cargarLista();
    }

    private void cargarLista() {
        try {
            listaInventario = facade.listarInventario();
        } catch (Exception e) {
            e.printStackTrace();
            listaInventario = new ArrayList<>();
        }
    }

    public void buscarProducto() {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                cargarLista();
            } else {
                listaInventario = facade.buscarInventarioPorNombre(nombre);
                //listaInventario = facade.buscarInventarioExacto(ID, nombre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //AQUI TE PREPARO LA MODIFICACION
    public void prepararModificar() {

        if (seleccionado == null) {
            msgWarn("Seleccione un producto primero");
            return;
        }

        if (productoEdit == null) {
            productoEdit = new InventarioDTO();
        }

        productoEdit.setIdProducto(seleccionado.getIdProducto());
        productoEdit.setNombreProducto(seleccionado.getNombreProducto());
        productoEdit.setStock(seleccionado.getStock());
        productoEdit.setPrecio(seleccionado.getPrecio());
        productoEdit.setProveedor(seleccionado.getProveedor());

        PrimeFaces.current().executeScript("PF('dlgModificar').show()");
    }
    //AQUI PERMITE MODIFICAR EL PRODUCTO ENTERO NO SOLO EL STOCK
    public void modificarProducto() {

        try {
            if (productoEdit == null) {
                msgWarn("Seleccione un producto primero");
                return;
            }

            facade.actualizarStock(productoEdit.getIdProducto(), productoEdit.getStock());

            cargarLista();

            seleccionado = null;
            productoEdit = new InventarioDTO();

            msgInfo("Producto modificado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepararEliminar() {

        if (seleccionado == null) {
            msgWarn("Seleccione un producto primero");
            return;
        }

        PrimeFaces.current().executeScript("PF('confirmDialog').show()");
    }

    public void eliminarProducto() {

        try {
            if (seleccionado == null) return;

            facade.eliminarRegistroInventario(seleccionado.getIdProducto());

            cargarLista();

            seleccionado = null;

            msgInfo("Producto eliminado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void msgWarn(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", msg));
    }

    private void msgInfo(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", msg));
    }



    public List<InventarioDTO> getListaInventario() { return listaInventario; }
    public void setListaInventario(List<InventarioDTO> listaInventario) { this.listaInventario = listaInventario; }

    public InventarioDTO getSeleccionado() { return seleccionado; }
    public void setSeleccionado(InventarioDTO seleccionado) { this.seleccionado = seleccionado; }

    public InventarioDTO getProductoEdit() { return productoEdit; }
    public void setProductoEdit(InventarioDTO productoEdit) { this.productoEdit = productoEdit; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setID(int ID) {
        this.ID = ID;
    }
}