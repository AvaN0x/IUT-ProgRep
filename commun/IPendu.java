package commun;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

public interface IPendu extends Remote {
    public static int MAX_VIE = 6;

    /**
     * Ajoute un nouveau salon à la Map
     * 
     * @return l'id du salon ou {@code null} si erreur
     * @throws RemoteException
     */
    public UUID nouveauSalon() throws RemoteException;

    /**
     * Récupère le nombre de lettre du mot d'un salon
     * 
     * @param id l'id du salon
     * @return le nombre de lettres
     * @throws RemoteException
     */
    public int recupNbLettres(UUID id) throws RemoteException;

    /**
     * Récupère des indices pour le joueur d'un salon
     * 
     * @param id l'id du salon
     * @return Une Map avec la position de la lettre et la lettre
     * @throws RemoteException
     */
    public Map<Character, java.util.ArrayList<Integer>> recupIndice(UUID id) throws RemoteException;

    /**
     * Vérifie si une lettre fait partie du mot d'un salon
     * 
     * @param id     l'id du salon
     * @param lettre la lettre donnée par le joueur
     * @return la nouvelle vie du joueur et la position de la lettre (-1 si elle
     *         n'existe pas)
     * @throws RemoteException
     */
    public PenduResultat envoiLettre(UUID id, char lettre) throws RemoteException;

    /**
     * Retire un salon de la Map
     * 
     * @param id l'id du salon
     * @return si la fermeture s'est bien passée
     * @throws RemoteException
     */
    public boolean fermerSalon(UUID id) throws RemoteException;
}
