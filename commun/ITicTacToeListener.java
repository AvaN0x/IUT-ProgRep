package commun;

public interface ITicTacToeListener extends java.rmi.Remote {
    public void partieLancee(boolean estTonTour, Cellule numJoueur) throws java.rmi.RemoteException;

    public void joueurRejoindre() throws java.rmi.RemoteException;

    public void joueurQuitter() throws java.rmi.RemoteException;

    public void celluleMAJ(int x, int y, Cellule status, boolean estTonTour) throws java.rmi.RemoteException;

    public void aGagner(NullBool estGagnant) throws java.rmi.RemoteException;
}
