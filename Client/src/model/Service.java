package model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {
	public String communication(String array) throws RemoteException;
	public boolean phase_1() throws RemoteException;
	public void phase_2(String s, boolean flag) throws RemoteException;
}
