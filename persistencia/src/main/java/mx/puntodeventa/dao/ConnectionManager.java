package mx.puntodeventa.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL =
            "jdbc:mysql://localhost:3306/inv_db?useSSL=false&serverTimezone=UTC&autoReconnect=true";
    private static final String USER = "root";
    private static final String PASSWORD = "6864314146";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //

            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la BD");
            return con;

        } catch (ClassNotFoundException e) {
            System.out.println("No se encontró el driver de MySQL");
            e.printStackTrace();
            throw new SQLException(e);
        } catch (SQLException e) {
            System.out.println("Error al conectar a la BD");
            e.printStackTrace();
            throw e;
        }
    }
}