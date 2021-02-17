package commun;

import java.rmi.*;

public interface IAllumettesJeux extends Remote {
    public void lancerPartie() throws RemoteException;

    public void retirer(int quantite) throws RemoteException;

    public int getAllumettesRestantes() throws RemoteException;

    public void setAllumettesRestantes(int allumettesRestantes) throws RemoteException;
}
