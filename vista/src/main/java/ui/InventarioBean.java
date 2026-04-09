package ui;

import helper.BusquedaHelper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import mx.puntodeventa.entity.Producto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("InventarioBean")
@RequestScoped
public class InventarioBean implements Serializable {
private List<Producto> listaProductos;
BusquedaHelper helper;
Producto productoSeleccionado;
private String nombre;


@PostConstruct
public void inicio(){
    listaProductos = new ArrayList<>();
    helper = new BusquedaHelper();
}


public void buscarProducto(){
   listaProductos = helper.buscar(nombre);
}

public void modificarProducto(){

}

}
