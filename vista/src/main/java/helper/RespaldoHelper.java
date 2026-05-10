package helper;

import mx.puntodeventa.entity.DetalleVenta;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Venta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.List;

public class RespaldoHelper {
    private static final String RUTA = "C:\\RespaldosPOS\\";

    public static void guardarVenta(Venta venta, List<DetalleVenta> detalles, Usuario u) {
        try {
            File carpeta = new File(RUTA);

            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            String nombreArchivo = RUTA + "Caja_" + venta.getIdCaja() + "_ACTIVA.csv";

            FileWriter fw = new FileWriter(nombreArchivo, true);

            BufferedWriter bw = new BufferedWriter(fw);

            for (DetalleVenta d : detalles) {
                String linea =
                        venta.getIdCaja() + "," +
                                LocalDateTime.now() + "," +
                                u.getNombre() + "," +
                                d.getProducto().getNombre() + "," +
                                d.getCantidad() + " Unidades," +
                                d.getPrecioUnitario() + "," +
                                (d.getCantidad() * d.getPrecioUnitario());
                bw.write(linea);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}