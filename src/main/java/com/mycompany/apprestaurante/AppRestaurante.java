package com.mycompany.apprestaurante;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AppRestaurante extends JFrame {
    private JComboBox<String> comboClientes;
    private JComboBox<String> comboMesas;
    private JComboBox<String> comboEmpleados;
    private JComboBox<String> comboMedios;
    private JTextField txtFecha, txtHora;
    private JButton btnGuardar;

    private Connection conn;

    public AppRestaurante() {
        setTitle("Sistema de Reservas");
        setSize(400, 400);
        setLayout(new GridLayout(7, 2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        comboClientes = new JComboBox<>();
        comboMesas = new JComboBox<>();
        comboEmpleados = new JComboBox<>();
        comboMedios = new JComboBox<>();
        txtFecha = new JTextField();
        txtHora = new JTextField();
        btnGuardar = new JButton("Guardar Reserva");

        add(new JLabel("Cliente:")); add(comboClientes);
        add(new JLabel("Mesa:")); add(comboMesas);
        add(new JLabel("Empleado:")); add(comboEmpleados);
        add(new JLabel("Medio Reserva:")); add(comboMedios);
        add(new JLabel("Fecha (YYYY-MM-DD):")); add(txtFecha);
        add(new JLabel("Hora (HH:MM:SS):")); add(txtHora);
        add(new JLabel("")); add(btnGuardar);

        conectarBD();
        cargarDatos();

        btnGuardar.addActionListener(e -> guardarReserva());

        setVisible(true);
    }

    private void conectarBD() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ControlRestaurante", "root", "");
            System.out.println("Conexión exitosa.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos: " + e.getMessage());
            System.exit(1);
        }
    }

    private void cargarDatos() {
        cargarCombo(comboClientes, "SELECT id_cliente, nombre FROM Cliente");
        cargarCombo(comboMesas, "SELECT id_mesa, CONCAT('Mesa ', numero_mesa, ' (', capacidad, ' personas)') FROM Mesa");
        cargarCombo(comboEmpleados, "SELECT id_empleado, nombre FROM Empleado");
        cargarCombo(comboMedios, "SELECT id_medio, descripcion FROM Medio_Reserva");
    }

    private void cargarCombo(JComboBox<String> combo, String query) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                combo.addItem(rs.getInt(1) + ": " + rs.getString(2));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void guardarReserva() {
        try {
            int idCliente = Integer.parseInt(comboClientes.getSelectedItem().toString().split(":")[0].trim());
            int idMesa = Integer.parseInt(comboMesas.getSelectedItem().toString().split(":")[0].trim());
            int idEmpleado = Integer.parseInt(comboEmpleados.getSelectedItem().toString().split(":")[0].trim());
            int idMedio = Integer.parseInt(comboMedios.getSelectedItem().toString().split(":")[0].trim());
            String fecha = txtFecha.getText();
            String hora = txtHora.getText();

            String sql = "INSERT INTO Reserva (id_cliente, id_mesa, id_empleado, id_medio, fecha_reserva, hora_reserva) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idMesa);
            pstmt.setInt(3, idEmpleado);
            pstmt.setInt(4, idMedio);
            pstmt.setString(5, fecha);
            pstmt.setString(6, hora);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Reserva guardada exitosamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la reserva: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppRestaurante());
    }
}
