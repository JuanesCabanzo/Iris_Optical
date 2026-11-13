package vista;

import controlador.CtrlPaciente;
import java.awt.*;
import javax.swing.*;

/**
 * Ventana principal de IRIS OPTICAL: menú lateral y área de contenido.
 */
public class FrmPrincipal extends JFrame {

    private static final String PANEL_INICIO = "inicio";
    private static final String PANEL_PACIENTES = "pacientes";

    private final CtrlPaciente controlador = new CtrlPaciente();

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);

    private final PanelPacientes panelPacientes = new PanelPacientes();
    private final PanelInicio panelInicio;
    private final BarraLateral barra;

    public FrmPrincipal() {

        super("IRIS OPTICAL — Sistema Integral de Gestión Óptica");

        panelInicio = new PanelInicio(
                () -> {
                    mostrar(PANEL_PACIENTES);
                    panelPacientes.iniciarRegistro();
                },
                () -> {
                    mostrar(PANEL_PACIENTES);
                    panelPacientes.enfocarBusqueda();
                });

        barra = new BarraLateral(
                () -> mostrar(PANEL_INICIO),
                () -> mostrar(PANEL_PACIENTES));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImages(java.util.Arrays.asList(
                Estilo.iconoApp(32), Estilo.iconoApp(64), Estilo.iconoApp(128)));

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1320, pantalla.width - 40),
                Math.min(820, pantalla.height - 60));
        setMinimumSize(new Dimension(1100, 680));

        setLayout(new BorderLayout());
        add(barra, BorderLayout.WEST);

        contenido.setBackground(Estilo.FONDO);
        contenido.add(panelInicio, PANEL_INICIO);
        contenido.add(panelPacientes, PANEL_PACIENTES);
        add(contenido, BorderLayout.CENTER);

        mostrar(PANEL_INICIO);
        setLocationRelativeTo(null);
    }

    private void mostrar(String panel) {

        cartas.show(contenido, panel);
        barra.seleccionarInicio(PANEL_INICIO.equals(panel));
        barra.seleccionarPacientes(PANEL_PACIENTES.equals(panel));

        if (PANEL_INICIO.equals(panel)) {
            panelInicio.refrescar();
        } else {
            panelPacientes.refrescar();
        }
        actualizarEstadoBD();
    }

    private void actualizarEstadoBD() {

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controlador.baseDeDatosDisponible();
            }

            @Override
            protected void done() {
                boolean ok = false;
                try {
                    ok = get();
                } catch (Exception ignorar) {
                    // queda como sin conexión
                }
                barra.setEstadoBD(ok);
            }
        }.execute();
    }
}
