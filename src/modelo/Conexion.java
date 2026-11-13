package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;


public class Conexion {

    private static final String URL_POR_DEFECTO
            = "jdbc:mysql://localhost:3306/sistema_iris_v2"
            + "?useSSL=false&serverTimezone=UTC&connectTimeout=5000";

    private static final String USER_POR_DEFECTO = "root";

    private static final String PASSWORD_POR_DEFECTO = "root";

    private static final Properties CONFIG = cargarConfiguracion();

    private static Properties cargarConfiguracion() {

        Properties propiedades = new Properties();
        File archivo = new File("db.properties");

        if (archivo.isFile()) {
            try (InputStream in = new FileInputStream(archivo)) {
                propiedades.load(in);
            } catch (IOException e) {
                System.err.println("No se pudo leer db.properties: "
                        + e.getMessage());
            }
        }

        return propiedades;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url", URL_POR_DEFECTO),
                CONFIG.getProperty("db.user", USER_POR_DEFECTO),
                CONFIG.getProperty("db.password", PASSWORD_POR_DEFECTO));
    }

    
    public static boolean estaDisponible() {

        try (Connection con = getConnection()) {
            return con.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean probarConexion() {

        try (Connection con = getConnection()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Conexión exitosa con MySQL.",
                    "IRIS OPTICAL",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return true;

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al conectar con MySQL:\n" + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }
}
