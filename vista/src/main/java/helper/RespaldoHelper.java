package helper;

import mx.puntodeventa.entity.DetalleVenta;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Venta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class RespaldoHelper {
    private static final String RUTA = "C:\\RespaldosPOS\\";
    private static double VENTAFINAL = 0;

    public static double getVENTAFINAL() {
        return VENTAFINAL;
    }

    public static void resetVentaFinal() {
        VENTAFINAL = 0.0;
    }


    public static void guardarVenta(Venta venta, List<DetalleVenta> detalles, Usuario u) {
        try {
            File carpeta = new File(RUTA);

            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("ddMMyyyy");
            String fechaActual = LocalDateTime.now().format(formato);

            String nombreArchivo = RUTA + "CAJA" + venta.getIdCaja() + "_" + fechaActual + ".csv";
            File archivo = new File(nombreArchivo);
            boolean esNuevo = !archivo.exists();

            FileWriter fw = new FileWriter(nombreArchivo, true);
            BufferedWriter bw = new BufferedWriter(fw);

            if (esNuevo) {
                bw.write("Caja,Fecha,Usuario,Producto,Cantidad,Preciounitario,Total de venta");
                bw.newLine();
            }
            for (DetalleVenta d : detalles) {
                String linea =
                        venta.getIdCaja() + "," +
                                LocalDateTime.now() + "," +
                                u.getNombre() + "," +
                                d.getProducto().getNombre() + "," +
                                d.getCantidad() + " Unidades," +
                                d.getPrecioUnitario() + "," +
                                (d.getCantidad() * d.getPrecioUnitario());
                VENTAFINAL = VENTAFINAL + (d.getCantidad() * d.getPrecioUnitario());
                bw.write(linea);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}