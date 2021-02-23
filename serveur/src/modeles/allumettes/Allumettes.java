package serveur.src.modeles.allumettes;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
        System.out.println("Allumettes >> Salons " + id + " ouvert.");
        return id;
    }

    @Override
    public boolean fermerSalon(UUID id) throws RemoteException {
        System.out.println("Allumettes >> Salons " + id + " fermé.");
        // On retire le salon de la Map et on vérifie que tout s'est bien passé
        return salons.remove(id) != null;
    }

    @Override
    public boolean jouer(UUID id, ArrayList<Integer> positions) throws RemoteException {
        if (salons.get(id).getNombreAllumettesRestantes() >= positions.size()) {
            for (int position : positions) {
                retirer(id, position);
            }
            salons.get(id).changerProchainJoueur();
            return true;
        }
        return false;
    }

    private void retirer(UUID id, int position) throws RemoteException {
        if (salons.get(id).getNombreAllumettesRestantes() > 0) {
            salons.get(id).retirer(position);
        }
    }

    @Override
    public void serveurJoue(UUID id) throws RemoteException {
        // TODO IA
        int position = salons.get(id).getAleatPosition();
        if (position != -1)
            retirer(id, position);

        salons.get(id).changerProchainJoueur();
    }

    @Override
    public int getNombreAllumettes(UUID id) throws RemoteException {
        return salons.get(id).getNombreAllumettesRestantes();
    }

    @Override
    public boolean isAuJoueurDeJouer(UUID id) throws RemoteException {
        return salons.get(id).isAuJoueurDeJouer();
    }

    @Override
    public boolean[] getAllumettesArray(UUID id) throws RemoteException {
        return salons.get(id).getAllumettesArray();
    }
}
