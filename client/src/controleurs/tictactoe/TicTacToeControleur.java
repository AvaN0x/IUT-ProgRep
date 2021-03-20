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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.control.Button;
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

    private int numeroJoueur;

    private int[] cases;
    private boolean estTonTour;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        try {
            this.partie = (ITicTacToe) Naming.lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/tictactoe");
            this.monitor = new TicTacToeMonitor();
            initLobby();

            Platform.runLater(() -> {
                _vue.setOnCloseRequest((event) -> {
                    quitter();
                });
            });

            // Dispatch incomming events
            new Thread(() -> {
                while (true) {
                    if (monitor.partieLance) {
                        monitor.partieLance = false;
                        try {
                            partieLancee(monitor.estTonTour);
                        } catch (RemoteException e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                    if (monitor.estTonTour) {
                        // TODO
                    }
                    if (monitor.aRejoint) {
                        monitor.aRejoint = false;
                        Platform.runLater(() -> {
                            try {
                                joueurRejoindre();
                            } catch (RemoteException e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }
                        });
                    }
                    if (monitor.aQuitte) {
                        monitor.aQuitte = false;
                        Platform.runLater(() -> {
                            try {
                                joueurQuitter();
                            } catch (RemoteException e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }
                        });
                        break;
                    }
                    if (monitor.celluleMAJ != null) {
                        monitor.celluleMAJ = null;
                        Platform.runLater(() -> {
                            try {
                                celluleMAJ(monitor.celluleMAJ, estTonTour);
                            } catch (RemoteException e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }).start();

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            showErreurAlerte("TicTacToe exception: ", e.toString());
            this.fermer();
        }
    }

    public void quitter() {
        try {
            if (id != null)
                this.partie.quitterSalon(this.id, monitor);
        } catch (RemoteException e) {
            showErreurAlerte("TicTacToe exception: ", e.toString());
        }
        this._vue.close();
    }

    private void initLobby() throws RemoteException {
        sp_mainConteneur.setVisible(false);
        tf_entrerSalon.setText("");
        vbox_lobbyConteneur.setVisible(true);

        var noms = this.partie.recupererNoms();
        noms.forEach((key, value) -> {
            lv_salonListe.getItems().add(key);
        });

        lv_salonListe.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2)
                try {
                    var salonID = noms.get(lv_salonListe.getSelectionModel().getSelectedItem());
                    if (this.partie.rejoindreSalon(salonID, monitor)) {
                        this.id = salonID;
                        initPartie();
                    } else
                        showErreurAlerte("TicTacToe erreur: ", "Nous n'avons pas pu rejoindre le salon.");
                } catch (RemoteException e) {
                    showErreurAlerte("TicTacToe exception: ", "Nous n'avons pas pu rejoindre le salon.");
                    e.printStackTrace();
                }
        });
    }

    public void onEnterEntrerSalon() throws RemoteException {
        var noms = this.partie.recupererNoms();
        var salonID = noms.get(lv_salonListe.getSelectionModel().getSelectedItem());
        if (this.partie.rejoindreSalon(salonID, monitor)) {
            this.id = salonID;
            attendAutreJoueur();
        } else
            showErreurAlerte("TicTacToe exception: ", "Nous n'avons pas pu rejoindre le salon.");

    }

    public void nouveauSalon() throws RemoteException {
        this.id = this.partie.nouveauSalon(monitor);
        attendAutreJoueur();
    }

    public void initPartie() throws RemoteException {
        vbox_lobbyConteneur.setVisible(false);
        sp_mainConteneur.setVisible(true);

        Platform.runLater(() -> {
            for (int i = 0; i < 9; i++)
                caseCliquable((Group) pane_caseConteneur.getChildren().get(i), i);
        });
    }

    private void attendAutreJoueur() {
        vbox_lobbyConteneur.setVisible(false);
        sp_mainConteneur.setVisible(false);
        setLog("En attente d'un autre joueur...");
    }

    private void addCroix(Group grp) {
        grp.getChildren().clear();

        Rectangle tige1 = new Rectangle(.1, .7);
        tige1.setFill(Color.rgb(0, 122, 204));
        tige1.setArcHeight(.1);
        tige1.setArcWidth(.1);
        tige1.setRotate(-45);
        Rectangle tige2 = new Rectangle(.10, .7);
        tige2.setFill(Color.rgb(0, 122, 204));
        tige2.setArcHeight(.1);
        tige2.setArcWidth(.1);
        tige2.setRotate(45);

        grp.getChildren().addAll(tige1, tige2);
    }

    private void addRond(Group grp) {
        grp.getChildren().clear();

        Circle cercle = new Circle(.28, Color.TRANSPARENT);
        cercle.setStrokeWidth(.1);
        cercle.setStroke(Color.rgb(0, 122, 204));

        grp.getChildren().add(cercle);
    }

    private void caseCliquable(Group grp, int i) {
        grp.getChildren().clear();

        Rectangle fond = new Rectangle(1, 1);
        fond.setFill(Color.TRANSPARENT);
        grp.getChildren().add(fond);

        grp.setOnMouseClicked((e) -> {
            try {
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

    public void partieLancee(boolean estTonTour) throws RemoteException {
        this.estTonTour = estTonTour;
        initPartie();
    }

    public void joueurRejoindre() throws RemoteException {
        setLog("Un joueur a rejoint la partie.");
    }

    public void joueurQuitter() throws RemoteException {
        showAlerte("TicTacToe", "TicTacToe", "Vous avez gagné car le joueur en face a quitté la partie.",
                AlertType.INFORMATION);
        quitter();

    }

    public void celluleMAJ(CelluleMAJ cellule, boolean estTonTour) throws RemoteException {
        this.estTonTour = estTonTour;
        switch (cellule.status) {
        case JOUEUR_1:
            Platform.runLater(() -> addCroix((Group) pane_caseConteneur.getChildren().get(x + 3 * y)));
            break;
        case JOUEUR_2:
            Platform.runLater(() -> addRond((Group) pane_caseConteneur.getChildren().get(x + 3 * y)));
            break;
        default:
            break;
        }

    }

}

class TicTacToeMonitor extends java.rmi.server.UnicastRemoteObject implements ITicTacToeListener, Serializable {
    private transient TicTacToeControleur controller;

    public TicTacToeMonitor(TicTacToeControleur controller) throws RemoteException {
        this.controller = controller;
    }

    @Override
    public void partieLancee(boolean estTonTour) throws RemoteException {
        this.estTonTour = estTonTour;
    }

    @Override
    public void joueurRejoindre() throws RemoteException {
        this.aRejoint = true;
    }

    @Override
    public void joueurQuitter() throws RemoteException {
        this.aQuitte = true;
    }

    @Override
    public void celluleMAJ(int x, int y, Cellule status, boolean estTonTour) throws RemoteException {
        this.celluleMAJ = new CelluleMAJ(x, y, status);
        this.estTonTour = estTonTour;
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