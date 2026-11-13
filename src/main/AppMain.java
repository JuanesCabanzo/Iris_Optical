package main;

import javax.swing.SwingUtilities;
import vista.FrmPrincipal;

/**
 * Punto de entrada de IRIS OPTICAL.
 */
public class AppMain {

    public static void main(String[] args) {

        // Texto más nítido en pantalla
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            FrmPrincipal ventana = new FrmPrincipal();
            ventana.setVisible(true);
        });
    }
}
