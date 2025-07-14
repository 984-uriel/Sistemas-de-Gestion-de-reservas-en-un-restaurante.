package com.appcontrolrestaurante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;

public class GuiControlRestaurante extends JFrame implements ActionListener {

    private final JMenuBar barra;
    private final JMenu menuSistema, menuFormularios;
    private final JMenuItem itemSalir, itemClientes, itemempleado;

    private static Connection conexionSQL;
    private static FormularioCliente formCliente;
    private static Formularioempleado formempleado;
    private JPanel panelPrincipal;
    private CardLayout cardLayout;
    private JPanel panelFormularios;
    

    // Colores personalizados
    private final Color GRIS_CLARO = new Color(240, 240, 240);
    private final Color GRIS_DARK = new Color(50, 50, 50);
    private final Color AZUL_PRINCIPAL = new Color(30, 58, 138);
    private final Color AZUL_SECUNDARIO = new Color(59, 130, 246);

    public GuiControlRestaurante(Connection csql) {
        conexionSQL = csql;
        setTitle("Sistema de Gestión - Restaurante");
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(GRIS_CLARO);
        setContentPane(panelPrincipal);

        JLabel lblTitulo = new JLabel("SISTEMA DE RESERVACIONES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(GRIS_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        barra = new JMenuBar();
        barra.setBackground(AZUL_PRINCIPAL);
        barra.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        menuSistema = crearMenu("Sistema", Color.WHITE);
        itemSalir = crearItemMenu("Salir", Color.WHITE);
        menuSistema.add(itemSalir);

        menuFormularios = crearMenu("Formularios", Color.WHITE);
        itemClientes = crearItemMenu("Clientes", Color.WHITE);
        itemempleado = crearItemMenu("Empleados", Color.WHITE);
        menuFormularios.add(itemClientes);
        menuFormularios.add(itemempleado);

        barra.add(menuSistema);
        barra.add(menuFormularios);
        setJMenuBar(barra);

        formCliente = new FormularioCliente(conexionSQL);
        formempleado = new Formularioempleado(conexionSQL);

        // Usar CardLayout para alternar entre formularios
        cardLayout = new CardLayout();
        panelFormularios = new JPanel(cardLayout);
        panelFormularios.setBackground(Color.WHITE);
        panelFormularios.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelFormularios.add(formCliente, "Clientes");
        panelFormularios.add(formempleado, "Empleados");
        panelPrincipal.add(panelFormularios, BorderLayout.CENTER);

        // Mostrar primero el formulario de Cliente
        cardLayout.show(panelFormularios, "Clientes");

        itemSalir.addActionListener(this);
        itemClientes.addActionListener(this);
        itemempleado.addActionListener(this);

        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.background", AZUL_SECUNDARIO);
        UIManager.put("Label.foreground", GRIS_DARK);

        JLabel lblFooter = new JLabel("© 2025 Restaurante Sabor Azteca. Todos los derechos reservados.", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFooter.setForeground(GRIS_DARK);
        panelPrincipal.add(lblFooter, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JMenu crearMenu(String texto, Color color) {
        JMenu menu = new JMenu(texto);
        menu.setForeground(color);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return menu;
    }

    private JMenuItem crearItemMenu(String texto, Color color) {
        JMenuItem item = new JMenuItem(texto);
        item.setForeground(color);
        item.setBackground(AZUL_PRINCIPAL);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Salir":
                cerrarAplicacion();
                break;
            case "Clientes":
                cardLayout.show(panelFormularios, "Clientes");
                break;
            case "Empleados":
                cardLayout.show(panelFormularios, "Empleados");
                break;
        }
    }

    private void cerrarAplicacion() {
        try {
            if (conexionSQL != null && !conexionSQL.isClosed()) {
                conexionSQL.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cerrar la conexión: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.exit(0);
    }
}

