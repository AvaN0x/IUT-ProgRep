package commun;

public interface ITicTacToeListener extends java.rmi.Remote {
    /**
     * Notifie le lancement de la partie
     * 
     * @param estTonTour Indique si c'est au tour du joueur
     * @param numJoueur  Indique le numéro du joueur
     * @throws java.rmi.RemoteException
     */
    public void partieLancee(boolean estTonTour, Cellule numJoueur) throws java.rmi.RemoteException;

    /**
     * Notifie qu'un joueur a rejoint la partie
     * 
     * @throws java.rmi.RemoteException
     */
    public void joueurRejoindre() throws java.rmi.RemoteException;

    /**
     * Notifie qu'un joueur a quitté la partie
     * 
     * @throws java.rmi.RemoteException
     */
    public void joueurQuitter() throws java.rmi.RemoteException;

    /**
     * Notifie qu'une cellule du plateau à changée
     * 
     * @param x          Colonne de la celulle
     * @param y          Ligne de la cellule
     * @param status     Etat de la cellule
     * @param estTonTour Indique si c'est au tour du joueur
     * @throws java.rmi.RemoteException
     */
    public void celluleMAJ(int x, int y, Cellule status, boolean estTonTour) throws java.rmi.RemoteException;

    /**
     * Notifie qu'un joueur a gagné la partie
     * 
     * @param estGagnant Indique si le joueur est le gagnant
     * @throws java.rmi.RemoteException
     */
    public void aGagner(NullBool estGagnant) throws java.rmi.RemoteException;
}
