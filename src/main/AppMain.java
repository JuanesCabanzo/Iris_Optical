package main;

import javax.swing.SwingUtilities;
import vista.FrmPrincipal;


public class AppMain {

    public static void main(String[] args) {

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            FrmPrincipal ventana = new FrmPrincipal();
            ventana.setVisible(true);
        });
    }
}
