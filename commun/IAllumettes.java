package commun;

import java.rmi.*;
import java.util.ArrayList;
import java.util.UUID;

public interface IAllumettes extends Remote {
    public static final int JOUEUR = 1;
    public static final int SERVEUR = JOUEUR + 1;

    public UUID nouveauSalon() throws RemoteException;

    public boolean fermerSalon(UUID id) throws RemoteException;

    public boolean jouer(UUID id, ArrayList<Integer> positions) throws RemoteException;

    public int serveurJoue(UUID id) throws RemoteException;

    public int getNombreAllumettes(UUID id) throws RemoteException;

    public boolean[] getAllumettesArray(UUID id) throws RemoteException;

    public int getNombreAllumettesJoueur(UUID id) throws RemoteException;

    public int getNombreAllumettesServeur(UUID id) throws RemoteException;

    public int quiCommence(UUID id) throws RemoteException;
}
