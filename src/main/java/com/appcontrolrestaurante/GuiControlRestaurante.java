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
    private final Color AZUL_PRINCIPAL = new Color(30, 58, 138);
    private final Color AZUL_SECUNDARIO = new Color(59, 130, 246);
    private final Color BLANCO = new Color(255, 255, 255);

    public GuiControlRestaurante(Connection csql) {
        conexionSQL = csql;
        setTitle("Sistema de Gestión - Restaurante");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Configuración del panel principal con fondo azul degradado
        panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, AZUL_PRINCIPAL, 
                    getWidth(), getHeight(), AZUL_SECUNDARIO);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        setContentPane(panelPrincipal);

        // Configuración del título en negro
        JLabel lblTitulo = new JLabel("SISTEMA DE GESTIÓN - RESTAURANTE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.BLACK);
        
        // Efecto de sombra blanca para mejor contraste
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 0, 20, 0),
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true)
        ));
        
        // Panel para el título con fondo semi-transparente
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setOpaque(false);
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel fondoTexto = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 120));
                g.fillRoundRect(10, 5, getWidth()-20, getHeight()-10, 20, 20);
            }
        };
        fondoTexto.setLayout(new BorderLayout());
        fondoTexto.add(lblTitulo, BorderLayout.CENTER);
        panelTitulo.add(fondoTexto, BorderLayout.CENTER);
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);

        // Configuración de la barra de menú
        barra = new JMenuBar();
        barra.setBackground(AZUL_PRINCIPAL);
        barra.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Menú Sistema
        menuSistema = crearMenu("Sistema", BLANCO);
        itemSalir = crearItemMenu("Salir", BLANCO);
        menuSistema.add(itemSalir);
        
        // Menú Formularios
        menuFormularios = crearMenu("Formularios", BLANCO);
        itemClientes = crearItemMenu("Clientes", BLANCO);
        itemProductos = crearItemMenu("Productos", BLANCO);
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
        panelFormularios.setBackground(BLANCO);
        panelFormularios.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelFormularios.add(formCliente);
        panelPrincipal.add(panelFormularios, BorderLayout.CENTER);

        // Configurar acciones
        itemSalir.addActionListener(this);
        itemClientes.addActionListener(this);
        itemProductos.addActionListener(this);
        
        // Estilo para los componentes
        UIManager.put("Button.foreground", BLANCO);
        UIManager.put("Button.background", AZUL_SECUNDARIO);
        UIManager.put("Label.foreground", BLANCO);

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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new GuiControlRestaurante(null); // En producción pasar la conexión real
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
