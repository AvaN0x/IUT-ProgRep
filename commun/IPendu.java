package commun;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface IPendu extends Remote {
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
     * Vérifie si une lettre fait partie du mot d'un salon
     * 
     * @param id     l'id du salon
     * @param lettre la lettre donnée par le joueur
     * @return la nouvelle vie du joueur
     * @throws RemoteException
     */
    public int envoiLettre(UUID id, char lettre) throws RemoteException;

    /**
     * Retire un salon de la Map
     * 
     * @param id l'id du salon
     * @return si la fermeture s'est bien passée
     * @throws RemoteException
     */
    public boolean fermerSalon(UUID id) throws RemoteException;
}
