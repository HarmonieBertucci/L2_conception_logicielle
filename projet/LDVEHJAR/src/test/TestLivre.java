package test;
import modeles.*;

import java.io.*;
import java.net.URL;


public class TestLivre {
    private Livre livreRef;
    private Livre livreTest;
    public TestLivre(){

    }

    public boolean addTest(){
        this.livreTest = new Livre("");
        System.out.println();
        recuperationRef("/resources/livreAddTest.txt");
        for(int i = 0 ; i<10;i++ ) {
            this.livreTest.creerEtape();
        }
        return this.livreRef.getListeEtapes().size() == this.livreTest.getListeEtapes().size();
    }

    public boolean setTextTest(){
        recuperationRef("/resources/livreSetTextTest.txt");
        for(int i = 0; i<10 ; i++){
            this.livreTest.getEtape(i).redactionTexte("etape"+i+"\n");
        }
        int j = 0;
        while(j<10 && this.livreRef.getEtape(j).getTexte().equals( this.livreTest.getEtape(j).getTexte())){
            j++;

        }

        return j==10;
    }

    public boolean deleteEtapeTest(){
        recuperationRef("/resources/livreDeleteEtapeTest.txt");
        for(int i = 1; i<10 ; i++) {
            this.livreTest.supprimerEtape(1);
        }
        return this.livreRef.getListeEtapes().size() == this.livreTest.getListeEtapes().size();
    }

  public boolean linkTest(){
        recuperationRef("/resources/livreLinkTest.txt");
        this.livreTest.creerEtape();
        this.livreTest.getEtape(0).ajouterEtapeSuivante(this.livreTest.getListeEtapes(),this.livreTest.getEtape(1));
        return this.livreTest.getEtape(0).getLienEtapeSuivantes().size() == this.livreRef.getEtape(0).getLienEtapeSuivantes().size();
  }

  public boolean delinkTest(){
      recuperationRef("/resources/livreDelinkTest.txt");
      this.livreTest.getEtape(0).deleteEtapeSuivantes(this.livreTest.getEtape(1).getLienEtapesPrecedentes().get(0));
      return this.livreTest.getEtape(0).getLienEtapeSuivantes().size() == this.livreRef.getEtape(0).getLienEtapeSuivantes().size();
    }

  public boolean firstTest(){
        recuperationRef("/resources/livreFirstTest.txt");
        this.livreTest.setPremiereEtape(this.livreRef.getEtape(0));
        return this.livreTest.getFirstEtape().equals(this.livreRef.getFirstEtape());
  }

  public boolean lastTest(){
      recuperationRef("/resources/livreLastTest.txt");
      this.livreTest.creerEtape();
      this.livreTest.creerEtape();
      this.livreTest.creerEtape();
      for(int i=1;i<5;i++ ){
          this.livreTest.setEtapeFinal(this.livreTest.getEtape(i));
      }
      return this.livreTest.getEtapeFinal().size() == this.livreRef.getEtapeFinal().size();

  }
    public void recuperationRef(String dataRef){
        FileReader monFichier = null;
        BufferedReader tampon = null; // Rendre accessible le texte rapidement / Facilite l'accès aux données
        String texte = "";
        int currentnumber = 1;
        URL url = null;
        try {
           url = new URL("jar:file:" + getClass().getResource(dataRef).toString().substring(9));

        }catch (Exception e){

            e.printStackTrace();
        }

        try {
            // Lecture du fichier pour création des étapes / récupération du contenu
            InputStream in = url.openStream();
            tampon = new BufferedReader(new InputStreamReader(in));
            String ligne = tampon.readLine(); // On lis les lignes

            if (ligne == null ){
                this.livreRef = new Livre("");

            } else {
                if (ligne != null) { // Si y'a du texte / Document non vide

                    this.livreRef = new Livre(ligne.substring(16, ligne.length())); // Récupération / Création du nom du livre
                    ligne = tampon.readLine();

                    while (ligne!= null &&!ligne.contains("~~~~~~~") /*&& (ligne!=null)*/) {  // Si la ligne actuelle n'est pas un fin de document

                        if (ligne.contains("Numero de l'etape :")) { // Si elle contient un début d'étape

                            this.livreRef.creerEtape();
                            currentnumber = Integer.parseInt(ligne.substring(19, ligne.length())); // Récupération de son numéro
                            ligne = tampon.readLine();
                            while (ligne!=null && !ligne.contains("µµµµµµµ")) { // Tant que la ligne n'est pas une fin d'étape
                                texte += ligne + "\n";
                                ligne = tampon.readLine();

                            } // On sort de l'étape
                            this.livreRef.getEtape(currentnumber).redactionTexte(texte); // Enregistrement du contenu de l'etape
                            texte = ""; // reset de la variable
                        } else if (ligne.contains("Premiere Etape :")) {
                            this.livreRef.setPremiereEtape(this.livreRef.getEtape(Integer.parseInt(ligne.substring(16))));
                        } else if (ligne.contains("Dernières Etape : ")) {
                            ligne = tampon.readLine();
                            while (!ligne.contains("~~~~~")) {
                                this.livreRef.setEtapeFinal(this.livreRef.getEtape(Integer.parseInt(ligne)));
                                ligne = tampon.readLine();
                            }
                        }
                        ligne = tampon.readLine();// Fin du while
                    }
                }
                // Relecture du fichier pour les liens entre étapes
                in = url.openStream();
                tampon = new BufferedReader(new InputStreamReader(in));
                ligne = tampon.readLine();

                while (ligne != null) {
                    if (ligne.contains("Numero de l'etape :")) {
                        currentnumber = Integer.parseInt(ligne.substring(19, ligne.length())); //get numéro de l'étape actuelle
                    } else if (ligne.contains("${")) {
                        this.livreRef.getEtape(currentnumber).ajouterEtapeSuivante(this.livreRef.getListeEtapes(), this.livreRef.getEtape(Integer.parseInt(ligne.substring(ligne.indexOf("${") + 2, ligne.indexOf("}")))));
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
