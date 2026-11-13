package vista;

import controlador.CtrlPaciente;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Paciente;


public class PanelPacientes extends JPanel {

    private final CtrlPaciente controlador = new CtrlPaciente();
    private final Estilo.Aviso aviso = new Estilo.Aviso();

    // listado 
    private final Estilo.Campo txtBuscar = new Estilo.Campo(
            "Buscar por nombre, apellido, cédula, teléfono o correo");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Cédula", "Paciente", "Teléfono"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columna) {
            return columna == 0 ? Integer.class : String.class;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final javax.swing.table.TableRowSorter<DefaultTableModel> sorter
            = new javax.swing.table.TableRowSorter<>(modelo);
    private final JLabel lblConteo = Estilo.etiqueta("", Font.PLAIN, 13, Estilo.MUTED);
    private final JLabel lblVacioTitulo = Estilo.etiqueta("", Font.BOLD, 16, Estilo.NAVY2);
    private final JLabel lblVacioTexto = Estilo.etiqueta("", Font.PLAIN, 13, Estilo.MUTED);
    private final CardLayout cartasLista = new CardLayout();
    private final JPanel contenedorLista = new JPanel(cartasLista);
    private final Map<Integer, Paciente> pacientes = new HashMap<>();
    private final javax.swing.Timer temporizadorBusqueda;
    private int solicitudActual = 0;
    private boolean actualizandoTabla = false;

    //  formulario 
    private final Estilo.Campo txtNombre1 = new Estilo.Campo("Ej. María");
    private final Estilo.Campo txtNombre2 = new Estilo.Campo("Opcional");
    private final Estilo.Campo txtApellido1 = new Estilo.Campo("Ej. Gómez");
    private final Estilo.Campo txtApellido2 = new Estilo.Campo("Opcional");
    private final Estilo.Campo txtCedula = new Estilo.Campo("Documento");
    private final Estilo.Segmentado segSexo = new Estilo.Segmentado(
            new String[]{"M", "F", "Otro"}, new String[]{"M", "F", "Otro"});
    private final Estilo.Campo txtFecha = new Estilo.Campo("AAAA-MM-DD");
    private final Estilo.Campo txtEdad = new Estilo.Campo("Años");
    private final Estilo.Campo txtTelefono = new Estilo.Campo("Ej. 300 123 4567");
    private final Estilo.Campo txtCorreo = new Estilo.Campo("correo@ejemplo.com");

    private final JLabel lblModo = Estilo.etiqueta("", Font.BOLD, 19, Estilo.NAVY2);
    private final JLabel lblSubModo = Estilo.etiqueta("", Font.PLAIN, 13, Estilo.MUTED);
    private final Estilo.Boton btnGuardar
            = new Estilo.Boton("Registrar paciente", Estilo.Tipo.PRIMARIO);
    private final Estilo.Boton btnLimpiar
            = new Estilo.Boton("Limpiar", Estilo.Tipo.SECUNDARIO);
    private final Estilo.Boton btnEliminar
            = new Estilo.Boton("Eliminar", Estilo.Tipo.PELIGRO);
    private JPanel panelBotonesDerecha;

    private int idActual = 0;

