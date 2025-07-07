package com.appcontrolrestaurante;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

        

public class FormularioCliente extends JPanel implements ActionListener {

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
        tfId = new JTextField();
        tfNombre = new JTextField();
        tfCorreo = new JTextField();
        tfTelefono = new JTextField();
        tfBuscarId = new JTextField();

        // Botones
        btnInsertar = new JButton("INSERT");
        btnModificar = new JButton("UPDATE");
        btnEliminar = new JButton("DELETE");
        btnBuscar = new JButton("Buscar ID");
        btnLimpiar = new JButton("Limpiar");

        btnInsertar.addActionListener(this);
        btnModificar.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnBuscar.addActionListener(this);
        btnLimpiar.addActionListener(this);

        // Panel de entrada
        JPanel panelEntrada = new JPanel(new GridLayout(3, 2));
        panelEntrada.add(new JLabel("Nombre:"));
        panelEntrada.add(tfNombre);
        panelEntrada.add(new JLabel("Correo:"));
        panelEntrada.add(tfCorreo);
        panelEntrada.add(new JLabel("Teléfono:"));
        panelEntrada.add(tfTelefono);

        // Panel de búsqueda
        JPanel panelBuscar = new JPanel(new BorderLayout());
        panelBuscar.add(new JLabel("ID Cliente:"), BorderLayout.WEST);
        panelBuscar.add(tfBuscarId, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 5));
        panelBotones.add(btnInsertar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);

        // Historial
        historial = new JTextArea(4, 20);
        historial.setEditable(false);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Teléfono"}, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);

        // Panel Norte
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelEntrada, BorderLayout.CENTER);
        panelNorte.add(panelBuscar, BorderLayout.SOUTH);

        // Agregando todo
        add(panelNorte, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollTabla, BorderLayout.SOUTH);
        add(new JScrollPane(historial), BorderLayout.EAST);

        consultarClientes();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "INSERT" -> insertar();
            case "UPDATE" -> modificar();
            case "DELETE" -> eliminar();
            case "Buscar ID" -> buscar();
            case "Limpiar" -> limpiarCampos();
        }
    }

    private void insertar() {
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
            historial.append("Error al insertar: " + ex + "\n");
        }
    }

    private void modificar() {
        if (tfBuscarId.getText().isEmpty()) return;
        int id = Integer.parseInt(tfBuscarId.getText());
        String sql = "UPDATE Cliente SET nombre=?, correo=?, telefono=? WHERE id_cliente=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfCorreo.getText());
            stmt.setString(3, tfTelefono.getText());
            stmt.setInt(4, id);
            stmt.executeUpdate();
            actualizarTabla();
            historial.append("Cliente con ID " + id + " modificado\n");
        } catch (SQLException ex) {
            historial.append("Error al modificar: " + ex + "\n");
        }
    }

    private void eliminar() {
        if (tfBuscarId.getText().isEmpty()) return;
        int id = Integer.parseInt(tfBuscarId.getText());
        String sql = "DELETE FROM Cliente WHERE id_cliente=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            actualizarTabla();
            historial.append("Cliente con ID " + id + " eliminado\n");
            limpiarCampos();
        } catch (SQLException ex) {
            historial.append("Error al eliminar: " + ex + "\n");
        }
    }

    private void buscar() {
        if (tfBuscarId.getText().isEmpty()) return;
        int id = Integer.parseInt(tfBuscarId.getText());
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
                historial.append("No se encontró el cliente con ID: " + id + "\n");
            }
        } catch (SQLException ex) {
            historial.append("Error al buscar: " + ex + "\n");
        }
    }

    private void consultarClientes() {
        modeloTabla.setRowCount(0);
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
            historial.append("Error al consultar clientes: " + ex + "\n");
        }
    }

    private void actualizarTabla() {
        consultarClientes();
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfCorreo.setText("");
        tfTelefono.setText("");
        tfBuscarId.setText("");
    }
}