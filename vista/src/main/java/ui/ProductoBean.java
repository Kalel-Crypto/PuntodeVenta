package ui;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import facade.SistemaFacade;
import mx.puntodeventa.dao.ProductoDAO;
import mx.puntodeventa.entity.Inventario;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;
import jakarta.inject.Inject;

@Named("productoBean")
@ViewScoped
public class ProductoBean implements Serializable {

    @Inject
    private InventarioBean inventarioBean;
    private Producto producto;
    private List<Proveedor> listaProveedores;
    private SistemaFacade facade;
    private Integer idProveedorSeleccionado;
    private ProductoDAO productoDao;

    private int stockInicial;

    @PostConstruct
    public void init() {
        productoDao = new ProductoDAO();
        facade = new SistemaFacade();
        producto = new Producto();

        try {
            listaProveedores = facade.listarProveedores();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registrar() throws Exception {
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Precio del producto: " + producto.getNombre());
        System.out.println("id proveedor: " + idProveedorSeleccionado);
        System.out.println("Stock: " + stockInicial);
        if(productoDao.existeProductoPorNombre(producto.getNombre())){
            msgWarn("Ya hay un producto con el mismo nombre, ingrese otro nombre");
        }
        if(producto.getPrecio() <= 0){
            msgWarn("Asegurese de que el precio sea un numero positivo");
            return;
        }
        if(stockInicial < 0){
            msgWarn("Asegurese que el stock sea un numero positivo");
            return;
        }


        try {
            facade.registrarProducto(
                    producto.getNombre(),
                    producto.getPrecio(),
                    idProveedorSeleccionado,
                    stockInicial

            );

            System.out.println("Producto registrado: " + producto.getNombre());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Producto registrado correctamente"));
           // facade.listarInventario();
            inventarioBean.cargarLista();

            PrimeFaces.current().ajax().update(":formInventario:tablaInventario");
            PrimeFaces.current().ajax().update(":formInventario:growl");

            this.producto = new Producto();
            this.producto.setProveedor(new Proveedor());
            this.stockInicial = 0;
            idProveedorSeleccionado = null;

        } catch (Exception e) {
            System.err.println("Error en el registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void msgWarn(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", mensaje));
    }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getStockInicial() { return stockInicial; }
    public void setStockInicial(int stockInicial) { this.stockInicial = stockInicial; }

    public List<Proveedor> getListaProveedores() { return listaProveedores; }
    public void setListaProveedores(List<Proveedor> listaProveedores) { this.listaProveedores = listaProveedores; }

    public Integer getIdProveedorSeleccionado() { return idProveedorSeleccionado; }
    public void setIdProveedorSeleccionado(Integer idProveedorSeleccionado) { this.idProveedorSeleccionado = idProveedorSeleccionado; }
}