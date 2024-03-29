package serveur.src.modeles.tictactoe;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import commun.Cellule;
import commun.ITicTacToeListener;
import commun.NullBool;
import serveur.src.modeles.Utils;

public class TicTacToeInstance {
    private List<ITicTacToeListener> joueurs;
    private String nom;
    private int tour = 0;
    private Cellule[][] plateau;
    private boolean estLancee;

    public TicTacToeInstance() {
        joueurs = new LinkedList<>();
        // Génère un nom compréhensible de façon aléatoire
        nom = Utils.getUrlContents("https://frightanic.com/goodies_content/docker-names.php").trim();
        log("La partie est créée.");
        plateau = new Cellule[3][3];
        for (int x = 0; x < plateau.length; x++) {
            for (int y = 0; y < plateau.length; y++) {
                plateau[x][y] = Cellule.INOCCUPE;
            }
        }
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

    public boolean getEstLancee() {
        return estLancee;
    }

    /**
     * Ajoute un joueur à la liste des joueurs et notifie les autres joueurs. <br/>
     * <br/>
     * Lance la partie si le nombre de joueur est suffisant
     * 
     * @param listener Le joueur
     * @return Si tout c'est bien passé
     */
    public boolean ajouterJoueur(ITicTacToeListener listener) {
        if (joueurs.size() < 2) {
            // Notifier les autres joueurs qu'un joueur a rejoint la partie
            notifier(joueur -> joueur.joueurRejoindre());
            log(listener.hashCode() + " a rejoint la partie");
            // On ajoute le joueur à la partie
            joueurs.add(listener);
            if (joueurs.size() == 2) {
                // Notifier les joueurs que la partie commence
                estLancee = true;
                notifier(joueur -> joueur.partieLancee(tour % 2 == joueurs.indexOf(joueur),
                        Cellule.values()[joueurs.indexOf(joueur) + 1]));
                log("Nombre de joueurs atteint. Lancement de la partie.");
            }
            return true;
        }
        return false;
    }

    /**
     * Retire le joueur de la liste des joueurs
     * 
     * @param listener Le joueur
     */
    public void retirerJoueur(ITicTacToeListener listener) {
        joueurs.remove(listener);
        log(listener.hashCode() + " a quitté la partie");
        // Notifier les autres qu'un joueur a quitté
        notifier(joueur -> joueur.joueurQuitter());
    }

    /**
     * Met à jour le plateau et indique aux joueurs que le plateau à été mis à jour
     * aux coordonnées <code>x</code>, <code>y</code><br/>
     * <br/>
     * Vérifie si il y'a un gagnant, ou s'il y a une égalitée
     * 
     * @param x        Colonne de la celulle
     * @param y        Ligne de la cellule
     * @param listener Le client qui vient de jouer
     */
    public void jouer(int x, int y, ITicTacToeListener listener) {
        plateau[x][y] = Cellule.values()[joueurs.indexOf(listener) + 1];
        log(listener.hashCode() + " a joué {x:" + x + " ; y: " + y + "}");
        tour++;
        // On notifie les joueurs que le plateau à changé
        notifier(joueur -> joueur.celluleMAJ(x, y, plateau[x][y], tour % 2 == joueurs.indexOf(joueur)));
        // On vérifie le gagnant
        Cellule joueurGagnant;
        if ((joueurGagnant = verificationVictoire()) != Cellule.INOCCUPE) {
            log(joueurs.get(joueurGagnant.ordinal() - 1).hashCode() + " a gagné");
            notifier(joueur -> joueur.aGagner(
                    joueurGagnant == Cellule.values()[joueurs.indexOf(joueur) + 1] ? NullBool.TRUE : NullBool.FALSE));
        } else if (estPlateauPlein()) {
            log("C'est une égalité.");
            notifier(joueur -> joueur.aGagner(NullBool.NULL));
        }
    }

    /**
     * Vérifie si le plateau est plein
     * 
     * @return Si le plateau est plein
     */
    public boolean estPlateauPlein() {
        for (int x = 0; x < plateau.length; x++) {
            for (int y = 0; y < plateau[x].length; y++) {
                // Si une case esst vide, c'est que le plateau n'est pas plein
                if (plateau[x][y] == Cellule.INOCCUPE) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Vérifie s'il y a un gagnant
     * 
     * @return Le joueur qui a gagné, ou <code>INOCCUPE</code> si aucun gagnant
     */
    public Cellule verificationVictoire() {
        // On vérifie si une colonne est présente
        for (int x = 0; x < plateau.length; x++) {
            Cellule gagnant = plateau[x][0];
            for (int y = 0; y < plateau[x].length; y++) {
                if (plateau[x][y] != gagnant) {
                    // La colonne n'est pas valide
                    gagnant = Cellule.INOCCUPE;
                    break;
                }
            }
            if (gagnant != Cellule.INOCCUPE) {
                return gagnant;
            }
        }
        // On vérifie si une ligne est présente
        for (int y = 0; y < plateau.length; y++) {
            Cellule gagnant = plateau[0][y];
            for (int x = 0; x < plateau[y].length; x++) {
                if (plateau[x][y] != gagnant) {
                    // La ligne n'est pas valide
                    gagnant = Cellule.INOCCUPE;
                    break;
                }
            }
            if (gagnant != Cellule.INOCCUPE) {
                return gagnant;
            }
        }
        // On vérifie si une diagonale est présente
        for (int i = 0; i < 3; i += 2) {
            Cellule gagnant = plateau[i][0];
            for (int n = 0; n < plateau.length; n++) {
                if (plateau[i != 2 ? n : (plateau.length - 1) - n][n] != gagnant) {
                    // La diagonale n'est pas valide
                    gagnant = Cellule.INOCCUPE;
                    break;
                }
            }
            if (gagnant != Cellule.INOCCUPE) {
                return gagnant;
            }
        }
        return Cellule.INOCCUPE;
    }

    /**
     * Affiche un message dans la console du serveur pour surveiller l'activiter du
     * salon
     * 
     * @param message
     */
    public void log(Object message) {
        System.out.println(String.format("[%s] - " + message.toString(), nom));
    }

    /**
     * Notifie les joueursde l'<code>action</code>
     * 
     * @param action
     */
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

/**
 * Action servant pour le modèle d'évenements
 */
interface ConsumerRMITicTacToe {
    public void accept(ITicTacToeListener param) throws RemoteException;
}