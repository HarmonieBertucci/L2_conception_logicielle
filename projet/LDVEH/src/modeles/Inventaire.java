package modeles;

import java.util.ArrayList;
import java.util.*;

public class Inventaire {
    //dictionnaire contenant le nom de l'objet et une liste contenant la liste des étapes où il est récupérable
    // et la liste des liens où l'objet est indispensable pour passer de l'étape mère a l'étape fille
    protected Map<String,ArrayList<ArrayList>> listeEditeur;

    public Inventaire(){
        this.listeEditeur = new HashMap<String,ArrayList<ArrayList>>();
    }

    //permet de récupérer l'inventaire (dictionnaire d'objets)
    public Map<String,ArrayList<ArrayList>> getListeEditeur(){
        return this.listeEditeur;
    }

    //permet de récupérer la liste des étapes où l'objet est récupérable
    public ArrayList<Etape> getListeEtapeRecup(String nameObject){
        return this.listeEditeur.get(nameObject).get(0);
    }

    //permet de récupérer la liste des liens où l'objet est indispensable pour passer de l'étape mère a l'étape fille
    public ArrayList<Lien> getListeEtapeUti(String nameObject){
        return this.listeEditeur.get(nameObject).get(1);
    }

    //permet d'ajouter un objet à l'inventaire/au dictionnaire
    public void addObject(String nameObject){
        ArrayList<ArrayList> listeetape= new ArrayList<ArrayList>();
        listeetape.add(new ArrayList<Etape>());
        listeetape.add(new ArrayList<Lien>());
        this.listeEditeur.put(nameObject,listeetape);
    }

    //permet d'ajouter une étape à la liste des étapes où l'objet est récupérable
    public void addEtapeRecup(String object , ArrayList<Etape> e){
        if(this.listeEditeur.containsKey(object) && !this.listeEditeur.get(object).get(0).contains(e)) {
            this.listeEditeur.get(object).remove(0);
            this.listeEditeur.get(object).add(0,e);

        }
    }

    //permet d'ajouter un lien à la liste des liens où l'objet est indispensable pour passer de l'étape mère a l'étape fille
    public void addEtapeUtilisation(String object , ArrayList<Lien> e){
        if(this.listeEditeur.containsKey(object) && !this.listeEditeur.get(object).get(1).contains(e)) {
            this.listeEditeur.get(object).remove(1);
            this.listeEditeur.get(object).add(1,e);
        }
    }

}
