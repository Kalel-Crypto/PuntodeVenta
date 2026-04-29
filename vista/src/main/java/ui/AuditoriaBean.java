package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import facade.SistemaFacade;
import mx.puntodeventa.entity.MovimientoInventario;

@Named("AuditoriaBean")
@SessionScoped
public class AuditoriaBean implements Serializable {

    private List<MovimientoInventario> listaMovimientos;
    private List<MovimientoInventario> listaFiltrada;
    private SistemaFacade facade;

    private int totalEntradas;
    private int totalSalidas;
    private String mesFiltro;

    @Inject
    private UsuarioBean usuarioBean;

    @PostConstruct
    public void inicio() {
        facade = new SistemaFacade();
        listaMovimientos = new ArrayList<>();
        listaFiltrada = new ArrayList<>();
        cargarTodosLosMovimientos();
    }

    public void cargarTodosLosMovimientos() {
        try {
            listaMovimientos = facade.listarTodosLosMovimientos();
            aplicarFiltros();
        } catch (Exception e) {
            e.printStackTrace();
            listaMovimientos = new ArrayList<>();
        }
    }

    public void aplicarFiltros() {

        totalEntradas = 0;
        totalSalidas = 0;

        for (MovimientoInventario mov : listaMovimientos) {
            if ("Entrada".equalsIgnoreCase(mov.getTipo())) {
                totalEntradas += mov.getCantidad();
            } else if ("Salida".equalsIgnoreCase(mov.getTipo()) || "Venta".equalsIgnoreCase(mov.getTipo())) {
                totalSalidas += mov.getCantidad();
            }
        }
    }


    public String obtenerEstiloCantidad(int cantidad) {
        return cantidad < 0 ? "color: red; font-weight: bold;" : "";
    }

    public boolean puedeVerAuditoria() {
        if (usuarioBean != null &&
                usuarioBean.getUsuario() != null &&
                usuarioBean.getUsuario().getRol() != null) {

            String rol = usuarioBean.getUsuario().getRol().toString();
            return "ADMINISTRADOR".equalsIgnoreCase(rol) || "ADMIN".equalsIgnoreCase(rol);
        }
        return false;
    }

    public List<MovimientoInventario> getListaMovimientos() { return listaMovimientos; }
    public void setListaMovimientos(List<MovimientoInventario> listaMovimientos) { this.listaMovimientos = listaMovimientos; }

    public int getTotalEntradas() { return totalEntradas; }
    public int getTotalSalidas() { return totalSalidas; }

    public String getMesFiltro() { return mesFiltro; }
    public void setMesFiltro(String mesFiltro) { this.mesFiltro = mesFiltro; }
}