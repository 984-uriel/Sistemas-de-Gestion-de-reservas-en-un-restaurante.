package com.appcontrolrestaurante;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class ReservacionForm extends JFrame {
    
    private final Connection conexionSQL;
    // Componentes de la interfaz
    private JComboBox<String> cbRestaurante;
    private JComboBox<String> cbMedioReserva;
    private JTextField tfNombreCliente;
    private JTextField tfTelefono;
    private JTextField tfCorreo;
    private JSpinner spFechaReserva;
    private JSpinner spHoraReserva;
    private JComboBox<Integer> cbNumPersonas;
    private JButton btnBuscarMesas;
    private JButton btnConfirmarReserva;
    private JTable tblMesasDisponibles;
    private DefaultTableModel modeloTabla;
    private JTextArea taHistorial;
    
    // Configuración de colores
    private final Color COLOR_PRIMARIO = new Color(30, 58, 138);
    private final Color COLOR_SECUNDARIO = new Color(59, 130, 246);
    private final Color COLOR_FONDO = new Color(248, 249, 250);
    private final Color COLOR_BORDE = new Color(222, 226, 230);

    public ReservacionForm(Connection conexionSQL) {
        this.conexionSQL = conexionSQL;
        configurarInterfaz();
        initComponents();
        cargarDatosIniciales();
    }

    private void configurarInterfaz() {
        setTitle("Sistema de Reservas - Restaurante");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(COLOR_FONDO);

        // Encabezado
        JLabel titulo = new JLabel("SISTEMA DE RESERVAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(COLOR_PRIMARIO);
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // Formulario principal
        JPanel panelFormulario = new JPanel(new BorderLayout(15, 15));
        
        // Panel superior con datos del cliente y reservación
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2, 20, 10));
        panelSuperior.add(crearPanelCliente());
        panelSuperior.add(crearPanelReservacion());
        panelFormulario.add(panelSuperior, BorderLayout.NORTH);

        // Panel de mesas
        JPanel panelMesas = crearPanelMesas();
        panelFormulario.add(panelMesas, BorderLayout.CENTER);

        // Panel de historial
        panelFormulario.add(crearPanelHistorial(), BorderLayout.SOUTH);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        add(panelPrincipal);
    }

    private JPanel crearPanelCliente() {
        JPanel panel = crearPanelConEstilo("Datos del Cliente");
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        
        tfNombreCliente = new JTextField();
        tfTelefono = new JTextField();
        tfCorreo = new JTextField();
        cbMedioReserva = new JComboBox<>();
        
        agregarCampo(panel, "Nombre Completo:", tfNombreCliente);
        agregarCampo(panel, "Teléfono:", tfTelefono);
        agregarCampo(panel, "Correo Electrónico:", tfCorreo);
        agregarCampo(panel, "Medio de Reserva:", cbMedioReserva);
        
        return panel;
    }

    private JPanel crearPanelReservacion() {
        JPanel panel = crearPanelConEstilo("Detalles de Reservación");
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        
        cbRestaurante = new JComboBox<>();
        spFechaReserva = new JSpinner(new SpinnerDateModel());
        spHoraReserva = new JSpinner(new SpinnerDateModel());
        cbNumPersonas = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        
        spFechaReserva.setEditor(new JSpinner.DateEditor(spFechaReserva, "dd/MM/yyyy"));
        spHoraReserva.setEditor(new JSpinner.DateEditor(spHoraReserva, "HH:mm"));
        
        agregarCampo(panel, "Restaurante:", cbRestaurante);
        agregarCampo(panel, "Fecha:", spFechaReserva);
        agregarCampo(panel, "Hora:", spHoraReserva);
        agregarCampo(panel, "Número de Personas:", cbNumPersonas);
        
        return panel;
    }

    private JPanel crearPanelMesas() {
        JPanel panel = crearPanelConEstilo("Mesas Disponibles");
        panel.setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(new String[]{"ID Mesa", "Número", "Capacidad", "Restaurante"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblMesasDisponibles = new JTable(modeloTabla);
        tblMesasDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMesasDisponibles.getSelectionModel().addListSelectionListener(e -> {
            btnConfirmarReserva.setEnabled(tblMesasDisponibles.getSelectedRow() != -1);
        });
        
        JScrollPane scroll = new JScrollPane(tblMesasDisponibles);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        btnBuscarMesas = crearBoton("Buscar Mesas Disponibles", COLOR_PRIMARIO);
        btnBuscarMesas.addActionListener(this::buscarMesasDisponibles);
        
        btnConfirmarReserva = crearBoton("Confirmar Reservación", COLOR_SECUNDARIO);
        btnConfirmarReserva.addActionListener(this::confirmarReservacion);
        btnConfirmarReserva.setEnabled(false);
        
        panelBotones.add(btnBuscarMesas);
        panelBotones.add(btnConfirmarReserva);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = crearPanelConEstilo("Historial de Operaciones");
        
        taHistorial = new JTextArea();
        taHistorial.setEditable(false);
        taHistorial.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(taHistorial);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        panel.add(scroll);
        
        return panel;
    }

    private JPanel crearPanelConEstilo(String titulo) {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            COLOR_PRIMARIO
        ));
        return panel;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl);
        
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (campo instanceof JTextField) {
            ((JTextField)campo).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
        panel.add(campo);
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return boton;
    }

    private void cargarDatosIniciales() {
        cargarRestaurantes();
        cargarMediosReserva();
        spFechaReserva.setValue(new Date());
        spHoraReserva.setValue(new Date());
    }

    private void cargarRestaurantes() {
        try (Statement stmt = conexionSQL.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id_restaurante, nombre FROM Restaurante")) {
            cbRestaurante.removeAllItems();
            while (rs.next()) {
                cbRestaurante.addItem(rs.getString("nombre"));
            }
            appendToHistorial("Restaurantes cargados correctamente.");
        } catch (SQLException ex) {
            mostrarError("Error al cargar restaurantes: " + ex.getMessage());
        }
    }

    private void cargarMediosReserva() {
        try (Statement stmt = conexionSQL.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT descripcion FROM Medio_Reserva")) {
            cbMedioReserva.removeAllItems();
            while (rs.next()) {
                cbMedioReserva.addItem(rs.getString("descripcion"));
            }
            appendToHistorial("Medios de reserva cargados correctamente.");
        } catch (SQLException ex) {
            mostrarError("Error al cargar medios de reserva: " + ex.getMessage());
        }
    }

    private void buscarMesasDisponibles(ActionEvent evt) {
        if (!validarDatosCliente()) return;

        Integer numPersonas = (Integer) cbNumPersonas.getSelectedItem();
        if (numPersonas == null) {
            mostrarAdvertencia("Seleccione el número de personas");
            return;
        }

        String nombreRestaurante = (String) cbRestaurante.getSelectedItem();
        Date fechaReserva = (Date) spFechaReserva.getValue();
        Date horaReserva = (Date) spHoraReserva.getValue();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        modeloTabla.setRowCount(0);

        try {
            String sql = "SELECT m.id_mesa, m.numero_mesa, m.capacidad, r.nombre " +
                       "FROM Mesa m JOIN Restaurante r ON m.id_restaurante = r.id_restaurante " +
                       "WHERE m.capacidad >= ? AND r.nombre = ? " +
                       "AND m.id_mesa NOT IN (" +
                       "SELECT id_mesa FROM Reserva WHERE fecha_reserva = ? AND hora_reserva = ?)";

            PreparedStatement stmt = conexionSQL.prepareStatement(sql);
            stmt.setInt(1, numPersonas);
            stmt.setString(2, nombreRestaurante);
            stmt.setString(3, dateFormat.format(fechaReserva));
            stmt.setString(4, timeFormat.format(horaReserva));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_mesa"),
                    rs.getInt("numero_mesa"),
                    rs.getInt("capacidad"),
                    rs.getString("nombre")
                });
            }

            if (modeloTabla.getRowCount() > 0) {
                appendToHistorial("Mesas encontradas: " + modeloTabla.getRowCount());
            } else {
                mostrarAdvertencia("No hay mesas disponibles para estos criterios");
            }
        } catch (SQLException ex) {
            mostrarError("Error al buscar mesas: " + ex.getMessage());
        }
    }

    private void confirmarReservacion(ActionEvent evt) {
        int selectedRow = tblMesasDisponibles.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Seleccione una mesa para confirmar");
            return;
        }

        if (!validarDatosCliente()) return;

        int idMesa = (Integer) modeloTabla.getValueAt(selectedRow, 0);
        String nombreCliente = tfNombreCliente.getText().trim();
        String telefono = tfTelefono.getText().trim();
        String correo = tfCorreo.getText().trim();
        String medioReserva = cbMedioReserva.getSelectedItem().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String fecha = dateFormat.format(spFechaReserva.getValue());
        String hora = timeFormat.format(spHoraReserva.getValue());

        try {
            conexionSQL.setAutoCommit(false);
            
            // Registrar cliente (si no existe)
            int idCliente = obtenerIdCliente(telefono);
            if (idCliente == -1) {
                idCliente = registrarCliente(nombreCliente, telefono, correo);
            }

            // Registrar reserva
            String sqlReserva = "INSERT INTO Reserva (id_cliente, id_mesa, fecha_reserva, hora_reserva, id_medio) " +
                              "VALUES (?, ?, ?, ?, (SELECT id_medio FROM Medio_Reserva WHERE descripcion = ?))";
            
            PreparedStatement stmt = conexionSQL.prepareStatement(sqlReserva);
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idMesa);
            stmt.setString(3, fecha);
            stmt.setString(4, hora);
            stmt.setString(5, medioReserva);
            
            stmt.executeUpdate();
            conexionSQL.commit();
            
            mostrarMensaje("Reserva confirmada exitosamente");
            limpiarFormulario();
            
        } catch (SQLException ex) {
            try {
                conexionSQL.rollback();
                mostrarError("Error al confirmar reserva: " + ex.getMessage());
            } catch (SQLException e) {
                mostrarError("Error crítico: " + e.getMessage());
            }
        }
    }

    private int obtenerIdCliente(String telefono) throws SQLException {
        String sql = "SELECT id_cliente FROM Cliente WHERE telefono = ?";
        PreparedStatement stmt = conexionSQL.prepareStatement(sql);
        stmt.setString(1, telefono);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt("id_cliente") : -1;
    }

    private int registrarCliente(String nombre, String telefono, String correo) throws SQLException {
        String sql = "INSERT INTO Cliente (nombre, telefono, correo) VALUES (?, ?, ?)";
        PreparedStatement stmt = conexionSQL.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, nombre);
        stmt.setString(2, telefono);
        stmt.setString(3, correo);
        stmt.executeUpdate();
        
        ResultSet rs = stmt.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : -1;
    }

    private boolean validarDatosCliente() {
        if (tfNombreCliente.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingrese el nombre del cliente");
            return false;
        }
        if (tfTelefono.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingrese el teléfono del cliente");
            return false;
        }
        if (cbRestaurante.getSelectedIndex() == -1) {
            mostrarAdvertencia("Seleccione un restaurante");
            return false;
        }
        if (cbMedioReserva.getSelectedIndex() == -1) {
            mostrarAdvertencia("Seleccione un medio de reserva");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        tfNombreCliente.setText("");
        tfTelefono.setText("");
        tfCorreo.setText("");
        spFechaReserva.setValue(new Date());
        spHoraReserva.setValue(new Date());
        cbNumPersonas.setSelectedIndex(0);
        modeloTabla.setRowCount(0);
        btnConfirmarReserva.setEnabled(false);
    }

    private void appendToHistorial(String mensaje) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        taHistorial.append("[" + sdf.format(new Date()) + "] " + mensaje + "\n");
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        appendToHistorial(mensaje);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
        appendToHistorial(mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ControlRestaurante",
                    "root",
                    ""
                );
                new ReservacionForm(conexion).setVisible(true);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                    "Error al conectar con la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

