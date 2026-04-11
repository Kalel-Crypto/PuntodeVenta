package negocio.service;

import dto.InventarioDTO;
import persistencia.InventarioDAO;

import java.util.List;
import java.util.stream.Collectors;

public class InventarioService {

    private InventarioDAO inventarioDAO = new InventarioDAO();


    public List<InventarioDTO> listarInventario() throws Exception {
        return inventarioDAO.listar(); // si está vacío → lista vacía ✔
    }


    public List<InventarioDTO> buscarExacto(int idProducto, String nombre) throws Exception {

        List<InventarioDTO> lista = inventarioDAO.listar();

        return lista.stream()
                .filter(p -> p.getIdProducto() == idProducto &&
                        p.getNombre().equalsIgnoreCase(nombre))
                .collect(Collectors.toList());
    }


    public List<InventarioDTO> buscarPorNombre(String nombre) throws Exception {

        List<InventarioDTO> lista = inventarioDAO.listar();

        return lista.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }


    public void actualizarStock(int idProducto, int nuevoStock) throws Exception {

        if (nuevoStock < 0) {
            throw new Exception("El stock no puede ser negativo");
        }

        inventarioDAO.actualizarStock(idProducto, nuevoStock);
    }


    public void eliminarRegistro(int idProducto) throws Exception {
        inventarioDAO.eliminar(idProducto);
    }
}