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
        nom = Utils.getUrlContents("https://frightanic.com/goodies_content/docker-names.php").trim();
        log("La partie est créée.");
        plateau = new Cellule[3][3];
    }

    public TicTacToeInstance(ITicTacToeListener listener) {
        this();
        joueurs.add(listener);
        log(listener.hashCode() + " a rejoint la partie");
    }

    public String getNom() {
        return nom;
    }

    public int getNombreJoueurs() {
        return joueurs.size();
    }

    public boolean ajouterJoueur(ITicTacToeListener listener) {
        if (joueurs.size() < 2) {
            // Notifier les autres joueurs qu'un joueur a rejoint la partie
            notifier(joueur -> joueur.joueurRejoindre());
            log(listener.hashCode() + " a rejoint la partie");
            // On ajoute le joueur à la partie
            joueurs.add(listener);
            if (joueurs.size() == 2) {
                // Notifier les joueurs que la partie commence
                notifier(joueur -> joueur.partieLancee(tour % 2 == joueurs.indexOf(joueur)));
                log("Nombre de joueurs atteint. Lancement de la partie.");
            }
            return true;
        }
        return false;
    }

    public void retirerJoueur(ITicTacToeListener listener) {
        joueurs.remove(listener);
        log(listener.hashCode() + " a quitté la partie");
        // Notifier les autres qu'un joueur a quitté
        notifier(joueur -> joueur.joueurQuitter());
    }

    public void jouer(int x, int y, ITicTacToeListener listener) {
        plateau[x][y] = Cellule.values()[joueurs.indexOf(listener) + 1];
        log(listener.hashCode() + " a joué {x:" + x + " ; y: " + y + "}");
        tour++;
        // On notifie les joueurs que le plateau à changé
        notifier(joueur -> joueur.celluleMAJ(x, y, plateau[x][y], tour % 2 == joueurs.indexOf(joueur)));
        // On vérifie le gagnant
        Cellule joueurGagnant;
        if ((joueurGagnant = verificationVictoire()) != Cellule.INOCCUPE) {
            notifier(joueur -> joueur.aGagner(joueurGagnant == Cellule.values()[joueurs.indexOf(listener) + 1]));
        }
    }

    public Cellule verificationVictoire() {
        // On vérifie si une ligne est présente
        Cellule gagnantLigne = Cellule.INOCCUPE;
        for (int x = 0; x < plateau.length; x++) {
            gagnantLigne = plateau[x][0];
            for (int y = 0; y < plateau[x].length; y++) {
                if (plateau[x][y] != gagnantLigne) {
                    // La ligne n'est pas valide
                    gagnantLigne = Cellule.INOCCUPE;
                    break;
                }
            }
        }
        // On vérifie si une colonne est présente
        Cellule gagnantColonne = Cellule.INOCCUPE;
        for (int y = 0; y < plateau.length; y++) {
            gagnantColonne = plateau[0][y];
            for (int x = 0; x < plateau[y].length; x++) {
                if (plateau[x][y] != gagnantColonne) {
                    // La colonne n'est pas valide
                    gagnantColonne = Cellule.INOCCUPE;
                    break;
                }
            }
        }
        // On vérifie si une diagonale est présente
        Cellule gagnantDiagonale = plateau[0][0];
        for (int i = 0; i < plateau.length; i++) {
            if (plateau[i][i] != gagnantDiagonale) {
                // La diagonale n'est pas valide
                gagnantDiagonale = Cellule.INOCCUPE;
                break;
            }
        }
        Cellule gagnantDiagonaleInv = plateau[2][0];
        for (int i = 0; i < plateau.length; i++) {
            if (plateau[(plateau.length - 1) - i][i] != gagnantDiagonaleInv) {
                // La diagonale n'est pas valide
                gagnantDiagonaleInv = Cellule.INOCCUPE;
                break;
            }
        }
        if (gagnantLigne != Cellule.INOCCUPE)
            return gagnantLigne;
        if (gagnantColonne != Cellule.INOCCUPE)
            return gagnantColonne;
        if (gagnantDiagonale != Cellule.INOCCUPE)
            return gagnantDiagonale;
        return gagnantDiagonaleInv;
    }

    public void log(Object message) {
        System.out.println(String.format("[%s] - " + message.toString(), nom));
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