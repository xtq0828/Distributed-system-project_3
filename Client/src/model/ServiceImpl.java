package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import model.Service;

public class ServiceImpl extends UnicastRemoteObject implements Service {

	protected ServiceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	File file_1 = new File("server_log.txt");
	File file_2 = new File("server.txt");
	File file_3 = new File("save.txt");
	public static HashMap<String, Integer> set = new HashMap<String, Integer>();

	public synchronized String communication(String array) {
		String ret = "";
		try {
			byte[] save = new byte[(int) file_3.length()];
			InputStream input = new FileInputStream(file_2);
			OutputStream output = new FileOutputStream(file_3);
			input.read(save);
			output.write(save);
			input.close();
			output.close();
			String[] arr1 = array.split("\r\n");
			BufferedReader reader_1 = new BufferedReader(new FileReader(file_2));
			getstoredvalue(reader_1);
			FileWriter fileWritter_1 = new FileWriter(file_1.getName(), true);
			BufferedWriter bufferWritter_1 = new BufferedWriter(fileWritter_1);
			for (int i = 0; i < arr1.length; i++) {
				String str2 = arr1[i];
				if (str2.equals("END"))
					break;
				String[] arr2 = str2.split("\\s+");
				if ((arr2[0].equals("PUT") || arr2[0].equals("put")) && arr2.length == 3) {
					String key = arr2[1];
					int value = Integer.valueOf(arr2[2]);
					set.put(key, value);
					ret += "(" + key + "," + value + ") has been stored" + "\r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("(" + key + "," + value + ") has been stored in " + time + "\r\n");
				} else if ((arr2[0].equals("GET") || arr2[0].equals("get")) && arr2.length == 2) {
					String key = arr2[1];
					if (!set.containsKey(key)) {
						ret += "There is not a key " + key + "\r\n";
						continue;
					}
					int value = set.get(key);
					ret += "The value of key " + key + " is " + value + "\r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("There is a request of key " + key + " in " + time + "\r\n");
				} else if ((arr2[0].equals("DELETE") || arr2[0].equals("delete") && arr2.length == 2)) {
					String key = arr2[1];
					if (!set.containsKey(key)) {
						ret += "There is not a key " + key + "\r\n";
						continue;
					}
					set.remove(key);
					ret += "The key " + key + " has been deleted + \r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("The key " + key + " has been deleted in " + time + "\r\n");
				} else {
					ret += "Error of message format:" + str2 + "\r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("There is a request with wrong message format in " + time + "\r\n");
				}
			}
			ret += "END";
			bufferWritter_1.close();
			FileWriter fileWritter_2 = new FileWriter(file_2.getName());
			BufferedWriter bufferWritter_2 = new BufferedWriter(fileWritter_2);
			updatestoredvalue(bufferWritter_2);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void getstoredvalue(BufferedReader reader) throws IOException {
		String line = null;
		while (true) {
			line = reader.readLine();
			if (line == null || line.equals("END"))
				break;
			String[] arr = line.split("\\s+");
			set.put(arr[0], Integer.valueOf(arr[1]));
		}
		reader.close();
	}

	public static void updatestoredvalue(BufferedWriter bufferWritter) throws IOException {
		for (String key : set.keySet()) {
			String value = set.get(key).toString();
			bufferWritter.write(key + " " + value + "\r\n");
		}
		bufferWritter.write("END");
		bufferWritter.close();
	}

	public boolean phase_1() {
		try {
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void phase_2(String s, boolean flag) {
		try {
			if (flag) {
				BufferedWriter out = new BufferedWriter(new FileWriter(file_2));
				out.write(s);
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
