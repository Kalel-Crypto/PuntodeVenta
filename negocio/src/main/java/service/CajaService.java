package service;

import facade.SistemaFacade;
import mx.puntodeventa.dao.CajaDAO;
import mx.puntodeventa.entity.Caja;
import mx.puntodeventa.entity.Usuario;

import java.util.List;

public class CajaService {
    private CajaDAO cajaDAO = new CajaDAO();
    private Caja caja = new Caja();
    private Usuario user = new Usuario();
    UsuarioService usuarioService = new UsuarioService();

    public void verificarCajaExistente(String nombre) throws Exception {
        user = usuarioService.obtenerUsuarioporNombre(nombre);
        if(cajaDAO.obtenerCajaPorId(user.getId()) == null){
            caja.setIdUsuario(user.getId());
            cajaDAO.abrirCaja(caja);
        }
    }

    public Caja traerCajaActual(int id) throws Exception {
        return caja = cajaDAO.obtenerCajaPorId(id);
    }

}
