package commun;

public interface ITicTacToeListener extends java.rmi.Remote {
    public void partieLancee() throws java.rmi.RemoteException;

    public void joueurRejoindre() throws java.rmi.RemoteException;

    public void joueurQuiter() throws java.rmi.RemoteException;

    public void celluleMAJ(int x, int y, Cellule status) throws java.rmi.RemoteException;
}
