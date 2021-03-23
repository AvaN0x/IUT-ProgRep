package commun;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

public interface ITicTacToe extends Remote {
    /**
     * Créer un nouveau salon avec <code>listener</code> comme joueur par défaut
     * 
     * @param listener Le client qui recevra les évenements du salon
     * @return L'UUID du salon
     * @throws RemoteException
     */
    public UUID nouveauSalon(ITicTacToeListener listener) throws RemoteException;

    /**
     * Fait rejoindre le <code>listener</code> au salon désigné par
     * <code>salonId</code>
     * 
     * @param salonId  L'UUID du salon
     * @param listener Le client qui recevra les évenements du salon
     * @return Si tout c'est bien passé
     * @throws RemoteException
     */
    public boolean rejoindreSalon(UUID salonId, ITicTacToeListener listener) throws RemoteException;

    /**
     * Fait quitter le <code>listener</code> au salon désigné par
     * <code>salonId</code> <br/>
     * <br/>
     * Si le salon est vide, le supprime
     * 
     * @param salonId  L'UUID du salon
     * @param listener Le client qui recevais les évenements du salon
     * @return Si tout c'est bien passé
     * @throws RemoteException
     */
    public boolean quitterSalon(UUID salonId, ITicTacToeListener listener) throws RemoteException;

    /**
     * Inidique au salon désigné par <code>salonId</code> que le joueur
     * <code>listener</code> à joué au coordonnées <code>x</code>, <code>y</code>
     * 
     * @param salonId  L'UUID du salon
     * @param x        Colonne de la celulle
     * @param y        Ligne de la cellule
     * @param listener Le client qui vient de jouer
     * @return Si tout c'est bien passé
     * @throws RemoteException
     */
    public boolean jouer(UUID salonId, int x, int y, ITicTacToeListener listener) throws RemoteException;

    /**
     * Récupère les noms de tout les salons
     * 
     * @return les noms de tout les salons
     * @throws RemoteException
     */
    public Map<String, UUID> recupererNoms() throws RemoteException;
}
