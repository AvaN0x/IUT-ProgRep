package commun;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

public interface ITicTacToe extends Remote {
    public UUID nouveauSalon(UUID pid) throws RemoteException;

    public boolean rejoindreSalon(UUID salonId, UUID pid) throws RemoteException;

    public boolean quitterSalon(UUID salonId, UUID pid) throws RemoteException;
}
