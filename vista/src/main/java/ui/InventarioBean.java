package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import facade.SistemaFacade;
import mx.puntodeventa.dao.InventarioDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("InventarioBean")
@RequestScoped
public class InventarioBean implements Serializable {

    private List<InventarioDTO> listaInventario;
    private InventarioDTO seleccionado;

    private SistemaFacade facade;

    private String nombre;
    private int nuevoStock;

    @PostConstruct
    public void inicio() {
        facade = new SistemaFacade();
        listaInventario = new ArrayList<>();

        try {
            listaInventario = facade.listarInventario();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void buscarProducto() {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                listaInventario = facade.listarInventario();
            } else {
                listaInventario = facade.buscarInventarioPorNombre(nombre);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void modificarProducto() {

        if (seleccionado == null) {
            System.out.println("Debe seleccionar un producto");
            return;
        }

        try {
            facade.actualizarStock(seleccionado.getIdProducto(), nuevoStock);
            listaInventario = facade.listarInventario();

            // limpiar campo
            nuevoStock = 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void eliminarProducto() {

        if (seleccionado == null) {
            System.out.println("Debe seleccionar un producto");
            return;
        }

        try {
            facade.eliminarRegistroInventario(seleccionado.getIdProducto());
            listaInventario = facade.listarInventario();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<InventarioDTO> getListaInventario() {
        return listaInventario;
    }

    public void setListaInventario(List<InventarioDTO> listaInventario) {
        this.listaInventario = listaInventario;
    }

    public InventarioDTO getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(InventarioDTO seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNuevoStock() {
        return nuevoStock;
    }

    public void setNuevoStock(int nuevoStock) {
        this.nuevoStock = nuevoStock;
    }
}