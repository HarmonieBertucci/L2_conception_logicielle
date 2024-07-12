package application.controller;

import application.Lecture;

import javafx.scene.control.TextField;
import modeles.*;
import view.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.ResourceBundle;
import java.io.*;
import java.io.IOException;


public class Controller implements Initializable {
    final public String rule = "Add : Ajouter un cercle étape" +
            "\n Delete : Supprimer un cercle étape" +
            "\n Link : Relier deux cercles-étapes" +
            "\n Set start & Set end : Mettre le cercle en tant que cercle de début et de fin respectivement ( les met en bleu foncé et bleu clair respectivement" +
            "\n Clique gauche une fois : Selectioner et afficher le texte associé à un cercle ( le met en jaune)" +
            "\n Clique gauche deux fois : Sélectionner le cercle courant (en vue de le relier avec un autre)" +
            "\n Balises importantes : ${X} : indique le numero d'une étape suivante X" +
            "\n Couleurs :" +
            "\n Bleu foncé : 1ere étape" +
            "\n Cyan : dernière étape" +
            "\n Violet : Chemin pour aller de la 1ere étapes à l'étape  (ou aux étapes) finale(s)";

    @FXML
    protected AnchorPane GraphVisualisation;

    @FXML
    protected AnchorPane InventoryVisualisation;

    @FXML
    protected TextArea textarea;

    @FXML
    protected TextArea rules;

    @FXML
    protected TextArea informations;

    @FXML
    protected TextField titleArea;

    private URL urlEtape;
    private URL urlInventaire;
    protected URL urlEtapeOUT;
    protected URL urlInventaireOUT;
    protected Livre livre;
    protected List<MyCircle> circlesOnScreen = new ArrayList<MyCircle>(); // Cercles crées
    protected List<MySquare> squaresOnScreen = new ArrayList<MySquare>(); // Carré crées
    protected Map<Shape, Text> selectedNumber = new HashMap<Shape, Text>();// Numéros à supprimer/bouger
    protected Inventaire inventaire = new Inventaire();
    protected boolean inventorySelected ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.livre = new Livre("Titre du livre");
        this.rules.setText(rule);
        this.inventorySelected = false;
        try {
            this.urlEtape = new URL("jar:file:" + getClass().getResource("/resources/dataEtape.txt").toString().substring(9));
            this.urlInventaire = new URL("jar:file:" + getClass().getResource("/resources/dataInventaire.txt").toString().substring(9));
             }catch(Exception e){
            e.printStackTrace();
        }

