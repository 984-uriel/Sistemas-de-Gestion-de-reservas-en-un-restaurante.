CREATE DATABASE ControlRestaurante;
USE ControlRestaurante;

CREATE TABLE Cliente (
    id_cliente INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100) UNIQUE
);
INSERT INTO Cliente (nombre, telefono, correo) VALUES 
('Mariana Lopez Rodriguez', '5534126789', 'mariana.lopez@ejemplo.com'),
('Juan Pérez González', '5612983476', 'juan.perez@ejemplo.com'),
('Laura	Hernández Martínez','5523678901','laura.hernandez@ejemplo.com'),
('Carlos Sánchez Ramírez','5589432167','carlos.sanchez@ejemplo.com'),	
('Sofía	Torres	Morales','5512349087','sofia.torres@ejemplo.com'),
('Luis	Romero	Ortega','5546789321','luis.romero@ejemplo.com'),	
('Andrea Vargas	Mendoza','5623457890',	'andrea.vargas@ejemplo.com'),	
('Daniel Castillo Ríos','	5598765432','daniel.castillo@ejemplo.com'),
('Fernanda	Jiménez	Navarro',	'5578901234'	,'fernanda.jimenez@ejemplo.com'),	
('Miguel	Gutiérrez	Herrera'	,'5556789012 ','miguel.gutierrez@ejemplo.com'),
('Valeria	Cruz	Domínguez	','5612345678	','valeria.cruz@ejemplo.com'),	
('Jorge	Reyes	Aguirre	','5587654321	','jorge.reyes@ejemplo.com'	),
('Camila	Ruiz	Paredes','	5543216789	','camila.ruiz@ejemplo.com'	),
('Ricardo	Flores	Salaza	','5590123456','	ricardo.flores@ejemplo.com'),	
('Natalia	Morales	Peña	','5576543210	','natalia.morales@ejemplo.com'),	
('Alejandro	Ortega	Cárdenas	','5554321098','	alejandro.ortega@ejemplo.com'),	
('Daniela	Ramos	Silva	','5532109876','	daniela.ramos@ejemplo.com'),	
('Francisco	Navarro	Escobar','	5621098765','	francisco.navarro@ejemplo.com'),	
('Gabriela	León Bautista','5589012345	','gabriela.leon@ejemplo.com');	


CREATE TABLE Medio_Reserva (
    id_medio INT PRIMARY KEY AUTO_INCREMENT,
    descripcion VARCHAR(50) NOT NULL
);
INSERT INTO Medio_Reserva (descripcion) VALUES 
('Teléfono'),
('Web'),
('App');

CREATE TABLE Restaurante (
    id_restaurante INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(15) NOT NULL
);
INSERT INTO Restaurante (nombre, direccion, telefono) VALUES 
('Restaurante Sabor AZteca', 'Calle Salvador alvarado  123', '9831258593'),
('Restaurante El Buen Sabor', 'Avenida Siempre Viva 456', '9831849566');

CREATE TABLE Mesa (
    id_mesa INT PRIMARY KEY AUTO_INCREMENT,
    numero_mesa INT NOT NULL,
    capacidad INT NOT NULL,
    id_restaurante INT,
    FOREIGN KEY (id_restaurante) REFERENCES Restaurante(id_restaurante)
);
INSERT INTO Mesa (numero_mesa, capacidad, id_restaurante) VALUES 
(1, 4, 1),
(2, 2, 1),
(3, 6, 2),
(4, 4, 2);

CREATE TABLE Empleado (
    id_empleado INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    puesto VARCHAR(50) NOT NULL
);

INSERT INTO Empleado (nombre, puesto) VALUES 
('Juan Rodriguez', 'Puesto administrativo'),
('Fernanda Garcia', 'Puesto administrativo'),
('Maria Martinez', 'puesto administrativo');

CREATE TABLE Reserva (
    id_reserva INT PRIMARY KEY AUTO_INCREMENT,
    id_cliente INT,
    id_mesa INT,
    id_empleado INT,  -- Agregado aquí
    id_medio INT,
    fecha_reserva DATE NOT NULL,
    hora_reserva TIME NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente),
    FOREIGN KEY (id_mesa) REFERENCES Mesa(id_mesa),
    FOREIGN KEY (id_empleado) REFERENCES Empleado(id_empleado),
    FOREIGN KEY (id_medio) REFERENCES Medio_Reserva(id_medio),
    CHECK (hora_reserva BETWEEN '08:00:00' AND '16:30:59')
);

INSERT INTO Reserva (id_cliente, id_mesa, id_empleado, id_medio, fecha_reserva, hora_reserva) VALUES 
(1, 1, 1, 1, '2025-04-10', '12:00:00'),
(2, 2, 2, 2, '2025-04-11', '15:30:00');

SELECT Restaurante.nombre AS Restaurante, Medio_Reserva.descripcion AS Medio, COUNT(Reserva.id_reserva) AS Total_Reservas
FROM Reserva
INNER JOIN Mesa ON Reserva.id_mesa = Mesa.id_mesa
INNER JOIN Restaurante ON Mesa.id_restaurante = Restaurante.id_restaurante
INNER JOIN Medio_Reserva ON Reserva.id_medio = Medio_Reserva.id_medio
GROUP BY Restaurante.nombre, Medio_Reserva.descripcion
ORDER BY Total_Reservas DESC;






