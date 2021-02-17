package serveur.src.modeles.allumettes;

import java.rmi.*;
import java.rmi.server.*;

public class AllumettesJeux extends UnicastRemoteObject implements IAllumettesJeux {
    private int allumettesRestantes; // TODO use array

    public AllumettesJeux() throws RemoteException {
        super();
    }

    public void lancerPartie() throws RemoteException {
        allumettesRestantes = 20;
    }

    public void retirer(int quantite) throws RemoteException {
        if (allumettesRestantes >= quantite)
            allumettesRestantes -= quantite;
    }

    public int getAllumettesRestantes() throws RemoteException {
        return this.allumettesRestantes;
    }

    public void setAllumettesRestantes(int allumettesRestantes) throws RemoteException {
        this.allumettesRestantes = allumettesRestantes;
    }

}