        backup();
        updateInformation();
    }



    @FXML
    public void InventorySelected() throws NullPointerException{
        this.inventorySelected = true;
        if(MyCircle.currentCircle != null) {
            this.textarea.clear();
            MyCircle.currentCircle.setFill(MyCircle.color);
            MyCircle.currentCircle = null;
        }

    }

    @FXML
    public void graphSelected(){
        if(MySquare.currentSquare !=null) {
            this.textarea.clear();
            MySquare.currentSquare.setFill(Color.ORANGE);
            MySquare.currentSquare= null;
        }
        this.inventorySelected = false;
    }

    //permet d'ajouter/de créer un cercle ou un carré
    @FXML
    public void Add(ActionEvent event){
        if(this.inventorySelected){
            addObject();
        }
        else {
            addEtape();
        }
        updateInformation();
    }

    //permet d'ajouter/de créer un cercle/une étape dans le livre
    public void addEtape() {

        Etape etape = this.livre.creerEtape();
        MyCircle circle = new MyCircle(100, 100, 20, this, etape); // création du cercle
        circle.getNumber_circle().setText(String.valueOf(this.circlesOnScreen.size()));

        this.circlesOnScreen.add(circle); // Ajout du cercle à la liste des cercles présents sur la scène
        circle.getNumber_circle().relocate(circle.getCenterX(), circle.getCenterY()); // Placement du texte au centre du cercle


        this.selectedNumber.put(circle, circle.getNumber_circle()); // ajout du texte "courant" au dico

        GraphVisualisation.getChildren().addAll(circle, circle.getNumber_circle());

    }

    //permet d'ajouter/de créer un carré/un objet dans l'inventaire
    public void addObject(){
        MySquare square = new MySquare(100.0, 100.0, 40.0, this);
        square.getName_Object().setText("");

        this.squaresOnScreen.add(square); // Ajout du cercle à la liste des cercles présents sur la scène
        square.getName_Object().relocate(square.getX(), square.getY()-13); // Placement du texte au centre du cercle


        this.selectedNumber.put(square, square.getName_Object()); // ajout du texte "courant" au dico

        InventoryVisualisation.getChildren().addAll(square, square.getName_Object());
    }

    //permet de supprimer un élément séléctionné (cercle ou carré)
    @FXML
    public void Delete(ActionEvent event){
        if(this.inventorySelected){
            deleteSquare();


        }
        else{
            deleteCircle();

        }
        updateInformation();
    }

    //permet de supprimer un cercle/ une étape du livre
    public void deleteCircle() {
        GraphVisualisation.getChildren().removeAll(MyCircle.selectedCirclesToDelete); // supprime les cercles de l'écran
        GraphVisualisation.getChildren().removeAll(MyCircle.selectedNumberToDelete); // supprime les numéros de cercles selectionnés
        for (MyCircle circle : MyCircle.selectedCirclesToDelete) {
            GraphVisualisation.getChildren().removeAll(circle.getListLigne());
            for (Line line : circle.getListLigne()) {
                for (Lien link : circle.getEtape().getLienEtapeSuivantes()) {
                    etapeToCircle(link.getFille()).deleteLine(line);// on récupére le cercle associé à l'étape fille du cercle que l'on veut supprimer

                }

                for (Lien link : circle.getEtape().getLienEtapesPrecedentes()) {
                    etapeToCircle(link.getMere()).deleteLine(line);// on récupére le cercle associé à l'étape fille du cercle que l'on veut supprimer

                }

            }

            if(this.livre.getFirstEtape()!=null && this.livre.getFirstEtape().equals(this.livre.getEtape(getNumberCircle(circle)))){
                this.livre.setPremiereEtape(null);
            }
            this.livre.supprimerEtape(getNumberCircle(circle)); // On supprime les étapes asscociées aux cercles
            this.circlesOnScreen.remove(circle);
        }
        for (MyCircle circle : this.circlesOnScreen) {

            circle.getNumber_circle().setText(String.valueOf(this.livre.getNumEtape(circle.getEtape()))); // On récupère le numéro des étapes et on replace cette valeur par l'ancienne.
            //circle.getNumber_circle().relocate(circle.getCenterX(), circle.getCenterY()); // Placement du texte au centre du cercle
            //GraphVisualisation.getChildren().removeAll(circle, circle.getNumber_circle()); // On supprime les textes de tout les cercles pour éviter de générer une erreur
            //GraphVisualisation.getChildren().addAll(circle, circle.getNumber_circle()); // On ajoute les nouveaux textes avec leur bon numéros associer aux étapes

        }
        if(MyCircle.currentCircle !=null) {
            MyCircle.currentCircle.setFill(MyCircle.color);
            MyCircle.currentCircle = null;
            MyCircle.color = null;
            ClearText(new ActionEvent());
        }
        MyCircle.selectedCirclesToDelete.clear(); // clear la liste des circles
        MyCircle.selectedNumberToDelete.clear(); // clear la liste de numéros de cercles
    }

    //permet de supprimer un carré/un objet de l'inventaire
    public void deleteSquare(){
        this.squaresOnScreen.removeAll(MySquare.selectedSquaresToDelete);
        this.squaresOnScreen.removeAll(MySquare.selectedNameToDelete);
        for(MySquare square : MySquare.selectedSquaresToDelete){
            this.InventoryVisualisation.getChildren().remove(square);
            this.InventoryVisualisation.getChildren().remove(square.getName_Object());
            this.inventaire.getListeEditeur().remove(square.getName_Object());
        }
        MySquare.selectedSquaresToDelete.clear();
        MySquare.selectedNameToDelete.clear();
        if(MySquare.currentSquare !=null) {
            MySquare.currentSquare.setFill(Color.ORANGE);
            MySquare.currentSquare = null;
            ClearText(new ActionEvent());
        }
    }

    //permet de lier deux étapes/cercles
    @FXML
    public void Link(ActionEvent event) { // Relie deux cercles (deux étapes)
        Line line = new Line();
        if (MyCircle.c1 != null && MyCircle.c2 != null) { // Si l'utilisateur clique sur le bouton link sans avoir séléctionné de cercle il ne se passe rien


            MyCircle.connect(MyCircle.c1, MyCircle.c2, line);
            this.livre.getEtape(getNumberCircle(MyCircle.c1)).ajouterEtapeSuivante(this.livre.getListeEtapes(), this.livre.getEtape(getNumberCircle(MyCircle.c2))); // ajout d'un lien entre l'etape de c1 et l'etape de c2


            GraphVisualisation.getChildren().add(line);
            MyCircle.c1.AddListLigne(line);
            MyCircle.c2.AddListLigne(line);

            MyCircle.c1.setStyle(null);
            MyCircle.c2.setStyle(null);

            MyCircle.c1 = null;
            MyCircle.c2 = null;

            updateInformation();


        }


    }

    //permet de défaire le lien entre deux étapes/cercles
    @FXML
    public void Delink(ActionEvent event) {
        if (MyCircle.c1 != null && MyCircle.c2 != null) {


            MyCircle.c1.setStyle(null);
            MyCircle.c2.setStyle(null);
            Lien linkToDelete=null;
            Line lineToDelete = null;
            for (Line line : MyCircle.c1.getListLigne()) {
                if (MyCircle.c2.getListLigne().contains(line)) {
                    lineToDelete = line;
                    boolean bool = true;
                    for (Lien link : MyCircle.c1.getEtape().getLienEtapeSuivantes()) {
                        if (MyCircle.c2.getEtape().equals(link.getFille())) {
                            bool = false;
                            linkToDelete=link;
                        }
                    }
                    if (bool) {
                        for (Lien link : MyCircle.c2.getEtape().getLienEtapeSuivantes()) {
                            if (MyCircle.c1.getEtape().equals(link.getFille())) {
                                linkToDelete=link;
                            }
                        }
                    }

                }

            }
            if(linkToDelete!=null){
                MyCircle.c1.getEtape().deleteEtapeSuivantes(this.livre, linkToDelete);
                MyCircle.c2.getEtape().deleteEtapePrecedentes(this.livre, linkToDelete);
                MyCircle.c1.getListLigne().remove(lineToDelete);
                MyCircle.c2.getListLigne().remove(lineToDelete);

                GraphVisualisation.getChildren().remove(lineToDelete);
            }
            MyCircle.c1 = null;
            MyCircle.c2 = null;


            updateInformation();

        }
    }

    //permet de supprimer le livre en cours d'écriture
    @FXML
    public void ClearGraph(ActionEvent event) {
        if(this.inventorySelected){
            ClearText(new ActionEvent());
            InventoryVisualisation.getChildren().clear();
            this.squaresOnScreen.clear();
            selectedNumber.clear();
            this.inventaire =new Inventaire();

        }
        else{
            this.livre = new Livre("");
            GraphVisualisation.getChildren().clear(); //Clear toute la scene
            this.circlesOnScreen.clear();
            ClearText(new ActionEvent());
            updateInformation();
            this.titleArea.setText("");

            InventoryVisualisation.getChildren().clear();
            this.squaresOnScreen.clear();
            selectedNumber.clear();
            this.inventaire =new Inventaire();

        }
        //Save(new ActionEvent());
    }

    //définit le cercle séléctionné comme étape de commencement
    @FXML
    public void Start(ActionEvent event) {
        if (MyCircle.currentCircle != null) {
            if(this.livre.getFirstEtape()!=null){
                etapeToCircle(this.livre.getFirstEtape()).setFill(Color.ORANGE);
                etapeToCircle(this.livre.getFirstEtape()).setCircleColor(Color.ORANGE);
            }
            this.livre.setPremiereEtape(MyCircle.currentCircle.getEtape());
            MyCircle.currentCircle.setFill(Color.DARKCYAN);
            MyCircle.currentCircle.setCircleColor(Color.DARKCYAN);
            MyCircle.currentCircle = null;

            updateInformation();

        }
    }

    //définit le cercle séléctionné comme étape solution
    @FXML
    public void End(ActionEvent event) {
        if (MyCircle.currentCircle != null) {
            if (this.livre.getEtapeFinal().contains(MyCircle.currentCircle.getEtape())) {
                this.livre.getEtapeFinal().remove(MyCircle.currentCircle.getEtape());
                MyCircle.currentCircle.setFill(Color.ORANGE);
                MyCircle.currentCircle.setCircleColor(Color.ORANGE);

            } else {
                this.livre.setEtapeFinal(MyCircle.currentCircle.getEtape());
                MyCircle.currentCircle.setFill(Color.CYAN);
                MyCircle.currentCircle.setCircleColor(Color.CYAN);
            }
            MyCircle.currentCircle = null;

            updateInformation();
        }
    }

    //affiche en violet le chemin entre l'étape de commencement et la (ou les) solution(s)
    @FXML
    public void Path(ActionEvent event) {
        if(MyCircle.currentCircle !=null) {
            MyCircle.currentCircle.setFill(MyCircle.color);
            MyCircle.currentCircle = null;
            MyCircle.color = null;
            ClearText(new ActionEvent());
        }
        this.livre.setChemin();
        boolean verifAfficheChemin = false;
        for (Etape etape : this.livre.getListeEtapesChemin()) {
            if (etapeToCircle(etape).getFill() == Color.BLUEVIOLET) {
                verifAfficheChemin = true;
                etapeToCircle(etape).setFill(Color.ORANGE);
                etapeToCircle(etape).setCircleColor(Color.ORANGE);
                if (etapeToCircle(etape).getStyle().equals("-fx-effect: dropshadow(three-pass-box, yellow, 2, 2, 0, 0);")) {
                    etapeToCircle(etape).setStyle(null);
                    etapeToCircle(etape).setFill(Color.YELLOW);
                }
            } else if (etape != this.livre.getFirstEtape() && !verifAfficheChemin) {
                etapeToCircle(etape).setFill(Color.BLUEVIOLET);
                etapeToCircle(etape).setCircleColor(Color.BLUEVIOLET);
                if (etapeToCircle(etape) == MyCircle.currentCircle) {
                    etapeToCircle(etape).setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 2, 2, 0, 0);");
                }
            }
        }
    }

    //Clear la zone de texte
    @FXML
    public void ClearText(ActionEvent event) {
        if (this.inventorySelected && MySquare.currentSquare != null){
            showContent(7);
        }
        else{
            this.textarea.clear();
        }
    }

    //méthode du bouton save qui lance la sauvegarde du livre et de l'inventaire
    @FXML
    public void Save(ActionEvent event) {
        if(this.inventorySelected){
            saveObject();
        }
        else {
            if (getNumberCircle(MyCircle.currentCircle) >=0 && this.circlesOnScreen.size()>0) {
                this.livre.getEtape(getNumberCircle(MyCircle.currentCircle)).redactionTexte(textarea.getText()); // Ajout du contenu du paragraphe dans la liste
            }
        }
        updateInformation();
        //sauvegardeEtape();
       // sauvegardeInventaire();
    }

    //lance le mode lecture
    @FXML
    public void Show(ActionEvent event) {
        lecture();
    }

    //affiche dans le textArea les informations liés au carré ou au cercle séléctionné
    public void showContent(Integer number) {
        if(this.inventorySelected && MySquare.currentSquare != null){

            String affiche = MySquare.currentSquare.getName_Object().getText();
            if(this.inventaire.getListeEditeur().containsKey(affiche)){
                String aqui="[";
                for(Etape etape : this.inventaire.getListeEtapeRecup(affiche)) {
                    if (etape != null) {
                        aqui = aqui + this.livre.getNumEtape(etape) + ",";
                    }
                }
                aqui = aqui + "]";

                String uti= "[";
                for(Lien link : this.inventaire.getListeEtapeUti(affiche)){
                    if(link!=null){
                        uti = uti + this.livre.getNumEtape(link.getMere()) + "-"+this.livre.getNumEtape(link.getFille())+ ",";
                    }
                }
                uti = uti +"]";
                textarea.setText("Nom de l'objet : " + affiche + "\n"
                        +"Etape d'aquisition : " + aqui + "\n"+
                        "Etape d'utilisation : " + uti );
            }
            else {
                textarea.setText("Nom de l'objet :  \n"
                        +"Etape d'aquisition : [" + "]\n"+
                        "Etape d'utilisation : [-]" );
            }
        }

        else {
            textarea.setText(this.livre.getEtape(number).getTexte());
        }
    }


    //permet l'ouverture du mode lecture
    public void lecture() {
        new Lecture(this.livre,this.inventaire);
    }

    //permet de renommer le livre
    @FXML
    public void Rename(ActionEvent event) {
        this.livre.setNom(this.titleArea.getText());
    }


    // Prends l'entier numéro "interne" du cercle / Méthode qui permet la liaison pour bouger le texte du cercle en même temps que le cercle
    public Text getText(MyCircle circle) {
        return this.selectedNumber.get(circle);
    }

    //renvoie le numéro du cercle dans la liste des cercles
    public int getNumberCircle(MyCircle cercle) {
        return this.circlesOnScreen.indexOf(cercle);
    }

    public MyCircle etapeToCircle(Etape etape) {
        return this.circlesOnScreen.get(this.livre.getNumEtape(etape));
    }

    //permet de savoir si un string est un entier
    public boolean isInteger(String chaine) {
        try {
            Integer.parseInt(chaine);
        } catch (NumberFormatException e){ //si il y a une exception NumberFormat c'est que la chaine de caractères n'est pas un entier
            return false;
        }
        return true;
    }

    //créé l'objet dans l'inventaire grace au text dans le textArea
    public void saveObject(){
        if(MySquare.currentSquare != null){
            String texte= textarea.getText();
            String nom=MySquare.currentSquare.getName_Object().getText();
            ArrayList<Etape> aqui = new ArrayList<Etape>();
            ArrayList<Lien> uti = new ArrayList<Lien>();
            String[] divise = texte.split("\n");
            for(String text : divise){
                if(text.contains("Nom de l'objet :")  ){
                    nom= text.substring(text.indexOf(":")+1).replace(" " , "");

                }
                else if(text.contains("Etape d'aquisition :") ){
                   String listenum= text.substring(text.indexOf("[")+1, text.indexOf("]"));
                   String[] list=listenum.split(",");
                   for(String num : list){

                       if(isInteger(num) && (Integer.parseInt(num)<this.livre.getListeEtapes().size())){
                        aqui.add(this.livre.getEtape(Integer.parseInt(num)));
                       }
                   }
                }
                else if(text.contains("Etape d'utilisation :") ){
                    String[] mereFille;
                    Lien link;
                    uti = new ArrayList<Lien>();
                    String listenumu= text.substring(text.indexOf("[")+1, text.indexOf("]"));
                    String[] list=listenumu.split(",");
                    for(String num : list){
                        mereFille = num.split("-");
                        if(mereFille.length >0){
                        if(isInteger(mereFille[0]) && (Integer.parseInt(mereFille[0])<this.livre.getListeEtapes().size()) && (Integer.parseInt(mereFille[1])<this.livre.getListeEtapes().size())){
                            link = this.livre.getEtape(Integer.parseInt(mereFille[0])).getLienMereFille(this.livre.getEtape(Integer.parseInt(mereFille[1])));
                            if(link == null){
                                link = this.livre.getEtape(Integer.parseInt(mereFille[0])).getLienFilleMere(this.livre.getEtape(Integer.parseInt(mereFille[1])));
                            }
                            uti.add(link);
                        }
                        }
                    }
                }
            }
            if(!this.inventaire.getListeEditeur().containsKey(nom)) {
                this.inventaire.addObject(nom);
                this.inventaire.getListeEditeur().remove(MySquare.currentSquare.getName_Object().getText());
            }
            this.inventaire.addEtapeRecup(nom,aqui);
            this.inventaire.addEtapeUtilisation(nom,uti);

            MySquare.currentSquare.getName_Object().setText(nom);
        }
    }



    // permet de mettre à jour les informations (nombre d'étapes, difficulté, nombre d'étape solution
    public void updateInformation(){
        this.livre.parcourChemin();
        this.livre.setChemin();
        String info="";
        info = info + "Nombre d'etape : " + this.livre.getListeEtapes().size() + "\n";
        info = info + "Nombre d'etape solution : " + this.livre.getEtapeFinal().size() + "\n";
        if(this.livre.getFirstEtape()!=null){
            info = info + "Nombre de chemin qui mênent aux solutions : " + this.livre.getNumCheminSolution() + "\n";
            info = info + "Nombre de chemin possible : " + this.livre.getNumCheminPossible() + "\n";
            if(this.livre.getNumCheminPossible()!=0){
                info = info + "Difficuté : " + (int) (((float) this.livre.getNumCheminSolution()/this.livre.getNumCheminPossible() )* 100) +"%" + "\n";
            }
        }
        this.informations.setText(info);
        
    }

    //permet de sauvegarder le livre pour pouvoir le récupérer à la réouverture de l'éditeur
   /* public void sauvegardeEtape() {
        File monFichier = null;
        FileOutputStream fop = null;
        try {
            // Ouvre le fichier xyz.dat et y enregistre
            // les données du tableau
            monFichier = new File(urlEtapeOUT.toURI());
            fop = new FileOutputStream(monFichier);
            fop.write(String.format("Titre du livre : %s \n ", this.livre.getName()).getBytes());

            if(this.livre.getListeEtapes().size()>0 && this.circlesOnScreen.size()>0) {
                for (int i = 0; i < this.livre.getListeEtapes().size(); i++) {
                    fop.write(String.format("Numero de l'etape  : %d \n  %s \nµµµµµµµ\nCoord X : %f \nCoord Y : %f \n§§§§§§§\n ", i,this.livre.getEtape(i).getTexte(),this.circlesOnScreen.get(i).centerXProperty().get(),this.circlesOnScreen.get(i).centerYProperty().get()).getBytes());
                }

                fop.write(String.format("Premiere Etape : %d \nDernières Etape : ", this.livre.getNumEtape(this.livre.getFirstEtape())).getBytes());
                for (Etape etape : this.livre.getEtapeFinal()) {
                    fop.write(String.format("\n%d" , this.livre.getNumEtape(etape)).getBytes());
                }
                fop.write(String.format("\n~~~~~~~ \n~~~~~~~").getBytes());
            }
            else{

                fop.write(String.format("").getBytes());
            }

        } catch (Exception exception) {
              System.out.println("Impossible d'écrire dans le fichier :"
                 + exception.toString());
        } finally {
            try {
                assert fop != null;
                fop.close();
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }
    }*/

    //permet de sauvegarder l'inventaire pour pouvoir le récupérer a la réouverture de l'éditeur
   /* public void sauvegardeInventaire(){
        File monFichier =null;
        FileOutputStream fop = null;


        try {
            // Ouvre le fichier xyz.dat et y enregistre
            // les données du tableau
            monFichier = new File(urlInventaireOUT.toURI());
            fop = new FileOutputStream(monFichier);
            if (this.inventaire.getListeEditeur().keySet().size() > 0) {
                for (String affiche : this.inventaire.getListeEditeur().keySet()) {

                    if (this.inventaire.getListeEditeur().containsKey(affiche)) {
                        String aqui = "[";
                        for (Etape etape : this.inventaire.getListeEtapeRecup(affiche)) {
                            if (etape != null) {
                                aqui = aqui + this.livre.getNumEtape(etape) + ",";
                            }
                        }
                        aqui = aqui + "]";

                        String uti = "[";
                        for (Lien link : this.inventaire.getListeEtapeUti(affiche)) {
                            if (link != null) {
                                uti = uti + this.livre.getNumEtape(link.getMere()) + "-" + this.livre.getNumEtape(link.getFille()) + ",";
                            }
                        }
                        uti = uti + "]";
                        MySquare carre = null;
                        for (MySquare square : this.squaresOnScreen) {
                            if (square.getName_Object().getText().equals(affiche)) {
                                carre = square;
                            }
                        }

                        fop.write(String.format("Nom de l'objet : %s \nEtape d'aquisition : %s \nEtape d'utilisation : %s \n  ", affiche, aqui,uti).getBytes());
                        String.format("Nom de l'objet : %s \nEtape d'aquisition : %s \nEtape d'utilisation : %s \n  ", affiche, aqui,uti).getBytes();
                        if (carre != null) {
                            fop.write(String.format("Coord X : %f \nCoord Y : %f \n ",carre.getX(),carre.getY()).getBytes());
                        }
                    }
                }
                fop.write(String.format("~~~~~~~~").getBytes());

            }
            else{
                fop.write(String.format("").getBytes());
            }
        }catch (Exception e) {
              System.out.println("Impossible d'écrire dans le fichier :"
                 + e.toString());
        } finally {
            try {
                fop.flush();
                assert fop != null;
                fop.close();

            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }
    }*/

    //permet de récupérer les étapes quand on réouvre l'éditeur
    public void backup() {
        FileReader monFichier = null;
        BufferedReader tampon = null; // Rendre accessible le texte rapidement / Facilite l'accès aux données
        String texte = "";
        int currentnumber = 1;
        try {
            // Lecture du fichier pour création des étapes / récupération du contenu
            InputStream in = this.urlEtape.openStream();
            tampon = new BufferedReader(new InputStreamReader(in)); // On stocke le contenu dans le buffer
            String ligne = tampon.readLine(); // On lis les lignes
            if (ligne == null ){
                this.livre = new Livre("");
            } else {
                    this.livre = new Livre(ligne.substring(16)); // Récupération / Création du nom du livre
                    ligne = tampon.readLine();
                    while ((ligne!=null) &&!ligne.contains("~~~~~~~")) {  // Si la ligne actuelle n'est pas un fin de document
                        if (ligne.contains("Numero de l'etape :")) { // Si elle contient un début d'étape
                            this.livre.creerEtape();
                            currentnumber = Integer.parseInt(ligne.substring(19)); // Récupération de son numéro
                            ligne = tampon.readLine();
                            while (!ligne.contains("µµµµµµµ")) { // Tant que la ligne n'est pas une fin d'étape
                                texte += ligne + "\n";
                                ligne = tampon.readLine();
                            } // On sort de l'étape
                            this.livre.getEtape(currentnumber).redactionTexte(texte); // Enregistrement du contenu de l'etape
                            texte = ""; // reset de la variable
                        } else if (ligne.contains("Premiere Etape :") && Integer.parseInt(ligne.substring(16))!=-1 ) {
                            this.livre.setPremiereEtape(this.livre.getEtape(Integer.parseInt(ligne.substring(16))));
                        } else if (ligne.contains("Dernières Etape :")) {
                            ligne = tampon.readLine();
                            while (!ligne.contains("~~~~~")) {
                                this.livre.setEtapeFinal(this.livre.getEtape(Integer.parseInt(ligne)));
                                ligne = tampon.readLine();
                            }
                        }
                        ligne = tampon.readLine();// Fin du while
                    }
                }
                // Relecture du fichier pour les liens entre étapes
                in = this.urlEtape.openStream();
                tampon = new BufferedReader(new InputStreamReader(in));
                ligne = tampon.readLine();
                while (ligne != null) {
                    if (ligne.contains("Numero de l'etape :")) {
                        currentnumber = Integer.parseInt(ligne.substring(19)); //get numéro de l'étape actuelle
                        ligne = tampon.readLine();
                    } else if (ligne.contains("${")) {
                        this.livre.getEtape(currentnumber).ajouterEtapeSuivante(this.livre.getListeEtapes(), this.livre.getEtape(Integer.parseInt(ligne.substring(ligne.indexOf("${") + 2, ligne.indexOf("}")))));
                        ligne=ligne.substring(ligne.indexOf("}")+1);
                    }
                    else {
                        ligne = tampon.readLine();
                    }
                }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                assert tampon != null;
                tampon.close();

            } catch (IOException exception1) {
                exception1.printStackTrace();
            } catch (NullPointerException exception2) {
                exception2.printStackTrace();
            }
        }

        backupInterface();
    }

    //permet de récupérer l'interface (partie Graph) quand on réouvre l'éditeur
    public void backupInterface() {
        FileReader monFichier = null;
        BufferedReader tampon = null; // Rendre accessible le texte rapidement / Facilite l'accès aux données
        MyCircle circle;
        Text text;
        try {
            // Lecture du fichier pour création des étapes / récupération du contenu
            InputStream in = this.urlEtape.openStream();
            tampon = new BufferedReader(new InputStreamReader(in)); // On stocke le contenu dans le buffer
            String ligne = tampon.readLine(); // On lis les lignes
            double x;
            double y;

            if (ligne != null) { // Si y'a du texte / Document non vide
                this.titleArea.setText(this.livre.getName());
                while ((ligne != null)) {  // Si la ligne actuelle n'est pas un fin de document
                    if (ligne.contains("Coord X : ")) {
                        x = Float.parseFloat(ligne.substring(10));
                        ligne = tampon.readLine();
                        y = Float.parseFloat(ligne.substring(10));

                        circle = new MyCircle(x, y, 20, this, this.livre.getEtape(this.circlesOnScreen.size())); // création du cercle
                        if (this.livre.getFirstEtape() == this.livre.getEtape(this.circlesOnScreen.size())) {
                            circle.setFill(Color.DARKCYAN);
                        } else if (this.livre.getEtapeFinal().contains(this.livre.getEtape(this.circlesOnScreen.size()))) {
                            circle.setFill(Color.CYAN);
                        }
                        circle.getNumber_circle().setText(this.circlesOnScreen.size() + "");// ajout du texte dans le cercle, entier I converti en string "text"
                        circle.getNumber_circle().relocate(circle.getCenterX(), circle.getCenterY()); // Placement du texte au centre du cercle
                        this.selectedNumber.put(circle,circle.getNumber_circle() ); // ajout du texte "courant" au dico
                        GraphVisualisation.getChildren().addAll(circle, circle.getNumber_circle());
                        this.circlesOnScreen.add(circle); // Ajout du cercle à la liste des cercles présents sur la scène

                    }
                    ligne = tampon.readLine();
                }

                //line
                MyCircle c1;
                MyCircle c2;
                for (Etape etape : this.livre.getListeEtapes()) {
                    for (Lien link : etape.getLienEtapeSuivantes()) {
                        c1 = etapeToCircle(link.getMere());
                        c2 = etapeToCircle(link.getFille());
                        Line line = new Line();
                        MyCircle.connect(c1, c2, line);

                        GraphVisualisation.getChildren().add(line);
                        c1.AddListLigne(line);
                        c2.AddListLigne(line);

                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                assert tampon != null;
                tampon.close();

            } catch (IOException exception1) {
                exception1.printStackTrace();
            } catch (NullPointerException exception2) {
                exception2.printStackTrace();
            }
        }
        backupInventaire();
    }

    //permet de récupérer l'inventaire quand on réouvre l'éditeur
    public void backupInventaire(){
        FileReader monFichier = null;
        BufferedReader tampon = null;

        try {
            // Lecture du fichier pour création des étapes / récupération du contenu
            InputStream in = this.urlInventaire.openStream();
            tampon = new BufferedReader(new InputStreamReader(in)); // On stocke le contenu dans le buffer
            String ligne = tampon.readLine(); // On lis les lignes
            MySquare square;
            double x;
            double y;
            String texte="";
            if(ligne != null){
                while(ligne!=null && !ligne.contains("~~~~")){
                    if(ligne.contains("Nom de l'objet :")){
                        texte=ligne;
                        }
                        ligne = tampon.readLine();
                        if(ligne.contains("Etape d'aquisition :")){
                            texte=texte+"\n"+ligne;
                        }
                        ligne = tampon.readLine();
                        if(ligne.contains("Etape d'utilisation :")){
                            texte=texte+"\n"+ligne;
                        }
                        ligne = tampon.readLine();
                        if(ligne.contains("Coord X :")){
                            x = Float.parseFloat(ligne.substring(ligne.indexOf(":")+1));
                            ligne = tampon.readLine();
                            y = Float.parseFloat(ligne.substring(ligne.indexOf(":")+1));

                            square = new MySquare(x, y, 40, this); // création du cercle

                            InventoryVisualisation.getChildren().addAll(square, square.getName_Object());
                            this.squaresOnScreen.add(square); // Ajout du cercle à la liste des cercles présents sur la scène
                            MySquare.currentSquare=square;
                            InventorySelected();
                            textarea.setText(texte);
                            saveObject();
                            square.getName_Object().relocate(square.getX(), square.getY()-13);
                            textarea.clear();
                            graphSelected();
                        }
                    ligne = tampon.readLine();
                    }
                }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                assert tampon != null;
                tampon.close();

            } catch (IOException exception1) {
                exception1.printStackTrace();
            } catch (NullPointerException exception2) {
                exception2.printStackTrace();
            }
        }

    }


}

