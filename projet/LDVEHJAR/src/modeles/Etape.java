package modeles;

import java.util.*;

//un livre est composé d'une liste d'Etapes
public class Etape {

  protected LinkedList<Lien> lienEtapesPrecedentes;
  protected LinkedList<Lien> lienEtapesSuivantes;
  protected String texte;


  public Etape(){ //Etape est un paragraphe du livre
    this.lienEtapesSuivantes = new LinkedList<Lien>(); //liste des liens où this est l'étape mère
    this.lienEtapesPrecedentes = new LinkedList<Lien>(); //liste des liens où this est l'étape fille
    this.texte="";  // texte de l'etape
  }

  //retourne le lien où : this est l'etape mère et e est l'étape fille
  public Lien getLienMereFille(Etape e){
    for(Lien link : this.lienEtapesSuivantes){
      if(link.getFille().equals(e)){
        return link;
      }
    }
    return null;
  }

  //retourne le lien où : e est l'etape mère et this est l'étape fille
  public Lien getLienFilleMere(Etape e){
    for(Lien link : this.lienEtapesPrecedentes){
      if(link.getMere().equals(e)){
        return link;
      }
    }
    return null;
  }

  //accesseur de liste des liens où this est l'étape mère
  public LinkedList<Lien> getLienEtapeSuivantes(){
    return this.lienEtapesSuivantes;
  }

  //accesseur de liste des liens où this est l'étape fille
  public LinkedList<Lien> getLienEtapesPrecedentes(){
    return this.lienEtapesPrecedentes;
  }

  //retourne le texte de l'étape
  public String getTexte(){
    return this.texte;
  }

  //enregistre le texte donné dans this.texte
  public void redactionTexte(String content){
    this.texte = content;
  }

  //création et ajout du lien entre this et fille
  public void ajouterEtapeSuivante(LinkedList<Etape> listeEtape,Etape fille) {
    ajouterEtapeSuivante(listeEtape,listeEtape.indexOf(fille),fille);
  }

  public void ajouterEtapeSuivante(LinkedList<Etape> listeEtape, int oldnum ,Etape fille) {
    Lien link=new Lien(this,fille);
    this.lienEtapesSuivantes.add(link);
    fille.ajouterEtapePrecedente(link); //ajoute le lien a la fille
    link.updatedNumber(listeEtape, oldnum, fille);
  }

  public void ajouterEtapePrecedente(Lien et1) { // ajoute le lien a l'etape qui precede
    this.lienEtapesPrecedentes.add(et1);
  }

  //suppression du lien donné à l'étape suivante
  public void deleteEtapeSuivantes(Lien link){
    this.lienEtapesSuivantes.remove(link);

  }
  
  public void deleteEtapeSuivantes(Livre book,Lien link){

    deleteEtapeSuivantes(link);
    link.deleteNumber(book.getNumEtape(link.getFille()));

  }

  //suppression du lien donné à l'étape précédente
  public void deleteEtapePrecedentes(Lien link){
    this.lienEtapesPrecedentes.remove(link);
  }

  public void deleteEtapePrecedentes(Livre book,Lien link){
    link.deleteNumber(book.getNumEtape(this));
    deleteEtapePrecedentes(link);
  }

}
