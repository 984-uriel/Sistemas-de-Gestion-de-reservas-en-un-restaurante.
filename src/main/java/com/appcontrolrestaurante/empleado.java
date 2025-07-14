package com.appcontrolrestaurante;
//Equipo de Reservas.

public class empleado {
//Equipo de Reservas.
    
    private int idempleado;
    private String nombre;
    private String puesto;

    // Constructor vacío
    public empleado() {
    }

    // Constructor con parámetros
    public empleado(int idEmpleado, String Nombre, String Puesto) {
        this.idempleado = idEmpleado;
        this.nombre = Nombre;
        this.puesto = Puesto;
    }

    // Getters y Setters
    public int getIdEmpleado() {
        return idempleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idempleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String Nombre) {
        this.nombre = Nombre;
    }

    public String getpuesto() {
        return puesto;
    }

    public void setpuesto(String Puesto) {
        this.puesto = Puesto;
    }
    // Para mostrar en JComboBox si se requiere
    @Override
    public String toString() {
        return idempleado + " - " +nombre;
    }
}