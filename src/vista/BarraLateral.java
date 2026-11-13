package vista;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Menú lateral de IRIS OPTICAL: marca, navegación entre módulos y estado
 * de la conexión con la base de datos.
 *
 * Los módulos todavía no desarrollados aparecen deshabilitados bajo el
 * encabezado "PRÓXIMAMENTE", de modo que la plataforma muestra su dirección
 * sin aparentar funciones que aún no existen.
 */
public class BarraLateral extends JPanel {

    private final ItemMenu itemInicio = new ItemMenu("Inicio", Icono.INICIO, false);
    private final ItemMenu itemPacientes = new ItemMenu("Pacientes", Icono.PACIENTE, false);
    private final ItemMenu itemExamenes = new ItemMenu("Exámenes visuales", Icono.OJO, true);
    private final ItemMenu itemFormulas = new ItemMenu("Fórmulas ópticas", Icono.DOCUMENTO, true);

    private final JLabel lblEstadoBD
            = Estilo.etiqueta("Verificando conexión…", Font.PLAIN, 12, new Color(0xb9d4ec));
    private Color colorEstado = new Color(0x8fa6bd);

    public BarraLateral(Runnable alInicio, Runnable alPacientes) {

        super(new BorderLayout());
        setPreferredSize(new Dimension(250, 100));

        itemInicio.addActionListener(e -> alInicio.run());
        itemPacientes.addActionListener(e -> alPacientes.run());

        add(new Marca(), BorderLayout.NORTH);
        add(construirMenu(), BorderLayout.CENTER);
        add(construirPie(), BorderLayout.SOUTH);
    }

    public void seleccionarInicio(boolean valor) {
        itemInicio.setSeleccionado(valor);
    }

    public void seleccionarPacientes(boolean valor) {
        itemPacientes.setSeleccionado(valor);
    }

    public void setEstadoBD(boolean conectado) {
        colorEstado = conectado ? new Color(0x3ddc97) : new Color(0xff6b6b);
        lblEstadoBD.setText(conectado ? "MySQL conectado" : "Sin conexión a MySQL");
        lblEstadoBD.getParent().repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, Estilo.NAVY,
                0, getHeight(), new Color(0x0a2a4a)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private JComponent construirMenu() {

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(10, 14, 10, 14));

        menu.add(titulo("MENÚ"));
        menu.add(itemInicio);
        menu.add(Box.createVerticalStrut(4));
        menu.add(itemPacientes);
        menu.add(Box.createVerticalStrut(20));
        menu.add(titulo("PRÓXIMAMENTE"));
        menu.add(itemExamenes);
        menu.add(Box.createVerticalStrut(4));
        menu.add(itemFormulas);
        return menu;
    }

    private JComponent construirPie() {

        JPanel pie = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Estilo.suavizar(g);
                g2.setColor(colorEstado);
                g2.fill(new Ellipse2D.Double(24, 22, 10, 10));
                g2.dispose();
            }
        };
        pie.setOpaque(false);
        pie.setLayout(new BorderLayout());
        pie.setPreferredSize(new Dimension(250, 78));
        pie.setBorder(new EmptyBorder(14, 44, 12, 12));

        pie.add(lblEstadoBD, BorderLayout.NORTH);
        pie.add(Estilo.etiqueta("Proyecto Integrador · USB Bogotá",
                Font.PLAIN, 11, new Color(0x6f8aa6)), BorderLayout.SOUTH);
        return pie;
    }

    private JComponent titulo(String texto) {
        JLabel l = Estilo.etiqueta(texto, Font.BOLD, 11, new Color(0x6f8aa6));
        l.setBorder(new EmptyBorder(8, 12, 8, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /** Logotipo y nombre del sistema en la parte superior del menú. */
    private static class Marca extends JComponent {

        Marca() {
            setPreferredSize(new Dimension(250, 96));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Estilo.suavizar(g);
            Estilo.dibujarLogo(g2, 22, 26, 46);

            g2.setFont(Estilo.fuente(Font.BOLD, 20));
            g2.setColor(Color.WHITE);
            g2.drawString("IRIS", 80, 47);
            int ancho = g2.getFontMetrics().stringWidth("IRIS ");
            g2.setColor(Estilo.CYAN);
            g2.drawString("OPTICAL", 80 + ancho, 47);

            g2.setFont(Estilo.fuente(Font.PLAIN, 11));
            g2.setColor(new Color(0xb9d4ec));
            g2.drawString("Gestión óptica", 81, 64);

            g2.setColor(new Color(255, 255, 255, 25));
            g2.fillRect(18, getHeight() - 2, getWidth() - 36, 1);
            g2.dispose();
        }
    }

    // =====================================================================
    //  ELEMENTO DEL MENÚ
    // =====================================================================
    private enum Icono {
        INICIO, PACIENTE, OJO, DOCUMENTO
    }

    private static class ItemMenu extends JButton {

        private final Icono icono;
        private final boolean proximamente;
        private boolean seleccionado;

        ItemMenu(String texto, Icono icono, boolean proximamente) {
            super(texto);
            this.icono = icono;
            this.proximamente = proximamente;
            setFont(Estilo.fuente(Font.PLAIN, 14));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setPreferredSize(new Dimension(200, 44));
            if (proximamente) {
                setEnabled(false);
                setToolTipText("Este módulo se desarrollará en etapas posteriores del proyecto.");
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }

        void setSeleccionado(boolean valor) {
            this.seleccionado = valor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Estilo.suavizar(g);

            if (seleccionado) {
                g2.setColor(new Color(79, 195, 247, 40));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Estilo.CYAN);
                g2.fill(new RoundRectangle2D.Double(0, 10, 4, getHeight() - 20, 4, 4));
            } else if (getModel().isRollover() && isEnabled()) {
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
            }

            Color color = !isEnabled() ? new Color(0x5f7b98)
                    : seleccionado ? Color.WHITE : new Color(0xc4d8ec);

            dibujarIcono(g2, color, 16, (getHeight() - 20) / 2);

            g2.setFont(Estilo.fuente(seleccionado ? Font.BOLD : Font.PLAIN, 14));
            g2.setColor(color);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), 48,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }

        private void dibujarIcono(Graphics2D g2, Color color, int x, int y) {

            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.translate(x, y);

            switch (icono) {
                case INICIO: {
                    Path2D casa = new Path2D.Double();
                    casa.moveTo(2, 10);
                    casa.lineTo(10, 2);
                    casa.lineTo(18, 10);
                    casa.moveTo(4.5, 8.5);
                    casa.lineTo(4.5, 18);
                    casa.lineTo(15.5, 18);
                    casa.lineTo(15.5, 8.5);
                    g2.draw(casa);
                    break;
                }
                case PACIENTE:
                    g2.draw(new Ellipse2D.Double(6, 1.5, 8, 8));
                    g2.drawArc(2, 12, 16, 14, 0, 180);
                    break;
                case OJO: {
                    Path2D ojo = new Path2D.Double();
                    ojo.moveTo(1, 10);
                    ojo.quadTo(10, 0, 19, 10);
                    ojo.quadTo(10, 20, 1, 10);
                    g2.draw(ojo);
                    g2.fill(new Ellipse2D.Double(7, 7, 6, 6));
                    break;
                }
                default:
                    g2.draw(new RoundRectangle2D.Double(3.5, 1.5, 13, 17, 4, 4));
                    g2.drawLine(7, 7, 13, 7);
                    g2.drawLine(7, 11, 13, 11);
                    g2.drawLine(7, 15, 11, 15);
            }
            g2.translate(-x, -y);
        }
    }
}
