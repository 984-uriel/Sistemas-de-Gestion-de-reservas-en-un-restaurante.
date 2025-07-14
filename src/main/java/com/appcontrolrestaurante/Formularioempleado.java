package com.appcontrolrestaurante;
//Equipo reservas

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Formularioempleado extends JPanel implements ActionListener {

    private static final String INSERT_COMMAND = "Insertar";
    private static final String UPDATE_COMMAND = "Actualizar";
    private static final String DELETE_COMMAND = "Eliminar";
    private static final String SEARCH_COMMAND = "Buscar ID";
    private static final String CLEAR_COMMAND = "Limpiar";

    private final JTextField tfNombre, tfpuesto, tfBuscarId;
    private final JButton btnInsertar, btnModificar, btnEliminar, btnBuscar, btnLimpiar;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JTextArea historial;
    private final Connection conexionSQL;

    public Formularioempleado(Connection conexionSQL) {
        this.conexionSQL = conexionSQL;
        setLayout(new BorderLayout());

  
        tfNombre = new JTextField();
        tfpuesto = new JTextField();
        tfBuscarId = new JTextField();

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

        JPanel panelEntrada = new JPanel(new GridLayout(6, 2));
        panelEntrada.add(new JLabel("Nombre:"));       panelEntrada.add(tfNombre);
        panelEntrada.add(new JLabel("Puesto:"));  panelEntrada.add(tfpuesto);
        panelEntrada.add(new JLabel("Buscar ID:"));    panelEntrada.add(tfBuscarId);

        JPanel panelBotones = new JPanel(new GridLayout(1, 5));
        panelBotones.add(btnInsertar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);

        historial = new JTextArea(4, 20);
        historial.setEditable(false);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Puesto"}, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        
        add(panelEntrada, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollTabla, BorderLayout.SOUTH);
        add(new JScrollPane(historial), BorderLayout.EAST);
       
        consultarempleado();
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
        String sql = "INSERT INTO empleado(nombre, puesto) VALUES (?, ?)";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfpuesto.getText());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                modeloTabla.addRow(new Object[]{id, tfNombre.getText(), tfpuesto.getText()});
                historial.append("Empleado insertado con ID: " + id + "\n");
            }

            limpiarCampos();
            actualizarTabla();
        } catch (SQLException ex) {
            historial.append("Error al insertar empleado: " + ex.getMessage() + "\n");
        }
    }

    private void modificar() {
        if (tfBuscarId.getText().isEmpty()) {
            historial.append("Error: ID no puede estar vacío.\n");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(tfBuscarId.getText());
        } catch (NumberFormatException ex) {
            historial.append("Error: ID debe ser un número.\n");
            return;
        }

        String sql = "UPDATE empleado SET nombre=?, puesto=? WHERE id_empleado=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfpuesto.getText());
            stmt.setInt(3, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                historial.append("Empleado ID " + id + " modificado.\n");
            } else {
                historial.append("No se encontró empleado con ID: " + id + "\n");
            }
            actualizarTabla();
        } catch (SQLException ex) {
            historial.append("Error al modificar empleado: " + ex.getMessage() + "\n");
        }
    }

    private void eliminar() {
        if (tfBuscarId.getText().isEmpty()) {
            historial.append("Error: ID no puede estar vacío.\n");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(tfBuscarId.getText());
        } catch (NumberFormatException ex) {
            historial.append("Error: ID debe ser un número.\n");
            return;
        }

        String sql = "DELETE FROM empleado WHERE id_empleado=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            historial.append("Empleado con ID " + id + " eliminado.\n");
            actualizarTabla();
            limpiarCampos();
        } catch (SQLException ex) {
            historial.append("Error al eliminar empleado: " + ex.getMessage() + "\n");
        }
    }

    private void buscar() {
        if (tfBuscarId.getText().isEmpty()) {
            historial.append("Error: ID no puede estar vacío.\n");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(tfBuscarId.getText());
        } catch (NumberFormatException ex) {
            historial.append("Error: ID debe ser un número.\n");
            return;
        }

        String sql = "SELECT * FROM empleado WHERE id_empleado=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tfNombre.setText(rs.getString("nombre"));
                tfpuesto.setText(rs.getString("puesto"));
                historial.append("Empleado encontrado: " + rs.getString("nombre") + "\n");
            } else {
                historial.append("Empleado no encontrado con ID: " + id + "\n");
            }
        } catch (SQLException ex) {
            historial.append("Error al buscar empleado: " + ex.getMessage() + "\n");
        }
    }

    private void consultarempleado() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT * FROM empleado";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_empleado"),
                    rs.getString("nombre"),
                    rs.getString("puesto")
                });
            }
        } catch (SQLException ex) {
            historial.append("Error al encontrar personal: " + ex.getMessage() + "\n");
        }
    }

    private void actualizarTabla() {
        consultarempleado();
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfpuesto.setText("");
        tfBuscarId.setText("");
    }
}
