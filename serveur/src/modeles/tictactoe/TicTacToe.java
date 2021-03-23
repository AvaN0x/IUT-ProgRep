package serveur.src.modeles.tictactoe;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import commun.ITicTacToe;
import commun.ITicTacToeListener;

public class TicTacToe extends UnicastRemoteObject implements ITicTacToe {
    private HashMap<UUID, TicTacToeInstance> salons;

    public TicTacToe() throws RemoteException {
        salons = new HashMap<UUID, TicTacToeInstance>();
    }

    @Override
    public UUID nouveauSalon(ITicTacToeListener listener) throws RemoteException {
        UUID id = UUID.randomUUID(); // Génère un ID
        salons.put(id, new TicTacToeInstance(listener));
        return id;
    }

    @Override
    public boolean rejoindreSalon(UUID salonId, ITicTacToeListener listener) throws RemoteException {
        try {
            return salons.get(salonId).ajouterJoueur(listener);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean quitterSalon(UUID salonId, ITicTacToeListener listener) throws RemoteException {
        try {
            if (salons.get(salonId) != null)
                salons.get(salonId).retirerJoueur(listener);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // On supprime le salon
        if (salons.get(salonId).getNombreJoueurs() == 0) {
            System.out.println("[MAIN] - Suppression du salon " + salons.remove(salonId).getNom()
                    + ". Cause : 0 joueurs restants.");
        }
        return true;
    }

    @Override
    public boolean jouer(UUID salonId, int x, int y, ITicTacToeListener listener) throws RemoteException {
        try {
            salons.get(salonId).jouer(x, y, listener);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Map<String, UUID> recupererNoms() throws RemoteException {
        HashMap<String, UUID> res = new HashMap<>();
        for (UUID id : salons.keySet()) {
            if (salons.get(id).getNombreJoueurs() < 2) {
                res.put(salons.get(id).getNom(), id);
            }
        }
        return res;
    }
}
