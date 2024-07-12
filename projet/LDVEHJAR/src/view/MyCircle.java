package view;

import application.controller.Controller;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import modeles.*;
import java.util.*;

import javafx.geometry.Point2D;

public class MyCircle extends Circle {

    protected Controller controller;
    protected Etape etape;
    protected ArrayList<Line> listLigne;
    static public List<MyCircle> selectedCirclesToDelete = new ArrayList<MyCircle>(); // Cercles à supprimer
    static public List<javafx.scene.text.Text> selectedNumberToDelete = new ArrayList<javafx.scene.text.Text>(); // Numéros à supprimer
    static public MyCircle c1, c2,currentCircle;
    static public Paint color;
    protected Paint circleColor;
    protected Text number_circle;


    public MyCircle(double x, double y, Integer radius, Controller controller, Etape etape) {
        super();
        this.etape = etape;
        this.setCenterX(x);
        this.setCenterY(y);
        //this.centerXProperty().set(x);
        //this.centerYProperty().set(y);
        this.setRadius(radius);
        this.number_circle = new Text();
        this.listLigne = new ArrayList<>();

        this.controller = controller;
        this.circleColor=Color.ORANGE;
        this.setFill(Color.ORANGE); // attributs du/des cercles
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::mouseClicked);
    }

    public void deleteLine(Line line){
        this.getListLigne().remove(line);
    } //permet de supprimer les lignes

    public Etape getEtape(){
        return this.etape;
    } //permet de récuperer l'étape liée au cercle

    public ArrayList<Line> getListLigne(){
        return this.listLigne;
    } //renvoie la liste des lignes liées au cercle

    public Text getNumber_circle(){
        return this.number_circle;
    } //renvoie le numéro du cercle

    public void AddListLigne(Line line){ //ajoute une ligne a la liste des lignes liées au cercle
        this.listLigne.add(line);
    }

    public static Point2D getDirection(MyCircle c1, MyCircle c2) {
        return new Point2D(c2.getCenterX() - c1.getCenterX(), c2.getCenterY() - c1.getCenterY()).normalize();
    }

    // Methode permettant de relier deux cercles
    public static void connect(MyCircle c1, MyCircle c2, Line line) {

        InvalidationListener startInvalidated = observable -> {
            Point2D dir = getDirection(c1, c2);
            Point2D diff = dir.multiply(c1.getRadius());
            line.setStartX(c1.getCenterX() + diff.getX());
            line.setStartY(c1.getCenterY() + diff.getY());
        };
        InvalidationListener endInvalidated = observable -> {
            Point2D dir = getDirection(c2, c1);
            Point2D diff = dir.multiply(c2.getRadius());
            line.setEndX(c2.getCenterX() + diff.getX());
            line.setEndY(c2.getCenterY() + diff.getY());
        };
        c1.centerXProperty().addListener(startInvalidated);
        c1.centerYProperty().addListener(startInvalidated);
        c1.radiusProperty().addListener(startInvalidated);

        startInvalidated.invalidated(null);

        c2.centerXProperty().addListener(endInvalidated);
        c2.centerYProperty().addListener(endInvalidated);
        c2.radiusProperty().addListener(endInvalidated);

        endInvalidated.invalidated(null);
    }




    public void mouseEntered(MouseEvent event) {
        this.setCursor(Cursor.HAND); // Curseur  qui change en main pour que l'utilisateur sache que c'est "bougeable".
    }

    public void mouseClicked(MouseEvent event) { // Clic + Released en même temps
        if (event.getButton() == MouseButton.PRIMARY) {
            this.controller.Save(new ActionEvent());

            if (event.getClickCount() == 2) { // Clic gauche 2 fois

                this.setStyle("-fx-effect: dropshadow(three-pass-box, black, 2, 2, 0, 0);"); // Effet de selection du cercle en vue de le lier avec un autre
                if (c1 == null) {
                    c1 = this;
                } else if(this == c1){
                    this.setStyle(null);
                    c1 = null;
                }

                else if(c2 == null){
                    c2 = this;
                }

                else{
                    c2.setStyle(null);
                    c2=this;

                }

            }
            else if (event.getClickCount() == 1) { // Clic gauche 1 fois
                if(currentCircle == this) {
                    this.setFill(color);
                    this.setStyle(null);
                    color= null;
                    currentCircle = null;
                    this.controller.ClearText(new ActionEvent()); //le textArea est vidé
                }
                else {
                    if(currentCircle != null){
                        currentCircle.setFill(color);
                        if(currentCircle.getStyle().equals("-fx-effect: dropshadow(three-pass-box, yellow, 2, 2, 0, 0);")){
                            currentCircle.setStyle(null);
                        }
                    }
                    color = this.getFill();
                    currentCircle = this;
                    this.controller.showContent(this.controller.getNumberCircle(this)); // Affichage du texte associé au cercle sur lequel on clic
                    if(this.getFill()==Color.BLUEVIOLET){
                        this.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 2, 2, 0, 0);");
                    }
                    else{
                        this.setFill(Color.YELLOW);
                    }
                }
            }
        }
        if (event.getButton() == MouseButton.SECONDARY) { // Click droit
            if (!MyCircle.selectedCirclesToDelete.contains(this)) {
                MyCircle.selectedCirclesToDelete.add(this); // ajout dans la liste du cercle selectionne
                MyCircle.selectedNumberToDelete.add(this.controller.getText(this));
                this.setFill(Color.BLACK);
            } else {
                MyCircle.selectedCirclesToDelete.remove(this); // on l'enleve de la liste car déselectionne
                MyCircle.selectedNumberToDelete.remove(this.controller.getText(this));
                this.setFill(this.circleColor);
            }
        }
    }

    public void mousePressed(MouseEvent event) { // Maintien du click
        if (event.getButton() == MouseButton.PRIMARY) {
            this.setCursor(Cursor.MOVE); // curseur directionnel.
        }
    }

    public void mouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            this.setCenterX(event.getX());
            this.setCenterY(event.getY());
            this.number_circle.setLayoutX(event.getX());
            this.number_circle.setLayoutY(event.getY() );
        }

    }

    public void mouseReleased(MouseEvent event) {
        this.setCursor(Cursor.HAND); // application.Main qui change en curseur.
    }

    public void setCircleColor(Paint color){
        this.circleColor=color;
    }

}

