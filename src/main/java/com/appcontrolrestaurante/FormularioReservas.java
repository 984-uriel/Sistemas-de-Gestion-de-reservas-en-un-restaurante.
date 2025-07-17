package com.appcontrolrestaurante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormularioReservas extends JPanel implements ActionListener {
    private static final String DELETE_COMMAND = "Eliminar";
    private static final String SEARCH_COMMAND = "Buscar";
    private static final String CLEAR_COMMAND = "Limpiar";

    private final JTextField tfIdReserva;
    private final JButton btnEliminar, btnBuscar, btnLimpiar;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JTextArea historial;
    private final Connection conexionSQL;

    public FormularioReservas(Connection conexionSQL) {
        this.conexionSQL = conexionSQL;
        setLayout(new BorderLayout());

        // Campos
        tfIdReserva = new JTextField(10);

        // Botones
        btnEliminar = new JButton(DELETE_COMMAND);
        btnBuscar = new JButton(SEARCH_COMMAND);
        btnLimpiar = new JButton(CLEAR_COMMAND);

        btnEliminar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // Panel de entrada
        JPanel panelEntrada = new JPanel(new GridLayout(1, 2));
        panelEntrada.add(new JLabel("ID Reserva:"));
        panelEntrada.add(tfIdReserva);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 5, 5));
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);

        // Historial
        historial = new JTextArea(5, 30);
        historial.setEditable(false);

        // Tabla
        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Restaurante", "Fecha", "Hora", "Mesa"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(500, 450));

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int fila = tabla.getSelectedRow();
                tfIdReserva.setText(tabla.getValueAt(fila, 0).toString());
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    buscar();
                }
            }
        });

        // Estructura
        add(panelEntrada, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollTabla, BorderLayout.SOUTH);
        add(new JScrollPane(historial), BorderLayout.EAST);

        consultarReservas();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case DELETE_COMMAND -> eliminar();
            case SEARCH_COMMAND -> buscar();
            case CLEAR_COMMAND -> limpiarCampos();
        }
    }

    private void eliminar() {
        if (tfIdReserva.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(tfIdReserva.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar la reserva con ID: " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacion != JOptionPane.YES_OPTION) {
            historial.append("Eliminación cancelada para ID: " + id + "\n");
            return;
        }

        String sql = "DELETE FROM Reserva WHERE id_reserva=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                historial.append("Reserva con ID " + id + " eliminada.\n");
                actualizarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la reserva con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            historial.append("Error al eliminar: " + ex.getMessage() + "\n");
        }
    }

    private void buscar() {
        if (tfIdReserva.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(tfIdReserva.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = """
                SELECT r.id_reserva, c.nombre AS cliente, res.nombre AS restaurante,
                       r.fecha, r.hora, m.numero AS mesa
                FROM Reserva r
                JOIN Cliente c ON r.id_cliente = c.id_cliente
                JOIN Restaurante res ON r.id_restaurante = res.id_restaurante
                JOIN Mesa m ON r.id_mesa = m.id_mesa
                WHERE r.id_reserva = ?
            """;
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            modeloTabla.setRowCount(0);
            if (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id_reserva"),
                        rs.getString("cliente"),
                        rs.getString("restaurante"),
                        rs.getDate("fecha"),
                        rs.getString("hora"),
                        rs.getInt("mesa")
                });
                historial.append("Reserva encontrada ID: " + id + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Reserva no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
                historial.append("No se encontró reserva con ID: " + id + "\n");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            historial.append("Error al buscar: " + ex.getMessage() + "\n");
        }
    }

    private void consultarReservas() {
        modeloTabla.setRowCount(0);
        String sql = """
                SELECT r.id_reserva, c.nombre, res.nombre, r.fecha, r.hora, m.numero
                FROM Reserva r
                JOIN Cliente c ON r.id_cliente = c.id_cliente
                JOIN Restaurante res ON r.id_restaurante = res.id_restaurante
                JOIN Mesa m ON r.id_mesa = m.id_mesa
            """;

        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDate(4),
                        rs.getString(5),
                        rs.getInt(6)
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            historial.append("Error al consultar: " + ex.getMessage() + "\n");
        }
    }

    private void actualizarTabla() {
        consultarReservas();
    }

    private void limpiarCampos() {
        tfIdReserva.setText("");
    }
}

