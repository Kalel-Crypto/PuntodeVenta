package ui;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import facade.SistemaFacade;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;
import java.io.Serializable;
import java.util.List;


@Named("productoBean")
@RequestScoped
public class ProductoBean implements Serializable {

    private Producto producto;
    private List<Proveedor> listaProveedores;
    private SistemaFacade facade;


    private int stockInicial;

    @PostConstruct
    public void init() {
        facade = new SistemaFacade();
        producto = new Producto();
        producto.setProveedor(new Proveedor());

        try {
            listaProveedores = facade.listarProveedores();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registrar() {
        try {
           facade.registrarProducto(
                    producto.getNombre(),           // String nombre
                    producto.getPrecio(),           // double precio
                    producto.getProveedor().getId(),// int idProveedor
                    stockInicial,                   // int stock
                    producto.getCaducidad()         // Date caducidad
            );

            System.out.println("Producto registrado: " + producto.getNombre());

            this.producto = new Producto();
            this.producto.setProveedor(new Proveedor());
            this.stockInicial = 0;

        } catch (Exception e) {
            System.err.println("Error en el registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getStockInicial() { return stockInicial; }
    public void setStockInicial(int stockInicial) { this.stockInicial = stockInicial; }

    public List<Proveedor> getListaProveedores() { return listaProveedores; }
    public void setListaProveedores(List<Proveedor> listaProveedores) { this.listaProveedores = listaProveedores; }
}





