package com.appcontrolrestaurante;
//Equipo reservas
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class FormularioCliente extends JPanel implements ActionListener {
    private static final String INSERT_COMMAND = "Insertar";
    private static final String UPDATE_COMMAND = "Actualizar";
    private static final String DELETE_COMMAND = "Borrar";
    private static final String SEARCH_COMMAND = "Buscar ID";
    private static final String CLEAR_COMMAND = "Limpiar";

    private final JTextField tfId, tfNombre, tfCorreo, tfTelefono, tfBuscarId;
    private final JButton btnInsertar, btnModificar, btnEliminar, btnBuscar, btnLimpiar;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JTextArea historial;
    private final Connection conexionSQL;

    public FormularioCliente(Connection conexionSQL) {
        this.conexionSQL = conexionSQL;
        setLayout(new BorderLayout());

        // Campos de texto
        tfId = new JTextField(10);
        tfNombre = new JTextField(20);
        tfCorreo = new JTextField(20);
        tfTelefono = new JTextField(15);
        tfBuscarId = new JTextField(10);

        // Botones
        btnInsertar = new JButton(INSERT_COMMAND);
        btnModificar = new JButton(UPDATE_COMMAND);
        btnEliminar = new JButton(DELETE_COMMAND);
        btnBuscar = new JButton(SEARCH_COMMAND);
        btnLimpiar = new JButton(CLEAR_COMMAND);

        btnInsertar.addActionListener(this);
        btnModificar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // Panel de entrada
        JPanel panelEntrada = new JPanel(new GridLayout(4, 2));
        
        panelEntrada.add(new JLabel("Nombre:"));
        panelEntrada.add(tfNombre);
        panelEntrada.add(new JLabel("Correo:"));
        panelEntrada.add(tfCorreo);
        panelEntrada.add(new JLabel("Teléfono:"));
        panelEntrada.add(tfTelefono);
        panelEntrada.add(new JLabel("ID Cliente:"));
        panelEntrada.add(tfBuscarId);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 5, 5, 5));
        panelBotones.add(btnInsertar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);

        // Historial
        historial = new JTextArea(5, 30);
        historial.setEditable(false);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(500, 450));
        
        tabla.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
            int fila = tabla.getSelectedRow();
            tfBuscarId.setText(tabla.getValueAt(fila, 0).toString());
            tfNombre.setText(tabla.getValueAt(fila, 1).toString());
            tfCorreo.setText(tabla.getValueAt(fila, 2).toString());
            tfTelefono.setText(tabla.getValueAt(fila, 3).toString());
        }
    });


        // Agregando todo
        add(panelEntrada, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollTabla, BorderLayout.SOUTH);
        add(new JScrollPane(historial), BorderLayout.EAST);

        // Evento doble clic en la tabla para cargar datos
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int filaSeleccionada = tabla.getSelectedRow();
                    if (filaSeleccionada >= 0) {
                        tfBuscarId.setText(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
                        buscar();
                    }
                }
            }
        });

        consultarClientes();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case INSERT_COMMAND -> insertar();
            case UPDATE_COMMAND -> modificar();
            case DELETE_COMMAND -> eliminar();
            case SEARCH_COMMAND -> buscar();
            case CLEAR_COMMAND -> limpiarCampos();
        }
    }

    private void insertar() {
        if (tfNombre.getText().isEmpty() || tfCorreo.getText().isEmpty() || tfTelefono.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "INSERT INTO Cliente(nombre, correo, telefono) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfCorreo.getText());
            stmt.setString(3, tfTelefono.getText());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                modeloTabla.addRow(new Object[]{id, tfNombre.getText(), tfCorreo.getText(), tfTelefono.getText()});
                historial.append("Cliente agregado con ID: " + id + "\n");
            }
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al insertar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            historial.append("Error al insertar: " + ex.getMessage() + "\n");
        }
    }

    private void modificar() {
        if (tfBuscarId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(tfBuscarId.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "UPDATE Cliente SET nombre=?, correo=?, telefono=? WHERE id_cliente=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfCorreo.getText());
            stmt.setString(3, tfTelefono.getText());
            stmt.setInt(4, id);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                actualizarTabla();
                historial.append("Cliente con ID " + id + " modificado\n");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el cliente con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            historial.append("Error al modificar: " + ex.getMessage() + "\n");
        }
    }
    
    private void eliminar() {
    if (tfBuscarId.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "El ID no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    int id;
    try {
        id = Integer.parseInt(tfBuscarId.getText());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "El ID debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "Este cliente puede tener reservas activas. ¿Deseas eliminarlo junto con sus reservas?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
    );
    if (confirmacion != JOptionPane.YES_OPTION) {
        historial.append("Eliminación cancelada para el ID: " + id + "\n");
        return;
    }

    try {
        conexionSQL.setAutoCommit(false); // Comienza transacción

        // 1. Eliminar reservas del cliente
        try (PreparedStatement eliminarReservas = conexionSQL.prepareStatement("DELETE FROM Reserva WHERE id_cliente=?")) {
            eliminarReservas.setInt(1, id);
            eliminarReservas.executeUpdate();
        }

        // 2. Eliminar cliente
        try (PreparedStatement eliminarCliente = conexionSQL.prepareStatement("DELETE FROM Cliente WHERE id_cliente=?")) {
            eliminarCliente.setInt(1, id);
            int filasAfectadas = eliminarCliente.executeUpdate();

            if (filasAfectadas > 0) {
                conexionSQL.commit();
                actualizarTabla();
                historial.append("Cliente con ID " + id + " y sus reservas fueron eliminados correctamente\n");
                limpiarCampos();
            } else {
                conexionSQL.rollback();
                JOptionPane.showMessageDialog(this, "No se encontró el cliente con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (SQLException ex) {
        try {
            conexionSQL.rollback();
        } catch (SQLException rollbackEx) {
            historial.append("Error al hacer rollback: " + rollbackEx.getMessage() + "\n");
        }
        JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        historial.append("Error al eliminar: " + ex.getMessage() + "\n");
    } finally {
        try {
            conexionSQL.setAutoCommit(true);
        } catch (SQLException ex) {
            historial.append("Error al restaurar autoCommit: " + ex.getMessage() + "\n");
        }
    }
}



    private void buscar() {
        if (tfBuscarId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id;
        try {
            id = Integer.parseInt(tfBuscarId.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "SELECT * FROM Cliente WHERE id_cliente=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tfNombre.setText(rs.getString("nombre"));
                tfCorreo.setText(rs.getString("correo"));
                tfTelefono.setText(rs.getString("telefono"));
                historial.append("Cliente encontrado: " + rs.getString("nombre") + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el cliente con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                limpiarCampos();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            historial.append("Error al buscar: " + ex.getMessage() + "\n");
        }
    }
    

    private void consultarClientes() {
        modeloTabla.setRowCount(0); // Limpiar tabla antes de cargar nuevos datos
        String sql = "SELECT * FROM Cliente";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("telefono")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar clientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            historial.append("Error al consultar clientes: " + ex.getMessage() + "\n");
        }
    }

    private void actualizarTabla() {
        consultarClientes();
    }

    private void limpiarCampos() {
        tfBuscarId.setText("");
        tfNombre.setText("");
        tfCorreo.setText("");
        tfTelefono.setText("");
    }
}