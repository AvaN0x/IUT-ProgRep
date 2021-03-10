package serveur.src.modeles.pendu;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import commun.IPendu;
import commun.PenduResultat;

public class Pendu extends UnicastRemoteObject implements IPendu {
    private static final long serialVersionUID = 725494682670218713L;
    private HashMap<UUID, PenduInstance> salons;

    public Pendu() throws RemoteException {
        super();
        salons = new HashMap<>();
    }

    @Override
    public UUID nouveauSalon() throws RemoteException {
        UUID id = UUID.randomUUID(); // Génère un ID
        try {
            salons.put(id, new PenduInstance()); // Le rajoute à la Map des salons
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // Une erreur lors de la génération du salon s'est produite
            e.printStackTrace();
            return null;
        }
        return id;
    }

    @Override
    public int recupNbLettres(UUID id) throws RemoteException {
        // Renvoie le nombre de lettres du mot du salon `id`
        return salons.get(id).recupererMot().length();
    }

    @Override
    public Map<Character, ArrayList<Integer>> recupIndice(UUID id) throws RemoteException {
        return salons.get(id).recupIndice();
    }

    @Override
    public PenduResultat envoiLettre(UUID id, char lettre) throws RemoteException {
        PenduInstance salon = salons.get(id);
        ArrayList<Integer> indexs = new ArrayList<>();
        for (int i = 0; i < salon.recupererMot().length(); i++) {
            if (salon.recupererMot().toLowerCase().charAt(i) == lettre) {
                indexs.add(i);
            }
        }
        if (indexs.size() == 0) {
            // Si la lettre ne fait pas partie du mot, on fait perdre de la vie au joueur
            return new PenduResultat(salon.perdreVie(), null);
        }
        return new PenduResultat(salon.recupererVie(), indexs);
    }

    @Override
    public boolean fermerSalon(UUID id) throws RemoteException {
        // On retire le salon de la Map et on vérifie que tout s'est bien passé
        return salons.remove(id) != null;
    }
}
