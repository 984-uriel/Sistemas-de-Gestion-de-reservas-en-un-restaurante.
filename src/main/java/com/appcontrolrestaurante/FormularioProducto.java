package com.appcontrolrestaurante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormularioProducto extends JPanel implements ActionListener {

    private final JTextField tfId, tfNombre, tfDescripcion, tfPrecio, tfStock, tfCategoria, tfBuscarId;
    private final JButton btnInsertar, btnModificar, btnEliminar, btnBuscar, btnLimpiar;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private final JTextArea historial;
    private final Connection conexionSQL;

    public FormularioProducto(Connection conexionSQL) {
        this.conexionSQL = conexionSQL;
        setLayout(new BorderLayout());

        tfId = new JTextField();
        tfNombre = new JTextField();
        tfDescripcion = new JTextField();
        tfPrecio = new JTextField();
        tfStock = new JTextField();
        tfCategoria = new JTextField();
        tfBuscarId = new JTextField();

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

        JPanel panelEntrada = new JPanel(new GridLayout(6, 2));
        panelEntrada.add(new JLabel("Nombre:"));       panelEntrada.add(tfNombre);
        panelEntrada.add(new JLabel("Descripción:"));  panelEntrada.add(tfDescripcion);
        panelEntrada.add(new JLabel("Precio:"));       panelEntrada.add(tfPrecio);
        panelEntrada.add(new JLabel("Stock:"));        panelEntrada.add(tfStock);
        panelEntrada.add(new JLabel("Categoría:"));    panelEntrada.add(tfCategoria);
        panelEntrada.add(new JLabel("Buscar ID:"));    panelEntrada.add(tfBuscarId);

        JPanel panelBotones = new JPanel(new GridLayout(1, 5));
        panelBotones.add(btnInsertar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);

        historial = new JTextArea(4, 20);
        historial.setEditable(false);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Precio", "Stock", "Categoría"}, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        
        
        add(panelEntrada, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollTabla, BorderLayout.SOUTH);
        add(new JScrollPane(historial), BorderLayout.EAST);
       

        consultarProductos();
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
        String sql = "INSERT INTO Producto(nombre, descripcion, precio, stock, categoria) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfDescripcion.getText());
            stmt.setDouble(3, Double.parseDouble(tfPrecio.getText()));
            stmt.setInt(4, Integer.parseInt(tfStock.getText()));
            stmt.setString(5, tfCategoria.getText());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                modeloTabla.addRow(new Object[]{id, tfNombre.getText(), tfDescripcion.getText(), tfPrecio.getText(), tfStock.getText(), tfCategoria.getText()});
                historial.append("Producto insertado con ID: " + id + "\n");
            }

            limpiarCampos();
        } catch (SQLException ex) {
            historial.append("Error al insertar producto: " + ex + "\n");
        }
    }

    private void modificar() {
        if (tfBuscarId.getText().isEmpty()) return;
        int id = Integer.parseInt(tfBuscarId.getText());
        String sql = "UPDATE Producto SET nombre=?, descripcion=?, precio=?, stock=?, categoria=? WHERE id_producto=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfDescripcion.getText());
            stmt.setDouble(3, Double.parseDouble(tfPrecio.getText()));
            stmt.setInt(4, Integer.parseInt(tfStock.getText()));
            stmt.setString(5, tfCategoria.getText());
            stmt.setInt(6, id);
            stmt.executeUpdate();
            historial.append("Producto con ID " + id + " modificado.\n");
            actualizarTabla();
        } catch (SQLException ex) {
            historial.append("Error al modificar producto: " + ex + "\n");
        }
    }

    private void eliminar() {
        if (tfBuscarId.getText().isEmpty()) return;
        int id = Integer.parseInt(tfBuscarId.getText());
        String sql = "DELETE FROM Producto WHERE id_producto=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            historial.append("Producto con ID " + id + " eliminado.\n");
            actualizarTabla();
            limpiarCampos();
        } catch (SQLException ex) {
            historial.append("Error al eliminar producto: " + ex + "\n");
        }
    }

    private void buscar() {
        if (tfBuscarId.getText().isEmpty()) return;
        int id = Integer.parseInt(tfBuscarId.getText());
        String sql = "SELECT * FROM Producto WHERE id_producto=?";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tfNombre.setText(rs.getString("nombre"));
                tfDescripcion.setText(rs.getString("descripcion"));
                tfPrecio.setText(rs.getString("precio"));
                tfStock.setText(rs.getString("stock"));
                tfCategoria.setText(rs.getString("categoria"));
                historial.append("Producto encontrado: " + rs.getString("nombre") + "\n");
            } else {
                historial.append("Producto no encontrado con ID: " + id + "\n");
            }
        } catch (SQLException ex) {
            historial.append("Error al buscar producto: " + ex + "\n");
        }
    }

    private void consultarProductos() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT * FROM Producto";
        try (PreparedStatement stmt = conexionSQL.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                });
            }
        } catch (SQLException ex) {
            historial.append("Error al consultar productos: " + ex + "\n");
        }
    }

    private void actualizarTabla() {
        consultarProductos();
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfDescripcion.setText("");
        tfPrecio.setText("");
        tfStock.setText("");
        tfCategoria.setText("");
        tfBuscarId.setText("");
    }
}