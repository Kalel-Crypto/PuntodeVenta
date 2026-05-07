package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import mx.puntodeventa.entity.MovimientoInventario;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;
import mx.puntodeventa.entity.Usuario;
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
    private List<MovimientoInventario> listaMovimientos;

    private InventarioDTO seleccionado;
    private InventarioDTO productoEdit;

    private SistemaFacade facade;
    private String busqueda;
    private int cantidadOperacion;
    private String nombre;
    private int ID;
    AuditoriaBean auditoriaBean;


    @PostConstruct
    public void inicio(){
        facade = new SistemaFacade();
        listaInventario = new ArrayList<>();
        productoEdit = new InventarioDTO();
        auditoriaBean = new AuditoriaBean();
        cargarLista();
    }
    @Inject
    private UsuarioBean usuarioBean;

    public void refrescar(){
        this.busqueda = null;
        cargarLista();
    }

    public void cargarLista() {
        try {
            listaInventario = facade.listarInventario();
        } catch (Exception e) {
            e.printStackTrace();
            listaInventario = new ArrayList<>();
        }

    }

    public void buscarProducto() {
        int Id;
        System.out.println("DATO CAPTURADO DEL FIELD: " + busqueda);
        try{
            if(busqueda.trim().isEmpty()){
                cargarLista();
            }
            Id = Integer.parseInt(busqueda);
            long inicio = System.currentTimeMillis();
           listaInventario = facade.buscarInventarioPorId(Id);
            long fin = System.currentTimeMillis();
            System.out.println("Tiempo: " + (fin - inicio));
        }catch (NumberFormatException msg){
            try {
                listaInventario = facade.buscarInventarioPorNombre(busqueda);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void registrarEntrada(InventarioDTO dto, Usuario usuarioLogeado) {
        if (cantidadOperacion <= 0) {
            msgWarn("La cantidad debe ser mayor a cero.");
            return;
        }
        try{
            Producto p = new Producto();
            p.setId(dto.getIdProducto());
            p.setNombre(dto.getNombreProducto());
            int nuevoStock = dto.getStock() + cantidadOperacion;
            facade.actualizarStock(dto.getIdProducto(), nuevoStock);
            facade.registrarMovimientoSeguro(usuarioLogeado,p, "Entrada", cantidadOperacion);
            dto.setStock(nuevoStock);
            msgInfo("Entrada registrada. Nuevo stock: " + nuevoStock);
            cantidadOperacion = 0;

            cargarLista();

        }catch (Exception msg){
            msgWarn("Error al procesar la entrada.");
            msg.printStackTrace();
        }
    }

    public void registrarSalida(InventarioDTO dto, Usuario usuarioLogeado) {
        if (cantidadOperacion <= 0) {
            msgWarn("La cantidad debe ser mayor a cero.");
            return;
        }
        try{
            Producto p = new Producto();
            p.setId(dto.getIdProducto());
            p.setNombre(dto.getNombreProducto());
            int nuevoStock = dto.getStock() + cantidadOperacion;
            facade.actualizarStock(dto.getIdProducto(), nuevoStock);
            facade.registrarMovimientoSeguro(usuarioLogeado,p, "Salida", cantidadOperacion);
            dto.setStock(nuevoStock);
            msgInfo("Entrada registrada. Nuevo stock: " + nuevoStock);
            cantidadOperacion = 0;

            cargarLista();

        }catch (Exception msg){
            msgWarn("Error al procesar la entrada.");
            msg.printStackTrace();
        }
    }

    //AQUI TE PREPARO LA MODIFICACION
    public void prepararModificar() {
        System.out.println("Producto seleccionado ID: " + seleccionado.getIdProducto());
        System.out.println("Proveedor seleccionado ID: " + seleccionado.getIdProveedor());
        if (seleccionado == null) {
            msgWarn("Seleccione un producto primero");
            return;
        }


        productoEdit = new InventarioDTO();


        productoEdit.setIdProducto(seleccionado.getIdProducto());
        productoEdit.setNombreProducto(seleccionado.getNombreProducto());
        productoEdit.setStock(seleccionado.getStock());
        productoEdit.setPrecio(seleccionado.getPrecio());
        productoEdit.setIdProveedor(seleccionado.getIdProveedor());


        PrimeFaces.current().ajax().update("formModificar");
        PrimeFaces.current().executeScript("PF('dlgModificar').show()");
    }
    //AQUI PERMITE MODIFICAR EL PRODUCTO ENTERO NO SOLO EL STOCK
    public void modificarProducto() {
        System.out.println(">>> ENTRANDO A modificarProducto() <<<");
        try {
            if (productoEdit == null || seleccionado == null) {
                msgWarn("Seleccione un producto primero");
                return;
            }

            int stockAnterior = seleccionado.getStock();
            int stockNuevo = productoEdit.getStock();
            int diferencia = stockNuevo - stockAnterior;

            String tipoMovimiento = "";
            if (diferencia > 0) {
                tipoMovimiento = "Entrada";
            } else if (diferencia < 0) {
                tipoMovimiento = "Salida";
            }

            facade.actualizarProducto(
                    productoEdit.getIdProducto(),
                    productoEdit.getNombreProducto(),
                    productoEdit.getPrecio(),
                    productoEdit.getIdProveedor()
            );

            facade.actualizarStock(
                    productoEdit.getIdProducto(),
                    productoEdit.getStock()
            );

            if (!tipoMovimiento.isEmpty()) {
                try {
                    mx.puntodeventa.entity.Producto p = new mx.puntodeventa.entity.Producto();
                    p.setId(productoEdit.getIdProducto());
                    p.setNombre(productoEdit.getNombreProducto());

                    facade.registrarMovimientoSeguro(
                            usuarioBean.getUsuario(),
                            p,
                            tipoMovimiento,
                            Math.abs(diferencia)
                    );
                } catch (Exception e) {
                    System.err.println("Error no crítico en auditoría: " + e.getMessage());
                }
            }

            cargarLista();
            seleccionado = null;
            productoEdit = new InventarioDTO();

            msgInfo("Producto modificado correctamente");

        } catch(Exception e) {
            msgWarn("Error al modificar: Intentelo de nuevo");
            System.out.println("ERROR CRÍTICO EN MODIFICAR PRODUCTO:");
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
            if (seleccionado == null || seleccionado.getIdProducto() <= 0) {
                return;
            }

            int idProducto = seleccionado.getIdProducto();



            facade.eliminarRegistroInventario(idProducto);
            facade.eliminarProducto(idProducto);
            seleccionado = null;
            msgInfo("Producto eliminado correctamente");
            cargarLista();
        } catch (Exception e) {
            msgWarn("Error al eliminar el producto, intentelo de nuevo");
            System.out.println("ERROR: " + e.getMessage());
            cargarLista();
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

    public int getCantidadOperacion() {
        return cantidadOperacion;
    }

    public void setCantidadOperacion(int cantidadOperacion) {
        this.cantidadOperacion = cantidadOperacion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }
}