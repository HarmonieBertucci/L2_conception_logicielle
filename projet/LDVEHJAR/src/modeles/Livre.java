package modeles;
import java.io.*;
import java.util.*;
import java.util.Collections;

public class Livre implements Serializable {
  protected String nom;
  protected LinkedList<Etape> listeEtapes;
  protected LinkedList<Etape> listeEtapesChemin;
  protected Etape firstEtape;
  protected LinkedList<Etape> listeEtapesFinal;
  protected int cheminSolution;
  protected int cheminPossible;

  public Livre(String nom) {
    this.nom = nom; // nom du livre
    this.listeEtapes = new LinkedList<Etape>(); // liste des étapes/pararaphes du livre
    this.listeEtapesChemin= new LinkedList<Etape>(); // liste des etape qui permettent d'acceder a la fin
    this.listeEtapesFinal= new LinkedList<Etape>(); //liste des étapes considérées comme de "bonnes fins"
    this.cheminPossible=0; //nombre total de chemins empruntable dans le livre
    this.cheminSolution=0; //nombre de chemins empruntable dans le livre qui mènent a une étape finale/solution
  }

  //accesseur pour le nom du livre
  public String getName() {
    return this.nom;
  }

  // retourne l'etape de numero i
  public Etape getEtape(int i) {
    return this.listeEtapes.get(i);
  }

  // retorune la liste des etapes
  public LinkedList<Etape> getListeEtapes() {
    return this.listeEtapes;
  }

  //retourne le rang de l'étape selon sa place dans la liste
  public Integer getNumEtape(Etape x) {
    return this.listeEtapes.indexOf(x);
  }

  //retourne la première étape
  public Etape getFirstEtape(){
    return this.firstEtape;
  }

  //définir l'étape donnée comme "première étape"
  public void setPremiereEtape(Etape etape1) { // on definit la premiere etape
    this.firstEtape= etape1;
  }

  //récupérer la liste des étapes finales
  public LinkedList<Etape> getEtapeFinal(){
    return this.listeEtapesFinal;
  }

  //ajoute l'étape donnée à la liste des "dernières étapes"
  public void setEtapeFinal(Etape etape){
    this.listeEtapesFinal.add(etape);
  }

  //retourne la liste des étapes qui permettent d'acceder a la fin
  public LinkedList<Etape> getListeEtapesChemin(){
    return this.listeEtapesChemin;
  }

  //retourne le nombre de chemins empruntable dans le livre qui mènent a une étape finale/solution
  public int getNumCheminSolution(){
    return this.cheminSolution;
  }

  //retourne le nombre total de chemins empruntable dans le livre
  public int getNumCheminPossible(){
    return this.cheminPossible;
  }

  //définit la liste des étapes par celle donnée (utilisé dans shuffle)
  public void setListeEtapes(LinkedList<Etape> newListeEtapes){
    this.listeEtapes= newListeEtapes;
  }

  //permet de renomer le livre
  public void setNom(String name){
    this.nom = name;
  }

  // creation d'une etape
  public Etape creerEtape() {
    Etape etapeCree = new Etape(); //création de l'étape
    this.listeEtapes.add(etapeCree); //ajout de l'étape créé dans la liste des étapes du livre
    return etapeCree;
  }

  //supprimer une étape grace à l'Etape
  public void supprimerEtape(Etape etapeASuprimmer) {

    int numero = getNumEtape(etapeASuprimmer);
    for (Lien link : etapeASuprimmer.getLienEtapesPrecedentes()) { // suppression de tout lien avec les autres etapes
      link.getMere().deleteEtapeSuivantes(this,link);

    }
    for (Lien link : etapeASuprimmer.getLienEtapeSuivantes()) {
      link.getFille().deleteEtapePrecedentes(this,link);
    }
    this.listeEtapes.remove(etapeASuprimmer);
    Etape etape;
    for (int i = numero; i < this.listeEtapes.size(); i++) {
      etape = getEtape(i);
      for (Lien link : etape.getLienEtapesPrecedentes()) {
        link.updatedNumber(this.listeEtapes, i+1, getEtape((i)));
      }

    }
    this.listeEtapesFinal.remove(etapeASuprimmer);
  }

  //supprimer une étape grace a son numéro
  public void supprimerEtape(int numero) { //on supprime l'etape de la liste des etapes
    Etape etapeASuprimmer = this.listeEtapes.get(numero);
    supprimerEtape(etapeASuprimmer);
  }

//pour l'étape donnée, parcours la liste de ses "mères/étapes précédentes" et les ajoutes a la liste des étapes permettent d'acceder a la fin
// ce qui permet d'avoir toutes les étapes permettant d'acceder a l'étape voulue
// le compteur du nombre de chemin pour arriver a une étape solution s'incrémente a chaque fois qu'on tombe sur la "première étape"
// permet donc de connaitre le nombre de chemins empruntables dans le livre pour arriver sur une étape solution
  public void setCheminRecursif(Etape etape){
    for(Lien link : etape.getLienEtapesPrecedentes()){
      if(!this.listeEtapesChemin.contains(link.getMere())){
        this.listeEtapesChemin.add(link.getMere());
      }
      if(link.getMere().equals(this.firstEtape)){
        this.cheminSolution++;
      }
      setCheminRecursif(link.getMere());
    }
  }

  //pour chaque étape solution, setchemin appelle setCheminRecursif
  public void setChemin(){
    if(getFirstEtape()!=null){
      this.cheminSolution=0;
      this.listeEtapesChemin= new LinkedList<Etape>();
      for(Etape etape : this.listeEtapesFinal) {
        setCheminRecursif(etape);
      }
    }

  }

  //pour une étape donnée(n'ayant pas d'étape suivante), remonte par récursivité toutes ses étapes précédentes jusqu'à arriver a la "première étape"
  // permet de connaitre le nombre de chemins empruntables dans le livre
  public void parcourCheminRecursif(Etape etape) {
    for (Lien link : etape.getLienEtapesPrecedentes()) {
      if (link.getMere().equals(this.firstEtape)) {
        this.cheminPossible++;
      }
      parcourCheminRecursif(link.getMere());
    }
  }

  //pour chaque étape n'ayant pas d'étape suivante parcourChemin appelle parcourCheminRecursif
  public void parcourChemin(){
    if(getFirstEtape()!=null){
      this.cheminPossible=0;
      for(Etape etape : this.listeEtapes){
        if(etape.lienEtapesSuivantes.size()== 0){
          parcourCheminRecursif(etape);

        }
      }
    }

  }

  //retourne un livre avec les étapes mélangées
  public Livre shuffleLivre () { //melange des etapes du livre
    Livre newLivre= new Livre(this.nom);

    if(this.listeEtapes.size()>1) {
      LinkedList<Etape> newListeEtape = new LinkedList<>();
      newListeEtape.addAll(getListeEtapes());
      newListeEtape.remove(0);
      Collections.shuffle(newListeEtape);
      newListeEtape.addFirst(this.listeEtapes.get(0));
      newLivre.setListeEtapes(newListeEtape);
      return newLivre;
    }else{
      return this; }
  }

  public void test() {
    ObjectOutputStream oos = null;

    try {
      final FileOutputStream fichier = new FileOutputStream("LivreTestLien.ser");
      oos = new ObjectOutputStream(fichier);
      // ...
    } catch (final java.io.IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (oos != null) {
          oos.flush();
          oos.close();
        }
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    }

  }


}