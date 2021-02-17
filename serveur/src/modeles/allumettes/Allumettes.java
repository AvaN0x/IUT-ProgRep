package serveur.src.modeles.allumettes;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.UUID;

import commun.IAllumettes;

public class Allumettes extends UnicastRemoteObject implements IAllumettes {
    private static final long serialVersionUID = 5416415132654321564L;
    private HashMap<UUID, AllumettesInstance> salons;

    public Allumettes() throws RemoteException {
        super();
        salons = new HashMap<>();
    }

    @Override
    public UUID nouveauSalon() throws RemoteException {
        UUID id = UUID.randomUUID(); // Génère un ID
        salons.put(id, new AllumettesInstance()); // Le rajoute à la Map des salons
        return id;
    }

    @Override
    public boolean fermerSalon(UUID id) throws RemoteException {
        // On retire le salon de la Map et on vérifie que tout s'est bien passé
        return salons.remove(id) != null;
    }

    @Override
    public void retirer(UUID id, int quantite) throws RemoteException {
        if (salons.get(id).getAllumettesRestantes() >= quantite)
            salons.get(id).retirer(quantite);
    }

    @Override
    public void serveurJoue(UUID id) throws RemoteException {
        // TODO IA
        retirer(id, 1);
    }

    @Override
    public int getNombreAllumettes(UUID id) throws RemoteException {
        return salons.get(id).getAllumettesRestantes();
    }

    @Override
    public boolean isAuJoueurDeJouer(UUID id) throws RemoteException {
        return salons.get(id).isAuJoueurDeJouer();
    }
}
