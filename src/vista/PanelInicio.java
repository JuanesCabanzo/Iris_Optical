package vista;

import controlador.CtrlPaciente;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla de inicio: saludo, indicadores reales del sistema, accesos
 * rápidos y estado de los módulos. Solo muestra información que existe:
 * el conteo de pacientes y el estado de la conexión salen de MySQL.
 */
public class PanelInicio extends JPanel {

    private final CtrlPaciente controlador = new CtrlPaciente();

    private final JLabel valorPacientes = Estilo.etiqueta("—", Font.BOLD, 40, Estilo.NAVY2);
    private final JLabel textoPacientes = Estilo.etiqueta("Consultando…", Font.PLAIN, 13, Estilo.MUTED);
    private final JLabel valorBD = Estilo.etiqueta("—", Font.BOLD, 26, Estilo.NAVY2);
    private final JLabel textoBD = Estilo.etiqueta("sistema_iris_v2", Font.PLAIN, 13, Estilo.MUTED);

    public PanelInicio(Runnable irARegistrar, Runnable irABuscar) {

        setLayout(new BorderLayout(0, 22));
        setBackground(Estilo.FONDO);
        setBorder(new EmptyBorder(26, 30, 24, 30));

        add(construirEncabezado(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 20));
        centro.setOpaque(false);
        centro.add(construirIndicadores(), BorderLayout.NORTH);

        JPanel inferior = new JPanel(new GridLayout(1, 2, 20, 0));
        inferior.setOpaque(false);
        inferior.add(construirAcciones(irARegistrar, irABuscar));
        inferior.add(construirEstadoModulos());

        JPanel pila = new JPanel(new BorderLayout());
        pila.setOpaque(false);
        pila.add(inferior, BorderLayout.NORTH);
        centro.add(pila, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
    }

    private JComponent construirEncabezado() {

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern(
                "EEEE, d 'de' MMMM 'de' yyyy", Locale.of("es", "CO")));
        fecha = Character.toUpperCase(fecha.charAt(0)) + fecha.substring(1);

        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(Estilo.etiqueta("Bienvenido a IRIS OPTICAL", Font.BOLD, 28, Estilo.NAVY2));
        p.add(Box.createVerticalStrut(2));
        p.add(Estilo.etiqueta(fecha + "  ·  Sistema Integral de Gestión Óptica",
                Font.PLAIN, 14, Estilo.MUTED));
        return p;
    }

    private JComponent construirIndicadores() {

        JPanel fila = new JPanel(new GridLayout(1, 3, 20, 0));
        fila.setOpaque(false);

        fila.add(indicador("PACIENTES REGISTRADOS", valorPacientes, textoPacientes));
        fila.add(indicador("BASE DE DATOS", valorBD, textoBD));
        fila.add(indicador("MÓDULOS",
                Estilo.etiqueta("1 activo", Font.BOLD, 26, Estilo.NAVY2),
                Estilo.etiqueta("2 proyectados", Font.PLAIN, 13, Estilo.MUTED)));
        return fila;
    }

    private JComponent indicador(String titulo, JLabel valor, JLabel detalle) {

        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 6));
        t.add(Estilo.etiqueta(titulo, Font.BOLD, 12, Estilo.MUTED), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(valor, BorderLayout.CENTER);
        t.add(centro, BorderLayout.CENTER);
        t.add(detalle, BorderLayout.SOUTH);
        return t;
    }

    private JComponent construirAcciones(Runnable registrar, Runnable buscar) {

        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 16));
        t.add(Estilo.etiqueta("Acciones rápidas", Font.BOLD, 18, Estilo.NAVY2),
                BorderLayout.NORTH);

        Estilo.Boton btnRegistrar = new Estilo.Boton("+  Registrar paciente", Estilo.Tipo.PRIMARIO);
        Estilo.Boton btnBuscar = new Estilo.Boton("Buscar paciente", Estilo.Tipo.SECUNDARIO);
        btnRegistrar.addActionListener(e -> registrar.run());
        btnBuscar.addActionListener(e -> buscar.run());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        botones.setOpaque(false);
        botones.add(btnRegistrar);
        botones.add(Box.createHorizontalStrut(10));
        botones.add(btnBuscar);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        JLabel descripcion = new JLabel("<html><body style='width:350px'>Gestiona la información de los "
                + "pacientes de la óptica: registra nuevos pacientes, consulta "
                + "sus datos y mantenlos al día.</body></html>");
        descripcion.setFont(Estilo.fuente(Font.PLAIN, 14));
        descripcion.setForeground(Estilo.TEXTO);
        centro.add(descripcion, BorderLayout.NORTH);
        centro.add(botones, BorderLayout.CENTER);

        t.add(centro, BorderLayout.CENTER);
        return t;
    }

    private JComponent construirEstadoModulos() {

        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 12));
        t.add(Estilo.etiqueta("Estado de los módulos", Font.BOLD, 18, Estilo.NAVY2),
                BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.add(filaModulo("Gestión de pacientes", "ACTIVO", Estilo.OK));
        lista.add(filaModulo("Exámenes visuales", "PROYECTADO", Estilo.PROXIMO));
        lista.add(filaModulo("Fórmulas ópticas", "PROYECTADO", Estilo.PROXIMO));
        t.add(lista, BorderLayout.CENTER);
        return t;
    }

    private JComponent filaModulo(String nombre, String estado, Color color) {

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xe8eef5)));
        fila.add(Estilo.etiqueta(nombre, Font.PLAIN, 14, Estilo.TEXTO), BorderLayout.CENTER);

        JPanel derecha = new JPanel(new GridBagLayout());
        derecha.setOpaque(false);
        derecha.add(new Pastilla(estado, color));
        fila.add(derecha, BorderLayout.EAST);
        fila.setPreferredSize(new Dimension(100, 46));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return fila;
    }

    /** Actualiza los indicadores consultando MySQL en segundo plano. */
    public void refrescar() {

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                return controlador.contarPacientes();
            }

            @Override
            protected void done() {
                int total = -1;
                try {
                    total = get();
                } catch (Exception ignorar) {
                    // se muestra como sin conexión
                }

                if (total >= 0) {
                    valorPacientes.setText(String.valueOf(total));
                    textoPacientes.setText(total == 1
                            ? "paciente en el sistema" : "pacientes en el sistema");
                    valorBD.setText("Conectada");
                    valorBD.setForeground(Estilo.OK);
                } else {
                    valorPacientes.setText("—");
                    textoPacientes.setText("No se pudo consultar");
                    valorBD.setText("Sin conexión");
                    valorBD.setForeground(Estilo.ROJO);
                }
            }
        }.execute();
    }

    /** Etiqueta redondeada de estado. */
    private static class Pastilla extends JComponent {

        private final String texto;
        private final Color color;

        Pastilla(String texto, Color color) {
            this.texto = texto;
            this.color = color;
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(Estilo.fuente(Font.BOLD, 11));
            return new Dimension(fm.stringWidth(texto) + 24, 24);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Estilo.suavizar(g);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                    getHeight(), getHeight()));
            g2.setColor(Color.WHITE);
            g2.setFont(Estilo.fuente(Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
