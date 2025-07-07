package com.appcontrolrestaurante;
//Equipo de Reservas.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class GuiControlRestaurante extends JFrame implements ActionListener  {

    private final JMenuBar barra;
    private final JMenu menuSistema, menuFormularios;
    private final JMenuItem itemSalir, itemClientes, itemProductos;

    private static Connection conexionSQL;
    private static FormularioCliente formCliente;
    private static FormularioProducto formProducto;
    private PanelConFondo panelFondoPrincipal;

    public GuiControlRestaurante(Connection csql) throws IOException {
        conexionSQL = csql;

        setTitle("Control Restaurante");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelFondoPrincipal = new PanelConFondo (new Color (0,51,102));
        panelFondoPrincipal.setLayout(new BorderLayout());
        this.setContentPane(panelFondoPrincipal);

        // Menú
        barra = new JMenuBar();
        menuSistema = new JMenu("Sistema");
        menuFormularios = new JMenu("Formularios");

        itemSalir = new JMenuItem("Salir");
        itemClientes = new JMenuItem("Clientes");
        itemProductos = new JMenuItem("Productos"); // opcional

        itemSalir.addActionListener(this);
        itemClientes.addActionListener(this);
        itemProductos.addActionListener(this);

        menuSistema.add(itemSalir);
        menuFormularios.add(itemClientes);
        menuFormularios.add(itemProductos); // Elimina si no hay productos

        barra.add(menuSistema);
        barra.add(menuFormularios);

        setJMenuBar(barra);

        // Formularios
        formCliente = new FormularioCliente(conexionSQL);
        formProducto = new FormularioProducto(conexionSQL); // opcional

        panelFondoPrincipal.add(BorderLayout.CENTER, formCliente); // Muestra Clientes por defecto

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Salir" -> {
                try {
                    if (conexionSQL != null && !conexionSQL.isClosed()) {
                        conexionSQL.close();
                        System.out.println("Conexión cerrada correctamente.");
                    }
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
                System.exit(0);
            }

            case "Clientes" -> {
                panelFondoPrincipal.removeAll();
                panelFondoPrincipal.add(BorderLayout.CENTER, formCliente);
                panelFondoPrincipal.revalidate();
                panelFondoPrincipal.repaint();
            }

            case "Productos" -> {
                panelFondoPrincipal.removeAll();
                panelFondoPrincipal.add(BorderLayout.CENTER, formProducto); // opcional
                panelFondoPrincipal.revalidate();
                panelFondoPrincipal.repaint();
            }
        }
    }
}