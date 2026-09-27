package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import facade.SistemaFacade;
import mx.puntodeventa.entity.MovimientoInventario;
import mx.puntodeventa.entity.Usuario;

@Named("AuditoriaBean")
@ViewScoped
public class AuditoriaBean implements Serializable {

    private List<MovimientoInventario> listaMovimientos;
    private List<MovimientoInventario> listaFiltrada;
    private List<Usuario> listaUsuarios;

    private Integer idUsuarioFiltro;
    private String productoFiltro;
    private Date fechaDesde;
    private Date fechaHasta;

    private SistemaFacade facade;


    private int totalEntradas;
    private int totalSalidas;

    private MovimientoInventario movimientoSeleccionado;

    private Usuario usuarioLogeado;
    LoginBeanUI loginBeanUI;


    @PostConstruct
    public void inicio() {

        loginBeanUI = new LoginBeanUI();
        facade = new SistemaFacade();

        listaMovimientos = new ArrayList<>();
        listaFiltrada = new ArrayList<>();
        listaUsuarios = new ArrayList<>();

        cargarUsuarios();
        cargarTodosLosMovimientos();
    }

    public void cargarTodosLosMovimientos() {

        try {
            listaMovimientos = facade.listarTodosLosMovimientos();
            aplicarFiltros();

        } catch (Exception e) {
            e.printStackTrace();
            listaMovimientos = new ArrayList<>();
            listaFiltrada = new ArrayList<>();

        }
    }

    public void cargarUsuarios() {

        try {

            listaUsuarios = facade.listarUsuarios();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void aplicarFiltros() {

        listaFiltrada = new ArrayList<>();
        totalEntradas = 0;
        totalSalidas = 0;

        if (listaMovimientos == null) {
            return;
        }


        for (MovimientoInventario mov : listaMovimientos) {

            if (mov == null) {
                continue;
            }
            boolean pasaFiltroUsuario = idUsuarioFiltro == null || idUsuarioFiltro == 0 || (mov.getUsuario() != null && mov.getUsuario().getId() == idUsuarioFiltro);
            boolean pasaFiltroProducto = true;

            if (productoFiltro != null && !productoFiltro.trim().isEmpty()) {

                String filtro = productoFiltro.trim().toLowerCase();

                if (mov.getProducto() == null) {
                    pasaFiltroProducto = false;
                } else {
                    String nombreProducto = mov.getProducto().getNombre() != null ? mov.getProducto().getNombre().toLowerCase() : "";

                    String idProducto = String.valueOf(mov.getProducto().getId());

                    pasaFiltroProducto = nombreProducto.contains(filtro) || idProducto.contains(filtro);
                }
            }
          /*
          AQUI HAZ LOS FILTROS DE LA FECHA, PARA QUE VALIDE TODOS LOS FILTROS JUNTO
          CON EL FILTRO DE LAS FECHAS, ya te deje una variable
          pasoFiltroProducto
           */

        if("Entrada".equalsIgnoreCase(mov.getTipo())) {
            totalEntradas += mov.getCantidad();
        } else if ("Salida".equalsIgnoreCase(mov.getTipo())){
            totalSalidas += mov.getCantidad();
        }

        }
    }
    public double calcularValorTotal(MovimientoInventario mov) {
        if (mov == null || mov.getProducto() == null) {
            return 0;
        }

        return mov.getCantidad() * mov.getProducto().getPrecio();
    }
    public void seleccionarMovimiento(MovimientoInventario mov) {

        this.movimientoSeleccionado = mov;
    }
    public void limpiarFiltros() {

        idUsuarioFiltro = null;
        productoFiltro = null;
        fechaDesde = null;
        fechaHasta = null;

        aplicarFiltros();
    }


    public boolean puedeVerAuditoria(Usuario usuarioLogeado) {

        return usuarioLogeado != null;
    }
    public List<MovimientoInventario> getListaMovimientos() {
        return listaMovimientos;
    }
    public void setListaMovimientos(
            List<MovimientoInventario> listaMovimientos) {

        this.listaMovimientos = listaMovimientos;
    }
    public List<MovimientoInventario> getListaFiltrada() {
        return listaFiltrada;
    }
    public void setListaFiltrada(
            List<MovimientoInventario> listaFiltrada) {

        this.listaFiltrada = listaFiltrada;
    }
    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }
    public void setListaUsuarios(
            List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
    public Integer getIdUsuarioFiltro() {
        return idUsuarioFiltro;
    }
    public void setIdUsuarioFiltro(Integer idUsuarioFiltro) {
        this.idUsuarioFiltro = idUsuarioFiltro;
    }
    public String getProductoFiltro() {
        return productoFiltro;
    }
    public void setProductoFiltro(String productoFiltro) {
        this.productoFiltro = productoFiltro;
    }
    public Date getFechaDesde() {
        return fechaDesde;
    }
    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }
    public Date getFechaHasta() {
        return fechaHasta;
    }
    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
    public int getTotalEntradas() {
        return totalEntradas;
    }
    public int getTotalSalidas() {
        return totalSalidas;
    }
    public MovimientoInventario getMovimientoSeleccionado() {
        return movimientoSeleccionado;
    }
    public void setMovimientoSeleccionado(
            MovimientoInventario movimientoSeleccionado) {
        this.movimientoSeleccionado = movimientoSeleccionado;
    }

}