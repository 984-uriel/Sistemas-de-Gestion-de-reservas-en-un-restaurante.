package com.appcontrolrestaurante;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppControlRestaurante {
    public static void main(String[] args) throws IOException {
        String url = "jdbc:mysql://localhost:3306/ControlRestaurante";
        String user = "root";       // Cambia si usas otro usuario
        String password = "";       // Cambia si tu contraseña no está vacía

        try {
            Connection conexion = DriverManager.getConnection(url, user, password);
            System.out.println("Conexion establecida correctamente.");
            new GuiControlRestaurante(conexion);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}