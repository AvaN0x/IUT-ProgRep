package commun;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

public interface ITicTacToe extends Remote {
    public UUID nouveauSalon(ITicTacToeListener listener) throws RemoteException;

    public boolean rejoindreSalon(UUID salonId, ITicTacToeListener listener) throws RemoteException;

    public boolean quitterSalon(UUID salonId, ITicTacToeListener listener) throws RemoteException;

    public boolean jouer(UUID salonId, int x, int y, ITicTacToeListener listener) throws RemoteException;

    public Map<String, UUID> recupererNoms() throws RemoteException;
}
