package ui;

import facade.SistemaFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.puntodeventa.entity.Caja;
import mx.puntodeventa.entity.DetalleVenta;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Venta;
import mx.puntodeventa.dao.InventarioDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("ventaBean")
@SessionScoped
public class VentaBean implements Serializable {

    private Venta venta;
    private Usuario usuario;
    private SistemaFacade facade;
    private LoginBeanUI usuarioLogeado;
    private Caja cajaActual;
    private List<DetalleVenta> listaDetalle;


    private Integer idProductoSeleccionado;

    private double total;
    private double dineroRecibido;
    private double cambio;
    private String nombreUsuario;
    private String busqueda;

    @PostConstruct
    public void init() {
        facade = new SistemaFacade();
        cajaActual = new Caja();
        cajaActual.setIdcaja(1);
        listaDetalle = new ArrayList<>();
        total = 0.0;
    }

    public List<InventarioDTO> sugerirProductos(String query) {
        List<InventarioDTO> sugerencias = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return sugerencias;
        }

        try {
            int idBusqueda = Integer.parseInt(query.trim());
            sugerencias = facade.buscarInventarioPorId(idBusqueda);
        } catch (NumberFormatException msg) {
            try {
                sugerencias = facade.buscarInventarioPorNombre(query.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sugerencias;
    }


    public void agregarProducto() {
        if (idProductoSeleccionado == null || idProductoSeleccionado <= 0) {
            return;
        }

        try {

            List<InventarioDTO> resultadoBusqueda = facade.buscarInventarioPorId(idProductoSeleccionado);

            if (resultadoBusqueda == null || resultadoBusqueda.isEmpty()) {
                return;
            }

            InventarioDTO productoBD = resultadoBusqueda.get(0);

            if (productoBD.getStock() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Agotado", "El producto no tiene existencias."));
                idProductoSeleccionado = null;
                return;
            }

            boolean productoYaExiste = false;

            for (DetalleVenta detalle : listaDetalle) {
                if (detalle.getProducto().getId() == productoBD.getIdProducto()) {

                    if (detalle.getCantidad() >= productoBD.getStock()) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock insuficiente", "No puedes agregar más unidades de las disponibles."));
                        idProductoSeleccionado = null;
                        return;
                    }

                    detalle.setCantidad(detalle.getCantidad() + 1);
                    productoYaExiste = true;
                    break;
                }
            }

            if (!productoYaExiste) {
                Producto p = new Producto();
                p.setId(productoBD.getIdProducto());
                p.setNombre(productoBD.getNombreProducto());
                p.setPrecio(productoBD.getPrecio());

                DetalleVenta nuevoDetalle = new DetalleVenta();
                nuevoDetalle.setProducto(p);
                nuevoDetalle.setCantidad(1);
                nuevoDetalle.setPrecioUnitario(productoBD.getPrecio());

                listaDetalle.add(nuevoDetalle);
            }


            total += productoBD.getPrecio();
            this.idProductoSeleccionado = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminar(DetalleVenta item) {
        listaDetalle.remove(item);
        total -= (item.getPrecioUnitario() * item.getCantidad());
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Removido", "Producto eliminado de la venta"));
    }


    public Caja getCajaActual() { return cajaActual; }
    public void setCajaActual(Caja cajaActual) { this.cajaActual = cajaActual; }

    public List<DetalleVenta> getListaDetalle() { return listaDetalle; }
    public void setListaDetalle(List<DetalleVenta> listaDetalle) { this.listaDetalle = listaDetalle; }


    public Integer getIdProductoSeleccionado() { return idProductoSeleccionado; }
    public void setIdProductoSeleccionado(Integer idProductoSeleccionado) { this.idProductoSeleccionado = idProductoSeleccionado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getDineroRecibido() { return dineroRecibido; }
    public void setDineroRecibido(double dineroRecibido) { this.dineroRecibido = dineroRecibido; }

    public double getCambio() { return cambio; }
    public void setCambio(double cambio) { this.cambio = cambio; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getBusqueda() { return busqueda; }
    public void setBusqueda(String busqueda) { this.busqueda = busqueda; }
}