package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Date fechaInicio;
    private Date fechaFin;

    private SistemaFacade facade;

    private int totalEntradas;
    private int totalSalidas;
    private String mesFiltro;
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

        for (MovimientoInventario mov : listaMovimientos) {

            boolean pasaFiltroUsuario = (idUsuarioFiltro == null || idUsuarioFiltro == 0) ||
                    (mov.getUsuario() != null && mov.getUsuario().getId() == idUsuarioFiltro);

            boolean pasaFiltroFecha = true;
            if (mov.getFecha() !=null) {
                if(fechaInicio != null && mov.getFecha().before(fechaInicio)) {
                    pasaFiltroFecha = false;
                }
                if (fechaFin != null){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fechaFin);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);

                    if (mov.getFecha().after(cal.getTime())) {
                        pasaFiltroFecha = false;
                    }
                }
            }
            if (pasaFiltroUsuario && pasaFiltroFecha) {
                listaFiltrada.add(mov);


                if ("Entrada".equalsIgnoreCase(mov.getTipo())) {
                    totalEntradas += mov.getCantidad();
                } else if ("Salida".equalsIgnoreCase(mov.getTipo()) || "Venta".equalsIgnoreCase(mov.getTipo())) {
                    totalSalidas += mov.getCantidad();
                }
            }
        }
    }
    public void limpiarFiltros(){
        this.idUsuarioFiltro = null;
        this.fechaInicio = null;
        this.fechaFin = null;
        aplicarFiltros();
    }

    public boolean puedeVerAuditoria(Usuario usuarioLogeado) {
        if(usuarioLogeado != null){
            return true;
        }
        return false;
    }


    public List<MovimientoInventario> getListaMovimientos() { return listaMovimientos; }
    public void setListaMovimientos(List<MovimientoInventario> listaMovimientos) { this.listaMovimientos = listaMovimientos; }

    public List<MovimientoInventario> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<MovimientoInventario> listaFiltrada) { this.listaFiltrada = listaFiltrada; }

    public List<Usuario> getListaUsuarios() { return listaUsuarios; }
    public void setListaUsuarios(List<Usuario> listaUsuarios) { this.listaUsuarios = listaUsuarios; }

    public Integer getIdUsuarioFiltro() { return idUsuarioFiltro; }
    public void setIdUsuarioFiltro(Integer idUsuarioFiltro) { this.idUsuarioFiltro = idUsuarioFiltro; }

    public int getTotalEntradas() { return totalEntradas; }
    public int getTotalSalidas() { return totalSalidas; }

    public String getMesFiltro() { return mesFiltro; }
    public void setMesFiltro(String mesFiltro) { this.mesFiltro = mesFiltro; }

    public Usuario getUsuarioLogeado() { return usuarioLogeado; }
    public void setUsuarioLogeado(Usuario usuarioLogeado) { this.usuarioLogeado = usuarioLogeado; }

    public Date getFechaInicio(){ return fechaInicio;}
    public void setFechaInicio(Date fechaInicio){this.fechaInicio = fechaInicio;}

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }


}