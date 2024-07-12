package view;

import application.controller.Controller;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import java.util.*;

public class MySquare extends Rectangle {

    protected Controller controller;


    static public List<MySquare> selectedSquaresToDelete = new ArrayList<MySquare>(); // Carrés à supprimer
    static public List<javafx.scene.text.Text> selectedNameToDelete = new ArrayList<javafx.scene.text.Text>(); // Texte (nom de l'objet) à supprimer (quand on supprime l'objet associé

    static public MySquare currentSquare; //carré courrant
    protected Text name_Object; //Texte (associé a l'objet) affiché à coté du carré
    protected String name; //nom de l'objet

    //MySquare prend en argument : une position x,y , une longueur de coté size et le controller
    public MySquare(double x, double y, double size, Controller controller) {
        super();
        this.name= ""; //Nom de base de l'objet, avant modification de l'utilisateur, vide
        this.setX(x); //positionnement du carré
        this.setY(y);
        this.setHeight(size); //définition de la taille du carré
        this.setWidth(size);
        this.name_Object = new Text(); //création de l'objet Text situé a coté de l'objet contenant le nom de l'objet

        this.controller = controller;
        this.setFill(Color.ORANGE); // affichage du carré avec sa couleur de base

        //ajout d'écouteurs sur les evenements de souris, appelant les méthodes associées
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::mouseClicked);
    }



    //renvoie le Text situé a coté du carré contenant le nom de l'objet
    public Text getName_Object(){
        return this.name_Object;
    }

    // Curseur qui change en main pour que l'utilisateur sache que c'est "bougeable".
    public void mouseEntered(MouseEvent event) {
        this.setCursor(Cursor.HAND);
    }

    //action au clic de la souris
    public void mouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) { // Clic gauche 1 fois
            this.controller.Save(new ActionEvent()); //sauvegarde de l'information du textArea si on clique autre part
            if (event.getClickCount() == 1) { //simple clic
                if(currentSquare == this) { //si le carré courant est le carré selectionné il est désélectionné
                    this.setFill(Color.ORANGE); //sa couleur redevient orange
                    currentSquare = null; //il n'y a plus de carré courant
                    this.controller.ClearText(new ActionEvent()); //le textArea est vidé
                }
                else { //si le carré courant n'est pas le carré selectionné
                    if(currentSquare!=null){
                        currentSquare.setFill(Color.ORANGE); //l'ancien carré courrant redevient orange
                    }
                    this.setFill(Color.YELLOW); //il devient jaune
                    currentSquare = this; //il est considéré comme carré courrant
                    this.controller.showContent(7); // Son contenu s'inscrit dans le textArea (Text a droite de l'éditeur)
                }
            }
        }
        if (event.getButton() == MouseButton.SECONDARY) { // Click droit
            if (!MySquare.selectedSquaresToDelete.contains(this)) { //si on a pas encore fait un clic droit sur le carré ( si il n'est pas dans la liste des carrés à supprimer)
                MySquare.selectedSquaresToDelete.add(this); // on l'ajoute à la liste des carrés à supprimer
                MySquare.selectedNameToDelete.add(this.name_Object); //on ajoute son Text à la liste des Texts à supprimer
                this.setFill(Color.BLACK); //il devient noir
            } else { //si il avait déjà été séléctionné pour être supprimé
                MySquare.selectedSquaresToDelete.remove(this); // on l'enleve de la liste des carrés à supprimer
                MySquare.selectedNameToDelete.remove(this.name_Object); //on enlève son Text de la liste des Texts à supprimer
                this.setFill(Color.ORANGE); //il redevient orange
            }
        }
    }

    public void mousePressed(MouseEvent event) { // Maintien du click
        if (event.getButton() == MouseButton.PRIMARY) {
            this.setCursor(Cursor.MOVE); // curseur directionnel.
        }
    }

    //permet le déplacement du carré
    public void mouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            this.setX(event.getX());
            this.setY(event.getY());
            this.name_Object.setLayoutX(event.getX());
            this.name_Object.setLayoutY(event.getY() );
        }

    }

    public void mouseReleased(MouseEvent event) {
        this.setCursor(Cursor.HAND); // application.Main qui change en curseur.
    }


}