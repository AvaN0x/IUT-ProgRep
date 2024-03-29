package client.src.controleurs.tictactoe;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.UUID;

import client.src.ClientMain;
import commun.Cellule;
import commun.ITicTacToe;
import commun.ITicTacToeListener;
import commun.NullBool;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class TicTacToeControleur extends client.src.controleurs.BaseControleur {
    private ITicTacToe partie;
    private UUID id;

    private TicTacToeMonitor monitor;

    @FXML
    private Pane pane_caseConteneur;
    @FXML
    private StackPane sp_mainConteneur;
    @FXML
    private VBox vbox_lobbyConteneur;
    @FXML
    private TextField tf_entrerSalon;
    @FXML
    private ListView<String> lv_salonListe;
    @FXML
    private Label lbl_log;
    @FXML
    private Label lbl_nomSalon;

    private boolean estTonTour;
    private boolean partieTerminee;
    private Cellule numJoueur;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        try {
            // On se connecte a userveur
            this.partie = (ITicTacToe) Naming.lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/tictactoe");
            this.monitor = new TicTacToeMonitor(this);
            // On initialise le lobby
            initLobby();

            // Evenement lors du clic de la croix de la fenetre
            Platform.runLater(() -> {
                _vue.setOnCloseRequest((event) -> {
                    quitter();
                });
            });

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            showErreurAlerte("TicTacToe exception: ", e.toString());
            this.fermer();
        }
    }

    public void quitter() {
        try {
            // Si l'id n'est pas null, on ferme le salon
            if (id != null) {
                this.partie.quitterSalon(this.id, monitor);
                this.id = null;
            }
        } catch (RemoteException e) {
            showErreurAlerte("TicTacToe exception: ", e.toString());
        }
        this._vue.close();
    }

    private void initLobby() throws RemoteException {
        // On affiche que ce qui est necessaire sur la fenetre
        sp_mainConteneur.setVisible(false);
        tf_entrerSalon.setText("");
        vbox_lobbyConteneur.setVisible(true);
        setLog("");
        setNomSalon("");

        // On rerempli la liste des salonss
        lv_salonListe.getItems().clear();
        var noms = this.partie.recupererNoms();
        noms.forEach((key, value) -> {
            lv_salonListe.getItems().add(key);
        });

        // Evenement de clic sur un element de la liste
        lv_salonListe.setOnMouseClicked((event) -> {
            // On recupere le nom du salon sur le quel l'utilisateur a cliqué
            var nomSalon = lv_salonListe.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2 && nomSalon != null)
                try {
                    var salonID = noms.get(nomSalon);
                    // Verification de si le salon existe
                    if (salonID != null && this.partie.rejoindreSalon(salonID, monitor)) {
                        this.id = salonID;
                        setNomSalon(nomSalon);
                        vbox_lobbyConteneur.setVisible(false);
                        sp_mainConteneur.setVisible(false);
                    } else
                        showErreurAlerte("TicTacToe erreur: ", "Nous n'avons pas pu rejoindre le salon.");
                } catch (RemoteException e) {
                    showErreurAlerte("TicTacToe exception: ", "Nous n'avons pas pu rejoindre le salon.");
                    e.printStackTrace();
                }
        });
    }

    public void onEnterEntrerSalon() throws RemoteException {
        // Fonction lorsque l'utilisateur fait entrer dans la textfield
        var noms = this.partie.recupererNoms();
        var nomSalon = tf_entrerSalon.getText().trim();
        var salonID = noms.get(nomSalon);
        // Verification de si le salon existe
        if (salonID != null && this.partie.rejoindreSalon(salonID, monitor)) {
            this.id = salonID;
            setNomSalon(nomSalon);
            vbox_lobbyConteneur.setVisible(false);
            sp_mainConteneur.setVisible(false);
        } else
            showErreurAlerte("TicTacToe exception: ", "Nous n'avons pas pu rejoindre le salon.");

    }

    public void nouveauSalon() throws RemoteException {
        // Fonction lors du clic sur le bouton de nouveau salon
        this.id = this.partie.nouveauSalon(monitor);

        // On récupère le nom du salon pour l'afficher
        var noms = this.partie.recupererNoms();
        noms.forEach((key, value) -> {
            if (value.equals(this.id))
                setNomSalon(key);
        });

        // On attend un autre joueur
        attendAutreJoueur();
    }

    public void initPartie() throws RemoteException {
        Platform.runLater(() -> {
            // On affiche ce qui est necessaire
            vbox_lobbyConteneur.setVisible(false);
            sp_mainConteneur.setVisible(true);
            partieTerminee = false;

            // On initialise les cases du jeu
            for (int i = 0; i < 9; i++)
                caseCliquable((Group) pane_caseConteneur.getChildren().get(i), i);
        });
    }

    private void attendAutreJoueur() {
        // Affiche d'attente d'un autre joueur
        vbox_lobbyConteneur.setVisible(false);
        sp_mainConteneur.setVisible(false);
        setLog("En attente d'un autre joueur...");
    }

    private void addCroix(Group grp, boolean isMoi) {
        grp.getChildren().clear();

        // On dessine la croix
        Rectangle tige1 = new Rectangle(.1, .7);
        tige1.setFill(isMoi ? Color.rgb(0, 122, 204) : Color.rgb(204, 0, 85));
        tige1.setArcHeight(.1);
        tige1.setArcWidth(.1);
        tige1.setRotate(-45);
        Rectangle tige2 = new Rectangle(.10, .7);
        tige2.setFill(isMoi ? Color.rgb(0, 122, 204) : Color.rgb(204, 0, 85));
        tige2.setArcHeight(.1);
        tige2.setArcWidth(.1);
        tige2.setRotate(45);

        grp.getChildren().addAll(tige1, tige2);
        // On enleve l'effet de clic
        grp.setOnMouseClicked(null);
    }

    private void addRond(Group grp, boolean isMoi) {
        grp.getChildren().clear();

        // On dessine le rond
        Circle cercle = new Circle(.28, Color.TRANSPARENT);
        cercle.setStrokeWidth(.1);
        cercle.setStroke(isMoi ? Color.rgb(0, 122, 204) : Color.rgb(204, 0, 85));

        grp.getChildren().add(cercle);
        // On enleve l'effet de clic
        grp.setOnMouseClicked(null);
    }

    private void caseCliquable(Group grp, int i) {
        grp.getChildren().clear();

        // On dessine une forme de fond de case pour cliquer dessus
        Rectangle fond = new Rectangle(1, 1);
        fond.setFill(Color.TRANSPARENT);
        grp.getChildren().add(fond);

        // Evenement de clic sur une case pour la jouer
        grp.setOnMouseClicked((e) -> {
            try {
                if (estTonTour)
                    this.partie.jouer(id, i % 3, i / 3, this.monitor);
            } catch (RemoteException e1) {
            }
        });
    }

    private void setLog(String logString) {
        Platform.runLater(() -> {
            lbl_log.setText(logString);
        });
    }

    private void setNomSalon(String nomSalon) {
        Platform.runLater(() -> {
            lbl_nomSalon.setText(nomSalon);
        });
    }

    public void partieLancee(boolean estTonTour, Cellule numJoueur) throws RemoteException {
        // La partie peut etre lancée, on l'initialise
        initPartie();
        this.numJoueur = numJoueur;
        setTour(estTonTour);
    }

    public void joueurRejoindre() throws RemoteException {
        setLog("Un joueur a rejoint la partie.");
    }

    public void joueurQuitter() throws RemoteException {
        // Un joueur a quitté la partie
        // Ne s'affiche que lorsque la partie n'est pas déjà terminée
        if (!partieTerminee)
            Platform.runLater(() -> {
                showAlerte("TicTacToe", "", "Vous avez gagné car le joueur en face a quitté la partie.",
                        AlertType.INFORMATION);
                try {
                    if (id != null)
                        this.partie.quitterSalon(this.id, monitor);
                    initLobby();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    quitter();
                }
            });
    }

    public void celluleMAJ(int x, int y, Cellule status, boolean estTonTour) throws RemoteException {
        setTour(estTonTour);
        // Dessine sur la case concernées
        switch (status) {
        case JOUEUR_1:
            Platform.runLater(() -> addCroix((Group) pane_caseConteneur.getChildren().get(x + 3 * y),
                    numJoueur == Cellule.JOUEUR_1));
            break;
        case JOUEUR_2:
            Platform.runLater(() -> addRond((Group) pane_caseConteneur.getChildren().get(x + 3 * y),
                    numJoueur == Cellule.JOUEUR_2));
            break;
        default:
            break;
        }
    }

    private void setTour(boolean estTonTour) {
        // Affiche si c'est le tour du joueur ou non
        this.estTonTour = estTonTour;
        setLog(this.estTonTour ? "C'est à vous de jouer." : "En attente de l'autre joueur...");
        // Effet d'opacité sur la grille pour reconnaitre si c'est au client de jouer
        sp_mainConteneur.setOpacity(this.estTonTour ? 1. : .6);
    }

    public void aGagner(NullBool estGagnant) throws RemoteException {
        Platform.runLater(() -> {
            partieTerminee = true;
            // On observe le cas de fin de partie
            if (estGagnant == NullBool.TRUE)
                showAlerte("TicTacToe", "", "Vous avez gagné la partie.", AlertType.INFORMATION);
            else if (estGagnant == NullBool.FALSE)
                showAlerte("TicTacToe", "", "Vous avez perdu la partie.", AlertType.INFORMATION);
            else
                showAlerte("TicTacToe", "", "Vous avez fait égalité.", AlertType.INFORMATION);

            try {
                // On fait quitter le salon au joueur
                if (id != null) {
                    this.partie.quitterSalon(this.id, monitor);
                    this.id = null;
                }
                // On réinitialise le lobby
                initLobby();
            } catch (RemoteException e) {
                e.printStackTrace();
                quitter();
            }
        });
    }
}

class TicTacToeMonitor extends java.rmi.server.UnicastRemoteObject implements ITicTacToeListener, Serializable {
    private transient TicTacToeControleur controller;

    public TicTacToeMonitor(TicTacToeControleur controller) throws RemoteException {
        this.controller = controller;
    }

    @Override
    public void partieLancee(boolean estTonTour, Cellule numJoueur) throws RemoteException {
        controller.partieLancee(estTonTour, numJoueur);
    }

    @Override
    public void joueurRejoindre() throws RemoteException {
        controller.joueurRejoindre();
    }

    @Override
    public void joueurQuitter() throws RemoteException {
        controller.joueurQuitter();
    }

    @Override
    public void celluleMAJ(int x, int y, Cellule status, boolean estTonTour) throws RemoteException {
        controller.celluleMAJ(x, y, status, estTonTour);
    }

    @Override
    public void aGagner(NullBool estGagnant) throws RemoteException {
        controller.aGagner(estGagnant);
    }
}

class CelluleMAJ implements Serializable {
    public int x;
    public int y;
    public Cellule status;

    public CelluleMAJ(int x, int y, Cellule status) {
        this.x = x;
        this.y = y;
        this.status = status;
    }
}