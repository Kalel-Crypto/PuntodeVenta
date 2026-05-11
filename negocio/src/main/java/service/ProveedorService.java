package service;

import mx.puntodeventa.dao.ProveedorDAO;
import mx.puntodeventa.entity.Proveedor;

public class ProveedorService {
    ProveedorDAO dao = new ProveedorDAO();


    public void registrar(String nombre, String numero) throws Exception {

        if(nombre.trim().isEmpty()){
            throw new Exception("No Deje espacios vacios");

        }
        if(!numero.matches("^686\\d{7}$")){
            throw new Exception("El espacio esta vacio o no se respeta el formato");
        }
        Proveedor provedor = new Proveedor();
        provedor.setNombre(nombre);
        provedor.setContacto(numero);
        dao.insertar(provedor);
    }
}
