package ui;

import facade.SistemaFacade;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import mx.puntodeventa.entity.Caja;
import mx.puntodeventa.entity.DetalleVenta;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Venta;

import java.io.Serializable;

@Named("VentaBean")
@SessionScoped
public class VentaBean implements Serializable {
    Venta venta;
    DetalleVenta detalleVenta;
    Usuario usuario;
    SistemaFacade facade;
    LoginBeanUI usuarioLogeado;
    Caja cajaActual;

    @PostConstruct
    public void init(){
        facade = new SistemaFacade();
        cajaActual.setIdcaja(1);

    }

}
