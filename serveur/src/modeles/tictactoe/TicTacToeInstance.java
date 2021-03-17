package serveur.src.modeles.tictactoe;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import commun.Cellule;
import commun.ITicTacToeListener;
import serveur.src.modeles.Utils;

public class TicTacToeInstance {
    private List<ITicTacToeListener> joueurs;
    private String nom;
    private int tour = 0;
    private Cellule[][] plateau;

    public TicTacToeInstance() {
        joueurs = new LinkedList<>();
        // Génère un nom compréhensible de façon aléatoire
        nom = Utils.getUrlContents("https://frightanic.com/goodies_content/docker-names.php");
        plateau = new Cellule[3][3];
    }

    public TicTacToeInstance(ITicTacToeListener listener) {
        this();
        joueurs.add(listener);
    }

    public String getNom() {
        return nom;
    }

    public int getNombreJoueurs() {
        return joueurs.size();
    }

    public void ajouterJoueur(ITicTacToeListener listener) {
        if (joueurs.size() < 2) {
            // Notifier les autres joueurs qu'un joueur a rejoint la partie
            notifier(joueur -> joueur.joueurRejoindre());
            // On ajoute le joueur à la partie
            joueurs.add(listener);
        }
        if (joueurs.size() == 2) {
            // Notifier les joueurs que la partie commence
            notifier(joueur -> joueur.partieLancee());
        }
    }

    public void retirerJoueur(ITicTacToeListener listener) {
        if (joueurs.size() > 0) {
            joueurs.remove(listener);
        }
        // Notifier les autres qu'un joueur a quitté
        notifier(joueur -> joueur.joueurQuitter());
    }

    public void jouer(int x, int y, ITicTacToeListener listener) {
        if (plateau[x][y] == null) {
            plateau[x][y] = Cellule.values()[joueurs.indexOf(listener) + 1];
            tour++;
            // On notifie les joueurs que le plateau à changé
            notifier(joueur -> joueur.celluleMAJ(x, y, plateau[x][y], tour % 2 == joueurs.indexOf(joueur)));
        }
    }

    public void notifier(ConsumerRMITicTacToe action) {
        for (ITicTacToeListener joueur : joueurs) {
            try {
                action.accept(joueur);
            } catch (RemoteException e) {
                if (joueurs.size() > 0) {
                    retirerJoueur(joueur);
                }
            }
        }
    }
}

interface ConsumerRMITicTacToe {
    public void accept(ITicTacToeListener param) throws RemoteException;
}