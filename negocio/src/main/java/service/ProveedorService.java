package service;

import mx.puntodeventa.dao.ProveedorDAO;
import mx.puntodeventa.entity.Proveedor;

import java.util.List;

public class ProveedorService {
    ProveedorDAO dao = new ProveedorDAO();


    public List<Proveedor> obtenerProveedores() throws Exception {
        return dao.listar();
    }

    public void registrar(String nombre, String numero, String marca) throws Exception {

        if(nombre.trim().isEmpty()){
            throw new Exception("No Deje espacios vacios");

        }
        if(!numero.matches("^686\\d{7}$")){
            throw new Exception("El espacio esta vacio o no se respeta el formato");
        }
        if(marca.trim().isEmpty()){
            throw new Exception("No deje espacios vacios");
        }
        Proveedor provedor = new Proveedor();
        provedor.setNombre(nombre);
        provedor.setContacto(numero);
        provedor.setMarca(marca);
        dao.insertar(provedor);
    }
}
