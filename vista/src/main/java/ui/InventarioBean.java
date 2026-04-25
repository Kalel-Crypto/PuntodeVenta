package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;
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
    private String busqueda;
    private int cantidadOperacion;

    private String nombre;
    private int ID;
    @PostConstruct
    public void inicio() {
        facade = new SistemaFacade();
        listaInventario = new ArrayList<>();
        productoEdit = new InventarioDTO();
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
           listaInventario = facade.buscarInventarioPorId(Id);
        }catch (NumberFormatException msg){
            try {
                listaInventario = facade.buscarInventarioPorNombre(busqueda);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        /*try {
            if ((nombre == null || nombre.trim().isEmpty()) && ID <= 0) {
                cargarLista();
            } else if (ID > 0 && (nombre != null && !nombre.trim().isEmpty())) {
                listaInventario = facade.buscarInventarioExacto(ID, nombre);
            } else if (nombre != null && !nombre.trim().isEmpty()) {
                listaInventario = facade.buscarInventarioPorNombre(nombre);
            } else if (ID > 0) {
                listaInventario = facade.buscarInventarioPorId(ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void registrarEntrada(InventarioDTO dto) {
        if (usuarioBean.getUsuario() == null ||
                !String.valueOf(usuarioBean.getUsuario().getRol()).equalsIgnoreCase("ADMINISTRADOR")) {
            msgWarn("Acceso denegado: Solo el administrador puede registrar entradas.");
            return;
        }

        if (cantidadOperacion <= 0) {
            msgWarn("La cantidad debe ser mayor a cero.");
            return;
        }

        try {
            mx.puntodeventa.entity.Producto p = new mx.puntodeventa.entity.Producto();
            p.setId(dto.getIdProducto());
            p.setNombre(dto.getNombreProducto());

            int nuevoStock = dto.getStock() + cantidadOperacion;

            facade.actualizarStock(dto.getIdProducto(), nuevoStock);

            facade.registrarMovimientoSeguro(usuarioBean.getUsuario(), p, "Entrada", cantidadOperacion);

            dto.setStock(nuevoStock);

            msgInfo("Entrada registrada. Nuevo stock: " + nuevoStock);
            cantidadOperacion = 0;
            cargarLista();
        } catch (Exception e) {
            msgWarn("Error al procesar la entrada.");
            e.printStackTrace();
        }
    }

    public void registrarSalida(InventarioDTO dto) {
        if (usuarioBean.getUsuario() == null ||
                !String.valueOf(usuarioBean.getUsuario().getRol()).equalsIgnoreCase("ADMINISTRADOR")) {
            msgWarn("Acceso denegado.");
            return;
        }

        if (dto.getStock() < cantidadOperacion) {
            msgWarn("No hay suficiente stock para realizar esta salida.");
            return;
        }

        try {
            mx.puntodeventa.entity.Producto p = new mx.puntodeventa.entity.Producto();
            p.setId(dto.getIdProducto());
            p.setNombre(dto.getNombreProducto());

            int nuevoStock = dto.getStock() - cantidadOperacion;

            facade.actualizarStock(dto.getIdProducto(), nuevoStock);

            facade.registrarMovimientoSeguro(usuarioBean.getUsuario(), p, "Salida", cantidadOperacion);

            dto.setStock(nuevoStock);
            msgInfo("Salida registrada correctamente.");
            cantidadOperacion = 0;
            cargarLista();
        } catch (Exception e) {
            msgWarn("Fallo al registrar la salida.");
            e.printStackTrace();
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