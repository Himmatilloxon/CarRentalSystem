package com.CarRentalSystem.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class rectangle extends Rectangle {

    public rectangle(double width, double height, Color borderColor, double borderWidth, double cornerRadius) {
        super(width, height); // Set width and height

        this.setFill(Color.TRANSPARENT); // Make background transparent
        this.setStroke(borderColor); // Set border color
        this.setStrokeWidth(borderWidth); // Set border width
        this.setArcWidth(cornerRadius); // Rounded corners
        this.setArcHeight(cornerRadius);
    }
}
