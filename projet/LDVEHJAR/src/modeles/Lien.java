package modeles;

import java.util.*;


public class Lien { // classe de lien qui permet de joindre deux etapes
  protected Etape mere; // etape ayant comme étape suivante l'étape fille
  protected Etape fille;

  public Lien(Etape mere, Etape fille) {
    this.mere = mere;
    this.fille = fille;

  }

  //accesseur pour la mère
  public Etape getMere() {
    return this.mere;
  }

  //accesseur pour la fille
  public Etape getFille() {
    return this.fille;
  }

  //est appelé quand on supprime une étape liée / remplace (dans le texte de l'étape mère) le numéro de l'étape fille par "/!\"
  public void deleteNumber(int numEtape) {
    this.mere.redactionTexte(this.mere.getTexte().replace("${" + numEtape + "}", " /!\\ "));
  }

  //modifie le numero de l'étape fille dans le texte de l'étape mère
  public void updatedNumber( int oldNum, int newNum) {
    if (this.mere.getTexte().contains("${" + oldNum)) {
      this.mere.redactionTexte(this.mere.getTexte().replace("${" + oldNum + "}", "${" + newNum + "}"));
    } else {
      this.mere.redactionTexte(this.mere.getTexte() + " \n" + "Allez à l'etape ${" + newNum+ "} pour : ");
    }
  }
  public void updatedNumber(LinkedList<Etape> listeEtape, int oldNum, Etape newNum) {
    updatedNumber(oldNum,listeEtape.indexOf(newNum));
  }

 }
