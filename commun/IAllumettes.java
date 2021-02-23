package commun;

import java.rmi.*;
import java.util.UUID;

public interface IAllumettes extends Remote {
    public static final int MAX_SELECTION = 2;

    public UUID nouveauSalon() throws RemoteException;

    public boolean fermerSalon(UUID id) throws RemoteException;

    public boolean retirer(UUID id, int[] positions) throws RemoteException;

    public void serveurJoue(UUID id) throws RemoteException;

    public int getNombreAllumettes(UUID id) throws RemoteException;

    public boolean isAuJoueurDeJouer(UUID id) throws RemoteException;

    public boolean[] getAllumettesArray(UUID id) throws RemoteException;
}
