package vista;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;


public final class Estilo {

    private Estilo() {
    }

    // PALETA 
    public static final Color NAVY = new Color(0x071c33);
    public static final Color NAVY2 = new Color(0x0c3b64);
    public static final Color ACCENT = new Color(0x1e88e5);
    public static final Color ACCENT_OSCURO = new Color(0x1565c0);
    public static final Color CYAN = new Color(0x4fc3f7);
    public static final Color FONDO = new Color(0xf2f6fa);
    public static final Color BLANCO = Color.WHITE;
    public static final Color TEXTO = new Color(0x1d2730);
    public static final Color MUTED = new Color(0x5b6b7a);
    public static final Color BORDE = new Color(0xd9e3ee);
    public static final Color FILA_ALTERNA = new Color(0xf7fafd);
    public static final Color SELECCION = new Color(0xdcecfb);
    public static final Color OK = new Color(0x1b8a5a);
    public static final Color ROJO = new Color(0xc62828);
    public static final Color PROXIMO = new Color(0xb7791f);

    // FUENTES 
    private static final String FAMILIA = elegirFamilia();

    private static String elegirFamilia() {
        Set<String> disponibles = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()));
        for (String candidata : new String[]{"Segoe UI", "Roboto",
            "Helvetica Neue", "Arial"}) {
            if (disponibles.contains(candidata)) {
                return candidata;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font(FAMILIA, estilo, tamano);
    }

    public static Graphics2D suavizar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    public static JLabel etiqueta(String texto, int estilo, int tamano, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(estilo, tamano));
        l.setForeground(color);
        return l;
    }

    // LOGO 
    public static void dibujarLogo(Graphics2D g2, int x, int y, int d) {
        double s = d / 200.0;
        g2.translate(x, y);
        g2.scale(s, s);

        g2.setColor(NAVY2);
        g2.fill(new Ellipse2D.Double(6, 6, 188, 188));
        g2.setPaint(new GradientPaint(0, 0, CYAN, 200, 200, ACCENT_OSCURO));
        g2.setStroke(new BasicStroke(6f));
        g2.draw(new Ellipse2D.Double(6, 6, 188, 188));

        Path2D ojo = new Path2D.Double();
        ojo.moveTo(18, 100);
        ojo.quadTo(100, 28, 182, 100);
        ojo.quadTo(100, 172, 18, 100);
        ojo.closePath();
        g2.setColor(new Color(0xf4faff));
        g2.fill(ojo);

        g2.setPaint(new RadialGradientPaint(100, 100, 40,
                new float[]{0f, 0.55f, 1f},
                new Color[]{NAVY2, ACCENT, new Color(0x81d4fa)}));
        g2.fill(new Ellipse2D.Double(60, 60, 80, 80));
        g2.setColor(new Color(0x061c33));
        g2.fill(new Ellipse2D.Double(84, 84, 32, 32));
        g2.setColor(new Color(255, 255, 255, 230));
        g2.fill(new Ellipse2D.Double(81, 81, 14, 14));

        g2.setPaint(new GradientPaint(0, 0, CYAN, 200, 200, ACCENT_OSCURO));
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.draw(ojo);

        g2.scale(1 / s, 1 / s);
        g2.translate(-x, -y);
    }

    public static Image iconoApp(int tamano) {
        BufferedImage img = new BufferedImage(tamano, tamano,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = suavizar(img.getGraphics());
        dibujarLogo(g2, 0, 0, tamano);
        g2.dispose();
        return img;
    }


    public static class Tarjeta extends JPanel {

        public Tarjeta(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(20, 22, 22, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizar(g);
            g2.setColor(new Color(7, 28, 51, 14));
            g2.fill(new RoundRectangle2D.Double(1, 3, getWidth() - 2,
                    getHeight() - 3, 18, 18));
            g2.setColor(BLANCO);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1,
                    getHeight() - 3, 18, 18));
            g2.setColor(BORDE);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1,
                    getHeight() - 4, 18, 18));
            g2.dispose();
            super.paintComponent(g);
        }
    }

   
    //  BOTÓN
    
    public enum Tipo {
        PRIMARIO, SECUNDARIO, PELIGRO, PELIGRO_SOLIDO
    }

    public static class Boton extends JButton {

        private final Tipo tipo;

        public Boton(String texto, Tipo tipo) {
            super(texto);
            this.tipo = tipo;
            setFont(fuente(Font.BOLD, 13));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 20, 0, 20));
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(getText()) + 44, 40);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizar(g);
            boolean activo = isEnabled();
            boolean encima = getModel().isRollover();
            boolean presionado = getModel().isPressed();

            Color relleno;
            Color borde = null;
            Color texto;

            switch (tipo) {
                case PRIMARIO:
                    relleno = presionado ? NAVY2
                            : encima ? ACCENT_OSCURO : ACCENT;
                    texto = BLANCO;
                    break;
                case PELIGRO_SOLIDO:
                    relleno = presionado ? new Color(0x8e1c1c)
                            : encima ? new Color(0xa82323) : ROJO;
                    texto = BLANCO;
                    break;
                case PELIGRO:
                    relleno = encima ? new Color(0xfdecea) : BLANCO;
                    borde = new Color(0xe5a3a3);
                    texto = ROJO;
                    break;
                default:
                    relleno = encima ? new Color(0xeef4fa) : BLANCO;
                    borde = BORDE;
                    texto = NAVY2;
            }

            if (!activo) {
                relleno = new Color(0xe6ecf2);
                borde = null;
                texto = new Color(0x9aa8b5);
            }

            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5,
                    getWidth() - 2, getHeight() - 2, 12, 12);
            g2.setColor(relleno);
            g2.fill(forma);

            if (borde != null) {
                g2.setColor(borde);
                g2.draw(forma);
            }

            g2.setFont(getFont());
            g2.setColor(texto);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }


    public static class Campo extends JTextField {

        private final String sugerencia;
        private boolean error;

        public Campo(String sugerencia) {
            this.sugerencia = sugerencia;
            setOpaque(false);
            setFont(fuente(Font.PLAIN, 14));
            setForeground(TEXTO);
            setCaretColor(ACCENT);
            setSelectionColor(SELECCION);
            setBorder(new EmptyBorder(8, 13, 8, 13));

            // al escribir se quita el marcado de error
            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    marcarError(false);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    marcarError(false);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    repaint();
                }
            });
        }

        public void marcarError(boolean valor) {
            if (this.error != valor) {
                this.error = valor;
                repaint();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, 40);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizar(g);
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5,
                    getWidth() - 2, getHeight() - 2, 12, 12);

            g2.setColor(isEditable() ? BLANCO : new Color(0xf0f4f8));
            g2.fill(forma);

            if (error) {
                g2.setColor(ROJO);
                g2.setStroke(new BasicStroke(1.6f));
            } else if (hasFocus()) {
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(BORDE);
                g2.setStroke(new BasicStroke(1.1f));
            }
            g2.draw(forma);

            if (getText().isEmpty() && sugerencia != null && !hasFocus()) {
                g2.setFont(getFont());
                g2.setColor(new Color(0x9aa8b5));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(sugerencia, getInsets().left,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }


    //  SELECTOR SEGMENTADO (para el sexo: M / F / Otro)
  
    public static class Segmentado extends JPanel {

        private final ButtonGroup grupo = new ButtonGroup();
        private final String[] valores;
        private final JToggleButton[] botones;
        private boolean error;

        public Segmentado(String[] etiquetas, String[] valores) {
            super(new GridLayout(1, etiquetas.length, 6, 0));
            this.valores = valores;
            this.botones = new JToggleButton[etiquetas.length];
            setOpaque(false);

            for (int i = 0; i < etiquetas.length; i++) {
                final JToggleButton b = new JToggleButton(etiquetas[i]) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = suavizar(g);
                        RoundRectangle2D forma = new RoundRectangle2D.Double(
                                0.5, 0.5, getWidth() - 2, getHeight() - 2, 12, 12);
                        if (isSelected()) {
                            g2.setColor(ACCENT);
                            g2.fill(forma);
                        } else {
                            g2.setColor(getModel().isRollover()
                                    ? new Color(0xeef4fa) : BLANCO);
                            g2.fill(forma);
                            g2.setColor(error ? ROJO : BORDE);
                            g2.draw(forma);
                        }
                        g2.setFont(getFont());
                        g2.setColor(isSelected() ? BLANCO : NAVY2);
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(getText(),
                                (getWidth() - fm.stringWidth(getText())) / 2,
                                (getHeight() - fm.getHeight()) / 2
                                + fm.getAscent());
                        g2.dispose();
                    }
                };
                b.setFont(fuente(Font.BOLD, 13));
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setOpaque(false);
                b.setRolloverEnabled(true);
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                b.addActionListener(e -> marcarError(false));
                botones[i] = b;
                grupo.add(b);
                add(b);
            }
        }

        public String getValor() {
            for (int i = 0; i < botones.length; i++) {
                if (botones[i].isSelected()) {
                    return valores[i];
                }
            }
            return null;
        }

        public void setValor(String valor) {
            error = false;
            grupo.clearSelection();
            for (int i = 0; i < valores.length; i++) {
                if (valores[i].equals(valor)) {
                    botones[i].setSelected(true);
                }
            }
            repaint();
        }

        public void marcarError(boolean valor) {
            this.error = valor;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 40);
        }
    }

  
    //  AVISO: franja de mensajes (éxito, error, información)

    public enum Nivel {
        EXITO, ERROR, INFO
    }

    public static class Aviso extends JPanel {

        private String mensaje;
        private Nivel nivel = Nivel.INFO;
        private final javax.swing.Timer reloj;

        public Aviso() {
            setOpaque(false);
            reloj = new javax.swing.Timer(4500, e -> ocultar());
            reloj.setRepeats(false);
        }

        public void mostrar(String texto, Nivel nivel) {
            this.mensaje = texto;
            this.nivel = nivel;
            reloj.restart();
            repaint();
        }

        public void ocultar() {
            mensaje = null;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(100, 46);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (mensaje == null) {
                return;
            }
            Graphics2D g2 = suavizar(g);
            Color fondo, texto;
            String icono;
            switch (nivel) {
                case EXITO:
                    fondo = new Color(0xe3f4ec);
                    texto = new Color(0x13694a);
                    icono = "✓";
                    break;
                case ERROR:
                    fondo = new Color(0xfdecea);
                    texto = new Color(0x9b1c1c);
                    icono = "!";
                    break;
                default:
                    fondo = new Color(0xe6f1fc);
                    texto = NAVY2;
                    icono = "i";
            }
            RoundRectangle2D forma = new RoundRectangle2D.Double(0, 2,
                    getWidth() - 1, getHeight() - 8, 12, 12);
            g2.setColor(fondo);
            g2.fill(forma);
            g2.setColor(texto);
            g2.fill(new Ellipse2D.Double(14, 11, 20, 20));
            g2.setFont(fuente(Font.BOLD, 13));
            g2.setColor(fondo);
            FontMetrics fmIcono = g2.getFontMetrics();
            g2.drawString(icono, 24 - fmIcono.stringWidth(icono) / 2,
                    21 + (fmIcono.getAscent() - fmIcono.getDescent()) / 2);
            g2.setFont(fuente(Font.PLAIN, 14));
            g2.setColor(texto);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(mensaje, 44,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 2);
            g2.dispose();
        }
    }

    
    //  BARRA DE DESPLAZAMIENTO delgada
 
    public static class BarraFina extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0xb9c8d8);
            trackColor = BLANCO;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return botonNulo();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return botonNulo();
        }

        private JButton botonNulo() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) {
                return;
            }
            Graphics2D g2 = suavizar(g);
            g2.setColor(isThumbRollover() ? new Color(0x8fa6bd) : thumbColor);
            g2.fill(new RoundRectangle2D.Double(r.x + 2, r.y + 2,
                    r.width - 4, r.height - 4, 8, 8));
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(10, 10);
        }
    }

    public static JScrollPane scroll(Component contenido) {
        JScrollPane sp = new JScrollPane(contenido);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BLANCO);
        sp.getVerticalScrollBar().setUI(new BarraFina());
        sp.getHorizontalScrollBar().setUI(new BarraFina());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

  
    //  DIÁLOGO DE CONFIRMACIÓN 

    public static boolean confirmar(Window padre, String titulo,
            String mensaje, String textoConfirmar, boolean peligro) {

        final boolean[] resultado = {false};
        final JDialog dialogo = new JDialog(padre,
                Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(BLANCO);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                new EmptyBorder(26, 28, 22, 28)));

        JLabel lblTitulo = etiqueta(titulo, Font.BOLD, 18, NAVY2);
        JLabel lblMensaje = new JLabel("<html><body style='width:320px'>"
                + mensaje + "</body></html>");
        lblMensaje.setFont(fuente(Font.PLAIN, 14));
        lblMensaje.setForeground(TEXTO);

        JPanel textos = new JPanel(new BorderLayout(0, 10));
        textos.setOpaque(false);
        textos.add(lblTitulo, BorderLayout.NORTH);
        textos.add(lblMensaje, BorderLayout.CENTER);

        Boton cancelar = new Boton("Cancelar", Tipo.SECUNDARIO);
        Boton aceptar = new Boton(textoConfirmar,
                peligro ? Tipo.PELIGRO_SOLIDO : Tipo.PRIMARIO);
        cancelar.addActionListener(e -> dialogo.dispose());
        aceptar.addActionListener(e -> {
            resultado[0] = true;
            dialogo.dispose();
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(aceptar);

        contenido.add(textos, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        dialogo.setContentPane(contenido);

        dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogo.getRootPane().setDefaultButton(aceptar);

        dialogo.pack();
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
        return resultado[0];
    }
}
