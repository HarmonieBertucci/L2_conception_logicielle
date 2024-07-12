package application;
import modeles.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class Lecture extends JFrame {
    protected Livre livreEditeur;
    protected Livre livreShuffle;
    protected int numEtapeEnCours ; //Numéro de l'etape qu'on est en train de visualiser
    protected  JTextArea textePrincipal ; //champ principal où on met le texte de l'étape visualisée
    protected JTextField textAreaNum; //champ où l'utilisateur peut entrer un numéro d'étape pour se déplacer plus vite pour aller à l'étape suivante choisie
    protected Inventaire inventaire; //inventaire lié au livre
    protected Map<String,Boolean> verificationObject; // dictionnaire qui verifie si l'utilisateur a recupéré les objets

    public Lecture(Livre livre, Inventaire inventaire){
        super("Lecture");
        this.livreEditeur=livre;
        this.livreShuffle = livre.shuffleLivre();
        this.numEtapeEnCours=0;
        this.inventaire=inventaire;
        this.textePrincipal=new  JTextArea  ();
        this.verificationObject = new HashMap<String,Boolean>();

        //ajoute au dictionnaire qui verifie si l'utilisateur a recupéré les objets tous les objets de l'inventaire
        for(String name : this.inventaire.getListeEditeur().keySet()){
            this.verificationObject.put(name,false);
        }

        this.textePrincipal.setWrapStyleWord(true);//permet le saut à la ligne pour qu'une ligne de texte ne s'écrive pas sur une seule ligne (pas très agréable à lire)
        this.textePrincipal.setLineWrap(true);

        //pour que l'utilisateur ne puisse pas éditer le texte visualisé
        this.textePrincipal.setEditable(false);

        JPanel contentPane = new JPanel(); //Panel contenant tous les elements pour les placer correctement
        contentPane.setLayout(new BorderLayout());

        JScrollPane jScrollPane = new JScrollPane(this.textePrincipal); //on met le texte principal dans un scrollPane
        contentPane.add(jScrollPane, BorderLayout.CENTER); //ajout du scrollPane au contenant principal

        JPanel panelNavigation = new JPanel(); //Panel pour aligner correctement les boutons et le champ pour naviguer parmi les étapes
        panelNavigation.setLayout(new BorderLayout());

        JButton preced=new JButton("<"); //bouton de gauche pour aller à l'étape (numérique) précedente
        preced.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(getNumEtapeEnCours()>=1){
                        affichageEtapes(getNumEtapeEnCours() - 1);
                    }
                }
            });
        panelNavigation.add(preced, BorderLayout.LINE_START); //ajout du bouton au début duPanel de navigation

        JButton suiv=new JButton(">"); //bouton de droite pour aller à l'étape (numérique) suivante
        suiv.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(getNumEtapeEnCours()+1<getLivre().getListeEtapes().size()){
                            affichageEtapes(getNumEtapeEnCours() + 1);
                        }
                    }
                });
        
        panelNavigation.add(suiv, BorderLayout.LINE_END); //ajout du bouton a la fin du Panel de navigation

        JPanel panelNumeroEtape = new JPanel(); //panel ou se situent le champ d'entrée du numero d'etape

        this.textAreaNum=new JTextField ();
        this.textAreaNum.setText(String.valueOf(this.numEtapeEnCours)); //on met le numéro de l'étape qu'on visualise dans le champ
        this.textAreaNum.setBackground(Color.ORANGE); //on met le background du champ en orange pour une visualisation plus agréable
        this.textAreaNum.setPreferredSize(new Dimension(25, 18));
        panelNumeroEtape.add(this.textAreaNum, BorderLayout.LINE_START); //ajout du champ au panel


        JButton go=new JButton("GO"); //bouton ou cliquer pour aller a l'etape entrée dans le champ précedent
        go.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String text=textAreaNum.getText(); //recupere le texte du champ
                        int text2int;
                        String nameObject=null ;
                        if(isInteger(text) ){ //si le texte est bien un entier
                            text2int=Integer.parseInt(text);
                            if ((text2int < getLivre().getListeEtapes().size()) && (text2int >= 0)) { //si l'entier est bien un numero d'étape valide (si l'étape XX existe vraiment)

                                for(String name : getVerifObject().keySet()){ // verifie si l'etape est accessible avec ou sans objet
                                    if(getInventaire().getListeEtapeUti(name).contains(getLivre().getEtape(getNumEtapeEnCours()).getLienMereFille(getLivre().getEtape(text2int)))){
                                        nameObject=name;
                                    }
                                }

                                if(nameObject !=null ) { //si un objet est lié à l'étape que l'on veut visualiser
                                    if ( getVerifObject().get(nameObject)) { //si l'objet a été récupéré
                                        affichageEtapes(text2int);
                                    }
                                    else{ //si l'objet n'a pas été récupéré
                                        afficheErreur(nameObject);
                                    }
                                }
                                else{//si aucun objet n'est lié à l'étape que l'on veut visualiser
                                    affichageEtapes(text2int);
                                }
                            }
                        }
                    }
                });

        panelNumeroEtape.add(go, BorderLayout.LINE_END);//ajout du bouton au panel

        panelNavigation.add(panelNumeroEtape, BorderLayout.CENTER); //ajout du panel au panel de navigation

        contentPane.add(panelNavigation, BorderLayout.PAGE_END); //ajout du panel de navigation en bas du panel principal

        JFrame frame = new JFrame();
        frame.setTitle("Lecture de "+livre.getName()); //nom du mode Lecture
        frame.setSize(800, 500); //taille de la fenètre quand elle n'est pas en plein écran
        frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE); //permet de fermer le mode lecture sans fermer l'éditeur

        //si le livre comporte au moins une étape la premiere étape est affichée, sinon une fenetre de dialogue s'ouvre pour mettre en garde l'utilisateur
        if(this.livreShuffle.getListeEtapes().size()>0){
            affichageEtapes(numEtapeEnCours);
        }
        else{
            JDialog d = new JDialog(frame, "Attention !");
            JLabel l = new JLabel("Le livre sélectionné est vide.");
            d.add(l);
            d.setSize(300, 100);
            d.setVisible(true);
        }

        //ajout de conteneur principal a la frame
        frame.setContentPane(contentPane);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); //permet de mettre la fenetre en plein écran
        frame.setVisible(true);

    }

    //donne le numéro de l'étape en train d'être visionnée
    public int getNumEtapeEnCours(){ //accesseur pour le numéro de l'étape qu'on visualise
        return this.numEtapeEnCours;
    }

    //donne le livre avec les étapes mélangées
    public Livre getLivre(){ //accesseur pour le livre mélangé
        return this.livreShuffle;
    }

    //accesseur de verificationObject
    public Map<String,Boolean> getVerifObject(){
        return this.verificationObject;
    }

    //accesseur pour l'inventaire
    public  Inventaire getInventaire(){
        return this.inventaire;
    }

    //méthode permétant d'afficher le text de l'étape visualisée dans le champ principal
    public void affichageEtapes(int numEtape) {
        this.numEtapeEnCours = numEtape;
        //on récupère le texte de l'étape
        Etape etape = this.livreShuffle.getEtape(numEtape);
        String newText = ""+etape.getTexte();
        String inventaire = "Inventaire : \n";
        int oldNum, newNum;
        if (etape.getLienEtapeSuivantes() != null) {
            for (Lien link : etape.getLienEtapeSuivantes()) {
                oldNum = this.livreEditeur.getNumEtape(link.getFille());
                newNum = this.livreShuffle.getNumEtape(link.getFille());
                //on remplace les numeros d'étapes notées ${XX} par XX dans le texte
                newText = newText.replace("${" + oldNum + "}", "" + newNum);

            }
            if(this.verificationObject.keySet().size()>0){
                for(String key : this.verificationObject.keySet()){
                    if(this.verificationObject.get(key)){
                        inventaire= inventaire + key +"\n";
                    }
                }
            }
        }
        newText = newText + "\n" +inventaire;
        //ajout du numero de l'étape dans le champs de navigation
        this.textAreaNum.setText(String.valueOf(numEtape));

        //ajout du texte de l'étape modifié dans le champs principal
        this.textePrincipal.setText(newText);

        //parcours la liste des objets pour verifier si il y a pas d'objet a recuperer dans cette etape
        for (String name : getVerifObject().keySet()) {
            if (getInventaire().getListeEtapeRecup(name).contains(getLivre().getEtape(getNumEtapeEnCours()))) {
                //objet recupéré et donc marqué comme recupéré dans le dictionnaire
                this.verificationObject.replace(name, true);
            }
        }
    }

    public void afficheErreur(String name){
        this.textePrincipal.setText(this.textePrincipal.getText() + "\n" + "Vous ne pouvez pas faire ce choix il vous manque l'objet:" + name) ;
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



}
