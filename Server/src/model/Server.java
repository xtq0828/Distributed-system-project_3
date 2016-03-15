package model;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Scanner;

import model.ServiceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.UnknownHostException;

public class Server {
	public static void main(String[] args) throws UnknownHostException {
		Scanner s = new Scanner(System.in);
		String str1 = null;
		String str2 = null;
		System.out.println("Please enter the IP address of the Server:");
		str1 = s.next();
		System.out.println("Please enter the port number of the Server:");
		str2 = s.next();
		s.close();
		File ADDRESS = new File("server_address.txt");
		ArrayList<String> address = new ArrayList<String>();
		File SERVER = new File("server.txt");
		try {
			Service service = new ServiceImpl();
			LocateRegistry.createRegistry(6600);
			Naming.rebind("rmi://" + str1 + ":" + str2 + "/Service", service);
			System.out.println("Service Start!");
			BufferedReader reader = new BufferedReader(new FileReader(ADDRESS));
			String str = "";
			while ((str = reader.readLine()) != null) {
				if (!str.equals(str1 + ":" + str2))
					address.add(str);
			}
			reader.close();
			if (address.size() > 0) {
				str = str1 + ":" + str2;
				MyThread runnable = new MyThread(str, address, SERVER.lastModified());		
				while (true) {
					Thread thread = new Thread(runnable);
					thread.start();
					thread.join();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class MyThread implements Runnable {
	private String str = "";
	private ArrayList<String> address = new ArrayList<String>();
	private File file = new File("server.txt");
	private String line = null;
	private String s = "";
	private static long lastModified = 0l;
	private boolean ret = true;

	public MyThread(String str, ArrayList<String> address, long lastModified) {
		this.str = str;
		this.address = address;
		MyThread.lastModified = lastModified;
	}

	public synchronized void run() {
		File SERVER = new File("server.txt");
		while (true) {
			if (SERVER.lastModified() > lastModified) {
				lastModified = SERVER.lastModified();
				break;
			}
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				s += line + "\r\n";
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < address.size(); i++) {
			try {
				Service service = (Service) Naming.lookup("rmi://" + address.get(i) + "/Service");
				if (!service.phase_1()) {
					System.out.println("Updating failed in SERVER: " + address.get(i));
					ret = false;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < address.size(); i++) {
			try {
				Service service = (Service) Naming.lookup("rmi://" + address.get(i) + "/Service");
				if (ret)
					service.phase_2(s, true);
				else
					service.phase_2(s, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
