package com.appcontrolrestaurante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;

public class GuiControlRestaurante extends JFrame implements ActionListener {

    private final JMenuBar barra;
    private final JMenu menuSistema, menuFormularios;
    private final JMenuItem itemSalir, itemClientes, itemProductos;

    private static Connection conexionSQL;
    private static FormularioCliente formCliente;
    private static FormularioProducto formProducto;
    private JPanel panelPrincipal;

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

        // Configuración del panel principal con fondo gris claro
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(GRIS_CLARO);
        setContentPane(panelPrincipal);

        // Configuración del título
        JLabel lblTitulo = new JLabel("SISTEMA DE RESERVACIONES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(GRIS_DARK);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Configuración de la barra de menú
        barra = new JMenuBar();
        barra.setBackground(AZUL_PRINCIPAL);
        barra.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Menú Sistema
        menuSistema = crearMenu("Sistema", Color.WHITE);
        itemSalir = crearItemMenu("Salir", Color.WHITE);
        menuSistema.add(itemSalir);

        // Menú Formularios
        menuFormularios = crearMenu("Formularios", Color.WHITE);
        itemClientes = crearItemMenu("Clientes", Color.WHITE);
        itemProductos = crearItemMenu("Productos", Color.WHITE);
        menuFormularios.add(itemClientes);
        menuFormularios.add(itemProductos);

        barra.add(menuSistema);
        barra.add(menuFormularios);
        setJMenuBar(barra);

        // Configurar los formularios
        formCliente = new FormularioCliente(conexionSQL);
        formProducto = new FormularioProducto(conexionSQL);

        // Panel para formularios con fondo blanco
        JPanel panelFormularios = new JPanel(new BorderLayout());
        panelFormularios.setBackground(Color.WHITE);
        panelFormularios.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelFormularios.add(formCliente);
        panelPrincipal.add(panelFormularios, BorderLayout.CENTER);

        // Configurar acciones
        itemSalir.addActionListener(this);
        itemClientes.addActionListener(this);
        itemProductos.addActionListener(this);

        // Estilo para los componentes
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.background", AZUL_SECUNDARIO);
        UIManager.put("Label.foreground", GRIS_DARK);

        // Pie de página
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
        JPanel panelFormularios = (JPanel) ((BorderLayout) panelPrincipal.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        panelFormularios.removeAll();

        switch (e.getActionCommand()) {
            case "Salir":
                cerrarAplicacion();
                break;
            case "Clientes":
                panelFormularios.add(formCliente);
                break;
            case "Productos":
                panelFormularios.add(formProducto);
                break;
        }

        panelFormularios.revalidate();
        panelFormularios.repaint();
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
   