    public PanelPacientes() {

        setLayout(new BorderLayout(0, 0));
        setBackground(Estilo.FONDO);
        setBorder(new EmptyBorder(26, 30, 22, 30));

        add(construirEncabezado(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(20, 0));
        centro.setOpaque(false);
        centro.add(construirListado(), BorderLayout.CENTER);
        centro.add(construirFormulario(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        temporizadorBusqueda = new javax.swing.Timer(250,
                e -> cargarTabla(txtBuscar.getText(), idActual));
        temporizadorBusqueda.setRepeats(false);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                temporizadorBusqueda.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        // la edad se calcula sola al escribir una fecha válida
        txtFecha.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarEdadDesdeFecha();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarEdadDesdeFecha();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> nuevo(true));

        actualizarModo();
        cargarTabla("", 0);
    }

   
    //  CONSTRUCCIÓN DE LA INTERFAZ
   
    private JComponent construirEncabezado() {

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.add(Estilo.etiqueta("Pacientes", Font.BOLD, 28, Estilo.NAVY2));
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(Estilo.etiqueta(
                "Registra, consulta y actualiza la información de los pacientes.",
                Font.PLAIN, 14, Estilo.MUTED));

        Estilo.Boton btnNuevo
                = new Estilo.Boton("+  Nuevo paciente", Estilo.Tipo.PRIMARIO);
        btnNuevo.addActionListener(e -> nuevo(true));

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.add(titulos, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        derecha.setOpaque(false);
        derecha.add(btnNuevo);
        fila.add(derecha, BorderLayout.EAST);

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        aviso.setAlignmentX(Component.LEFT_ALIGNMENT);
        norte.add(fila);
        norte.add(Box.createVerticalStrut(6));
        norte.add(aviso);
        return norte;
    }

    private JComponent construirListado() {

        Estilo.Tarjeta tarjeta = new Estilo.Tarjeta(new BorderLayout(0, 14));

        JPanel barra = new JPanel(new BorderLayout(14, 0));
        barra.setOpaque(false);
        barra.add(txtBuscar, BorderLayout.CENTER);
        barra.add(lblConteo, BorderLayout.EAST);
        tarjeta.add(barra, BorderLayout.NORTH);

        configurarTabla();

        JPanel vacio = new JPanel();
        vacio.setBackground(Estilo.BLANCO);
        vacio.setLayout(new BoxLayout(vacio, BoxLayout.Y_AXIS));
        lblVacioTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblVacioTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        vacio.add(Box.createVerticalGlue());
        vacio.add(lblVacioTitulo);
        vacio.add(Box.createVerticalStrut(6));
        vacio.add(lblVacioTexto);
        vacio.add(Box.createVerticalGlue());

        contenedorLista.setOpaque(false);
        JScrollPane desplazable = Estilo.scroll(tabla);
        desplazable.setColumnHeaderView(tabla.getTableHeader());
        contenedorLista.add(desplazable, "tabla");
        contenedorLista.add(vacio, "vacio");
        contenedorLista.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        tarjeta.add(contenedorLista, BorderLayout.CENTER);

        return tarjeta;
    }

    private void configurarTabla() {

        tabla.setRowHeight(42);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);
        tabla.setRowSorter(sorter);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFont(Estilo.fuente(Font.PLAIN, 14));
        tabla.setBackground(Estilo.BLANCO);
        tabla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(100, 40));

        DefaultTableCellRenderer celdas = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {

                super.getTableCellRendererComponent(t, valor, seleccionada,
                        false, fila, columna);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setFont(Estilo.fuente(columna == 2 ? Font.BOLD : Font.PLAIN, 14));
                setForeground(seleccionada ? Estilo.NAVY2 : Estilo.TEXTO);
                setBackground(seleccionada ? Estilo.SELECCION
                        : (fila % 2 == 0 ? Estilo.BLANCO : Estilo.FILA_ALTERNA));
                setHorizontalAlignment(
                        columna == 0
                        ? SwingConstants.CENTER : SwingConstants.LEFT);
                return this;
            }
        };
        tabla.setDefaultRenderer(Object.class, celdas);
        tabla.setDefaultRenderer(String.class, celdas);
        tabla.setDefaultRenderer(Integer.class, celdas);

        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {

                super.getTableCellRendererComponent(t, valor, false, false,
                        fila, columna);
                setText(valor == null ? "" : valor.toString().toUpperCase());
                setFont(Estilo.fuente(Font.BOLD, 12));
                setForeground(Estilo.MUTED);
                setBackground(new Color(0xeef4fa));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE),
                        new EmptyBorder(0, 14, 0, 14)));
                setHorizontalAlignment(
                        columna == 0
                        ? SwingConstants.CENTER : SwingConstants.LEFT);
                return this;
            }
        });

        int[] anchos = {55, 125, 260, 130};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || actualizandoTabla) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila < 0 || fila >= tabla.getRowCount()) {
                return;
            }
            int id = (Integer) modelo.getValueAt(
                    tabla.convertRowIndexToModel(fila), 0);
            Paciente p = pacientes.get(id);
            if (p != null) {
                cargarFormulario(p);
            }
        });
    }

    private JComponent construirFormulario() {

        Estilo.Tarjeta tarjeta = new Estilo.Tarjeta(new BorderLayout(0, 16));
        tarjeta.setPreferredSize(new Dimension(420, 100));

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        lblModo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubModo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(lblModo);
        cabecera.add(Box.createVerticalStrut(3));
        cabecera.add(lblSubModo);
        tarjeta.add(cabecera, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(Estilo.BLANCO);
        campos.setBorder(new EmptyBorder(0, 0, 0, 8));

        fila(campos, 0, grupo("Primer nombre *", txtNombre1),
                grupo("Segundo nombre", txtNombre2));
        fila(campos, 1, grupo("Primer apellido *", txtApellido1),
                grupo("Segundo apellido", txtApellido2));
        fila(campos, 2, grupo("Cédula *", txtCedula),
                grupo("Sexo *", segSexo));
        fila(campos, 3, grupo("Fecha de nacimiento", txtFecha),
                grupo("Edad", txtEdad));
        fila(campos, 4, grupo("Teléfono", txtTelefono), null);
        fila(campos, 5, grupo("Correo electrónico", txtCorreo), null);

        // relleno final para que los campos queden arriba
        GridBagConstraints relleno = new GridBagConstraints();
        relleno.gridy = 6;
        relleno.weighty = 1;
        campos.add(Box.createVerticalGlue(), relleno);

        tarjeta.add(Estilo.scroll(campos), BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(btnEliminar, BorderLayout.WEST);
        panelBotonesDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotonesDerecha.setOpaque(false);
        panelBotonesDerecha.add(btnLimpiar);
        panelBotonesDerecha.add(btnGuardar);
        pie.add(panelBotonesDerecha, BorderLayout.EAST);
        tarjeta.add(pie, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JPanel grupo(String titulo, JComponent componente) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(Estilo.etiqueta(titulo, Font.BOLD, 12, Estilo.MUTED),
                BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        return p;
    }

    private void fila(JPanel contenedor, int y, JComponent izq, JComponent der) {

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = y;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 14, 0);

        if (der == null) {
            c.gridx = 0;
            c.gridwidth = 2;
            contenedor.add(izq, c);
            return;
        }

        c.gridx = 0;
        c.insets = new Insets(0, 0, 14, 7);
        contenedor.add(izq, c);

        c.gridx = 1;
        c.insets = new Insets(0, 7, 14, 0);
        contenedor.add(der, c);
    }

    
    //  LISTADO
  

    public void refrescar() {
        cargarTabla(txtBuscar.getText(), idActual);
    }

    public void enfocarBusqueda() {
        txtBuscar.requestFocusInWindow();
    }

    public void iniciarRegistro() {
        nuevo(true);
    }

    private void cargarTabla(String filtro, int idSeleccionar) {

        final int token = ++solicitudActual;
        final String texto = filtro == null ? "" : filtro.trim();

        new SwingWorker<List<Paciente>, Void>() {
            @Override
            protected List<Paciente> doInBackground() {
                return controlador.buscarPorTexto(texto);
            }

            @Override
            protected void done() {
                if (token != solicitudActual) {
                    return; // llegó una consulta más nueva
                }
                try {
                    llenarTabla(get(), texto, idSeleccionar);
                } catch (Exception e) {
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    llenarTabla(java.util.Collections.emptyList(), texto, 0);
                    aviso.mostrar(causa.getMessage(), Estilo.Nivel.ERROR);
                }
            }
        }.execute();
    }

    private void llenarTabla(List<Paciente> lista, String filtro, int idSeleccionar) {

        pacientes.clear();
        actualizandoTabla = true;
        tabla.clearSelection();

        
        java.util.List<? extends RowSorter.SortKey> orden = sorter.getSortKeys();
        tabla.setRowSorter(null);
        modelo.setRowCount(0);

        for (Paciente p : lista) {
            pacientes.put(p.getId(), p);
            modelo.addRow(new Object[]{
                p.getId(),
                nz(p.getCedula()),
                nombreCompleto(p),
                nz(p.getTelefono())
            });
        }

        tabla.setRowSorter(sorter);
        sorter.allRowsChanged();
        sorter.setSortKeys(orden);
        actualizandoTabla = false;

        lblConteo.setText(lista.size() == 1 ? "1 paciente"
                : lista.size() + " pacientes");

        if (lista.isEmpty()) {
            if (filtro.isEmpty()) {
                lblVacioTitulo.setText("Aún no hay pacientes registrados");
                lblVacioTexto.setText(
                        "Usa el formulario de la derecha para registrar el primero.");
            } else {
                lblVacioTitulo.setText("Sin resultados");
                lblVacioTexto.setText("No encontramos pacientes que coincidan con “"
                        + filtro + "”.");
            }
            cartasLista.show(contenedorLista, "vacio");
            return;
        }

        cartasLista.show(contenedorLista, "tabla");

        if (idSeleccionar > 0) {
            for (int i = 0; i < modelo.getRowCount(); i++) {
                if ((Integer) modelo.getValueAt(i, 0) == idSeleccionar) {
                    int vista = tabla.convertRowIndexToView(i);
                    tabla.setRowSelectionInterval(vista, vista);
                    tabla.scrollRectToVisible(tabla.getCellRect(vista, 0, true));
                    break;
                }
            }
        }
    }

 
    //  FORMULARIO
    
    private void nuevo(boolean enfocar) {

        idActual = 0;
        tabla.clearSelection();

        txtNombre1.setText("");
        txtNombre2.setText("");
        txtApellido1.setText("");
        txtApellido2.setText("");
        txtCedula.setText("");
        segSexo.setValor(null);
        txtFecha.setText("");
        txtEdad.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");

        limpiarErrores();
        actualizarModo();

        if (enfocar) {
            txtNombre1.requestFocusInWindow();
        }
    }

    private void cargarFormulario(Paciente p) {

        idActual = p.getId();

        txtNombre1.setText(nz(p.getNombre1()));
        txtNombre2.setText(nz(p.getNombre2()));
        txtApellido1.setText(nz(p.getApellido1()));
        txtApellido2.setText(nz(p.getApellido2()));
        txtCedula.setText(nz(p.getCedula()));
        segSexo.setValor(p.getSexo());
        txtFecha.setText(p.getFechaNacimiento() == null
                ? "" : p.getFechaNacimiento().toString());
        txtEdad.setText(p.getEdad() == null ? "" : String.valueOf(p.getEdad()));
        txtTelefono.setText(nz(p.getTelefono()));
        txtCorreo.setText(nz(p.getCorreo()));

        limpiarErrores();
        actualizarModo();
    }

    private void actualizarModo() {

        boolean editando = idActual > 0;

        lblModo.setText(editando ? "Editar paciente" : "Nuevo paciente");
        lblSubModo.setText(editando
                ? "Registro #" + idActual + " · modifica los datos y guarda"
                : "Completa los datos para registrar un paciente");
        btnGuardar.setText(editando ? "Guardar cambios" : "Registrar paciente");
        btnLimpiar.setText(editando ? "Cancelar" : "Limpiar");
        btnEliminar.setVisible(editando);

        if (panelBotonesDerecha != null) {
            panelBotonesDerecha.revalidate();
            panelBotonesDerecha.repaint();
        }
    }

    private void limpiarErrores() {
        for (Estilo.Campo c : new Estilo.Campo[]{txtNombre1, txtNombre2,
            txtApellido1, txtApellido2, txtCedula, txtFecha, txtEdad,
            txtTelefono, txtCorreo}) {
            c.marcarError(false);
        }
        segSexo.marcarError(false);
    }

    private void actualizarEdadDesdeFecha() {

        String texto = txtFecha.getText().trim();

        if (!texto.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            return;
        }

        try {
            Date fecha = Date.valueOf(texto);
            if (!fecha.toLocalDate().isAfter(LocalDate.now())) {
                txtEdad.setText(String.valueOf(CtrlPaciente.calcularEdad(fecha)));
            }
        } catch (IllegalArgumentException ignorar) {
            // fecha incompleta o inexistente: se valida al guardar
        }
    }

    
    //  ACCIONES
 
    private void guardar() {

        try {
            Paciente paciente = leerFormulario();
            boolean nuevo = idActual == 0;

            if (nuevo) {
                controlador.registrar(paciente);
                aviso.mostrar("Paciente registrado correctamente (ID "
                        + paciente.getId() + ").", Estilo.Nivel.EXITO);
                nuevo(false);
                cargarTabla(txtBuscar.getText(), 0);
            } else {
                controlador.actualizar(paciente);
                aviso.mostrar("Cambios guardados correctamente.",
                        Estilo.Nivel.EXITO);
                cargarTabla(txtBuscar.getText(), paciente.getId());
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            marcarCampoSegunMensaje(e.getMessage());
            aviso.mostrar(e.getMessage(), Estilo.Nivel.ERROR);
        }
    }

    private void eliminar() {

        if (idActual <= 0) {
            return;
        }

        Paciente p = pacientes.get(idActual);
        String quien = p == null ? "este paciente"
                : "<b>" + nombreCompleto(p) + "</b> (cédula " + nz(p.getCedula()) + ")";

        boolean confirmado = Estilo.confirmar(
                SwingUtilities.getWindowAncestor(this),
                "Eliminar paciente",
                "¿Seguro que deseas eliminar a " + quien
                + "? Esta acción no se puede deshacer.",
                "Eliminar", true);

        if (!confirmado) {
            return;
        }

        try {
            controlador.eliminar(idActual);
            aviso.mostrar("Paciente eliminado correctamente.", Estilo.Nivel.EXITO);
            nuevo(false);
            cargarTabla(txtBuscar.getText(), 0);

        } catch (IllegalArgumentException | IllegalStateException e) {
            aviso.mostrar(e.getMessage(), Estilo.Nivel.ERROR);
        }
    }

    private Paciente leerFormulario() {

        boolean falta = false;
        falta |= marcarSiVacio(txtNombre1);
        falta |= marcarSiVacio(txtApellido1);
        falta |= marcarSiVacio(txtCedula);

        if (segSexo.getValor() == null) {
            segSexo.marcarError(true);
            falta = true;
        }

        if (falta) {
            throw new IllegalArgumentException(
                    "Completa los campos obligatorios marcados con *.");
        }

        Paciente p = new Paciente();
        p.setId(idActual);
        p.setNombre1(txtNombre1.getText());
        p.setNombre2(txtNombre2.getText());
        p.setApellido1(txtApellido1.getText());
        p.setApellido2(txtApellido2.getText());
        p.setCedula(txtCedula.getText());
        p.setSexo(segSexo.getValor());
        p.setFechaNacimiento(leerFecha());
        p.setEdad(leerEdad());
        p.setTelefono(txtTelefono.getText());
        p.setCorreo(txtCorreo.getText());
        return p;
    }

    private boolean marcarSiVacio(Estilo.Campo campo) {
        boolean vacio = campo.getText().trim().isEmpty();
        campo.marcarError(vacio);
        return vacio;
    }

    private Date leerFecha() {

        String texto = txtFecha.getText().trim();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            return Date.valueOf(texto);
        } catch (IllegalArgumentException e) {
            txtFecha.marcarError(true);
            throw new IllegalArgumentException(
                    "La fecha de nacimiento debe tener el formato AAAA-MM-DD (por ejemplo 1995-08-24).");
        }
    }

    private Integer leerEdad() {

        String texto = txtEdad.getText().trim();

        if (texto.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(texto);
        } catch (NumberFormatException e) {
            txtEdad.marcarError(true);
            throw new IllegalArgumentException("La edad debe ser un número válido.");
        }
    }

    /** Resalta el campo al que se refiere el mensaje del controlador. */
    private void marcarCampoSegunMensaje(String mensaje) {

        if (mensaje == null) {
            return;
        }

        String m = mensaje.toLowerCase();

        if (m.contains("cédula")) {
            txtCedula.marcarError(true);
        }
        if (m.contains("correo")) {
            txtCorreo.marcarError(true);
        }
        if (m.contains("teléfono")) {
            txtTelefono.marcarError(true);
        }
        if (m.contains("fecha")) {
            txtFecha.marcarError(true);
        }
        if (m.contains("edad")) {
            txtEdad.marcarError(true);
        }
    }

   
    //  UTILIDADES
    
    private String nz(String valor) {
        return valor == null ? "" : valor;
    }

    private String nombreCompleto(Paciente p) {
        StringBuilder sb = new StringBuilder();
        for (String parte : new String[]{p.getNombre1(), p.getNombre2(),
            p.getApellido1(), p.getApellido2()}) {
            if (parte != null && !parte.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(parte);
            }
        }
        return sb.toString();
    }
}
