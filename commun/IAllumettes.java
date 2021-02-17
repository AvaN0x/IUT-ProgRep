package commun;

import java.rmi.*;
import java.util.UUID;

public interface IAllumettes extends Remote {
    public UUID nouveauSalon() throws RemoteException;

    public boolean fermerSalon(UUID id) throws RemoteException;

    public void retirer(UUID id, int quantite) throws RemoteException;

    public void serveurJoue(UUID id) throws RemoteException;

    public int getNombreAllumettes(UUID id) throws RemoteException;

    public boolean isAuJoueurDeJouer(UUID id) throws RemoteException;

}
