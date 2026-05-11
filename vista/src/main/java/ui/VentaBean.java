package ui;

import facade.SistemaFacade;
import helper.RespaldoHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.puntodeventa.dao.VentaDAO;
import mx.puntodeventa.entity.Caja;
import mx.puntodeventa.entity.DetalleVenta;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Venta;
import mx.puntodeventa.dao.InventarioDTO;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("ventaBean")
@SessionScoped
public class VentaBean implements Serializable {
    private String adminNombre;
    private String adminPassword;
    private Venta venta;
    private Usuario usuario;
    private SistemaFacade facade;
    @Inject
    private LoginBeanUI loginUI;

    private Caja cajaActual;
    private List<DetalleVenta> listaDetalle;

    private Integer idProductoSeleccionado;

    private double total;
    private double dineroRecibido;
    private double cambio;
    private String nombreUsuario;
    private String busqueda;


    @PostConstruct
    public void init() throws Exception {
        facade = new SistemaFacade();
        listaDetalle = new ArrayList<>();
        usuario = (Usuario) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuario");
        cargarCajaUsuario();

    }

    public void validarCorteCaja(){
        try {

            boolean valido = false;
            for(Usuario u: facade.listarUsuarios()){
                if(u.getNombre().equals(adminNombre) && u.getPassword().equals(adminPassword)){
                    valido = true;
                }
            }
            if (!valido) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "Credenciales administrativas inválidas"
                        ));

                return;
            }
            corteCaja();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public void calcularCambio() {

        if (dineroRecibido >= total) {
            cambio = dineroRecibido - total;
        } else {
            cambio = 0;
        }
    }

    public void cargarCajaUsuario() throws Exception {
        System.out.println("USUARIO LLEGANDO A LA CAJA: " + usuario.getId());
        cajaActual = facade.obtenerCajaActual(usuario);
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

    public void cancelarVenta(){

        limpiarVenta();
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

    public void cobrar() {
        if (listaDetalle == null || listaDetalle.isEmpty() || total <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se han agregado productos"));
            return;
        }

        if (dineroRecibido < total) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Dinero insuficiente", "El monto recibido es menor al total."));
            return;
        }

        try {
            this.cambio = dineroRecibido - total;

            Venta nuevaVenta = new Venta();
            nuevaVenta.setIdCaja(cajaActual.getIdcaja());
            nuevaVenta.setTotal(total);

            VentaDAO dao = new VentaDAO();
            int idReal = (usuario != null) ? usuario.getId() : (loginUI != null ? loginUI.getUsuarioLogeado().getId() : 0);

            if (idReal == 0) {
                throw new Exception("No se pudo identificar al usuario de la sesión.");
            }
            dao.registrarVenta(nuevaVenta, listaDetalle, idReal);
            RespaldoHelper.guardarVenta(nuevaVenta, listaDetalle,usuario);

            Usuario cajero = null;
            if (loginUI != null && loginUI.getUsuarioLogeado() != null) {
                cajero = loginUI.getUsuarioLogeado();
            }
            //Este ciclo no es necesario ya que hay otro para insertar en el ventaDAO
            /*
            for (DetalleVenta detalle : listaDetalle) {
                facade.registrarMovimientoSeguro(cajero, detalle.getProducto(), "salida", detalle.getCantidad());
            }
            */
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Venta procesada y stock actualizado."));


            limpiarVenta();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de transacción", e.getMessage()));
            e.printStackTrace();
        }
    }

    public void corteCaja() {

        System.out.println("ENTRE AL METODO DE CORTE");

        try {

            String rutaActual = "C:\\RespaldosPOS\\Caja_" + cajaActual.getIdcaja() + "_ACTIVA.csv";

            File archivoActual = new File(rutaActual);

            if (!archivoActual.exists()) {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin datos", "No hay ventas registradas"));
                return;
            }

            String nuevoNombre = "C:\\RespaldosPOS\\Caja_" + cajaActual.getIdcaja() + "_Corte_" + System.currentTimeMillis() + ".csv";
            File archivoNuevo = new File(nuevoNombre);

            java.nio.file.Files.move(archivoActual.toPath(), archivoNuevo.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            System.out.println("ARCHIVO RENOMBRADO");


            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .invalidateSession();


            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .redirect("loginUI.xhtml");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void limpiarVenta() {
        listaDetalle.clear();
        total = 0.0;
        dineroRecibido = 0.0;
        cambio = 0.0;
    }

    public void eliminar(DetalleVenta item) {
        System.out.println(nombreUsuario);
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

    public String getAdminNombre() {
        return adminNombre;
    }

    public void setAdminNombre(String adminNombre) {
        this.adminNombre = adminNombre;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword= adminPassword;
    }
}