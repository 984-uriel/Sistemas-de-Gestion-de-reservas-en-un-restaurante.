package com.appcontrolrestaurante;

/**
 *
 * @author Jonathan
 */
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PanelConFondo extends JPanel{
    private Image imagenFondo;
    private Color colorDeFondoSolido;
    
    public PanelConFondo (String rutaImagen){
        try {
            imagenFondo = ImageIO.read(new File(rutaImagen));
            this.colorDeFondoSolido = null;
        }catch (IOException e){
            e.printStackTrace();
            System.err.println("Error al cargar la imagen de fondo:"+ rutaImagen + ".Usando color solido de fondo.");
            this.colorDeFondoSolido = new Color (0,51,102);
            this.imagenFondo = null;
        }
    }
public PanelConFondo (Color color){
    this.colorDeFondoSolido = color;
    this.imagenFondo = null;
}
@Override
protected void paintComponent (Graphics g){
super.paintComponent (g);

if (imagenFondo !=null){
    g.drawImage(imagenFondo,0,0,getWidth(),getHeight(),this);
}else if (colorDeFondoSolido != null){
    
    g.setColor(colorDeFondoSolido);
    g.fillRect(0, 0,getWidth(),getHeight());
}
}
}