package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
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

            if (pasaFiltroUsuario) {
                listaFiltrada.add(mov);


                if ("Entrada".equalsIgnoreCase(mov.getTipo())) {
                    totalEntradas += mov.getCantidad();
                } else if ("Salida".equalsIgnoreCase(mov.getTipo()) || "Venta".equalsIgnoreCase(mov.getTipo())) {
                    totalSalidas += mov.getCantidad();
                }
            }
        }
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
}