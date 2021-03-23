package commun;

import java.rmi.*;
import java.util.ArrayList;
import java.util.UUID;

public interface IAllumettes extends Remote {
    public static final int JOUEUR = 1;
    public static final int SERVEUR = JOUEUR + 1;

    /**
     * Ajoute un nouveau salon à la Map
     * 
     * @return l'id du salon ou {@code null} si erreur
     * @throws RemoteException
     */
    public UUID nouveauSalon() throws RemoteException;

    /**
     * Retire un salon de la Map
     * 
     * @param id l'id du salon
     * @return si la fermeture s'est bien passée
     * @throws RemoteException
     */
    public boolean fermerSalon(UUID id) throws RemoteException;

    /**
     * Permet de jouer une liste de position
     * 
     * @param id        id du salon
     * @param positions liste de positions
     * @return si les positions ont bien pu etre jouée
     * @throws RemoteException
     */
    public boolean jouer(UUID id, ArrayList<Integer> positions) throws RemoteException;

    /**
     * Fait jouer le serveur
     * 
     * @param id id du salon
     * @return le nombre d'allumettes que le serveur a prit
     * @throws RemoteException
     */
    public int serveurJoue(UUID id) throws RemoteException;

    /**
     * Récupère le nombre d'allumettes encore disponible sur le plateau
     * 
     * @param id
     * @return
     * @throws RemoteException
     */
    public int getNombreAllumettes(UUID id) throws RemoteException;

    /**
     * Récupère le tableau de toutes les allumettes, pour savoir les quelles sont
     * prise ou non
     * 
     * @param id id du salon
     * @return tableau de booléens
     * @throws RemoteException
     */
    public boolean[] getAllumettesArray(UUID id) throws RemoteException;

    /**
     * Permet de savoir le nombre d'allumettes que le joueur possède
     * 
     * @param id id du salon
     * @return nombre d'allumettes
     * @throws RemoteException
     */
    public int getNombreAllumettesJoueur(UUID id) throws RemoteException;

    /**
     * Permet de savoir le nombre d'allumettes que le serveur possède
     * 
     * @param id id du salon
     * @return nombre d'allumettes
     * @throws RemoteException
     */
    public int getNombreAllumettesServeur(UUID id) throws RemoteException;

    /**
     * Savoir qui du joueur ou du serveur commence
     * 
     * @param id id du salon
     * @return joueur ou serveur
     * @throws RemoteException
     */
    public int quiCommence(UUID id) throws RemoteException;
}
