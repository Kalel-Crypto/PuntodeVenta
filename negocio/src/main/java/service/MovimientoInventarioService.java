package service;

import mx.puntodeventa.dao.VentaDAO;
import mx.puntodeventa.entity.DetalleVenta;

import java.sql.SQLException;

public class MovimientoInventarioService {
    VentaDAO ventaDAO = new VentaDAO();
    DetalleVenta detalleVenta = new DetalleVenta();


    public boolean  conseguirMovimientoExistente(int id) throws SQLException {
        detalleVenta = ventaDAO.conseguirRegistro(id);
        if(detalleVenta != null){
            return true;
        }
        return false;
    }


}
