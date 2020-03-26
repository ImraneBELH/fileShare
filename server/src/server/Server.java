package server;


import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	Socket socket = null;
	private ServerSocket server = null;
	public Server(int port) {
		try {
			server = new ServerSocket(port);
			System.out.println("Server Started");

			while (true) {
				new ClientHandler(server.accept()).start();
			}
			
		}
		catch(IOException i) {
			System.out.println(i);
		}
	}
	
	public static void main(String args[]) throws IOException {	
		
		int port = 5000;
		
		do {
			System.out.println("Enter a port for the server:");
			BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in)); 
			port = Integer.parseInt(reader.readLine());
		}while (port > 5050 || port < 5000);		
		new Server(port);
	}
}

class ClientHandler extends Thread{
	private final String basePath = "./src/server/storage/";
	
	private ObjectOutputStream out = null;
	private DataInputStream inStream= null;
	private String currPath = basePath;
	private Socket socket = null;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try {
			System.out.println("Client accepted:");
			inStream = new DataInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			boolean exit = false;
			byte[] mybytearray = null;
			
			while (!exit) {
				String args[] = ((String)inStream.readUTF()).split(" ");
				switch (args[0]) {
					case "exit" :
						exit = true;
						break;
					case "mkdir":
						File newFolder = new File(currPath + args[1]);
						newFolder.mkdirs();
						break;
					case "ls" :
						out.writeObject(getFiles());
						out.flush();
						break;
					case "cd":
						out.writeObject(cdCommand(args, args.length));
						System.out.println(currPath);
						break;
					case "upload":
						OutputStream output = new FileOutputStream(args[1]);
						long size = inStream.readLong();
						System.out.print(size);
						int bytesRead;
						byte[] buffer = new byte[1024];
						while (size > 0 && (bytesRead = inStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
						{
						    output.write(buffer, 0, bytesRead);
						    size -= bytesRead;
						}
						break;
					case "download" :
						File file = new File(currPath + args[1]);
						if (!file.exists())
							out.writeBoolean(false);
						out.writeBoolean(true);
						
						mybytearray= new byte[(int) file.length()];
						System.out.print(file.length());
						FileInputStream fis = new FileInputStream(file);
						BufferedInputStream bis = new BufferedInputStream(fis);
						
						DataInputStream dis = new DataInputStream(bis);   
				        dis.readFully(mybytearray, 0, mybytearray.length);
						
						out.writeLong(mybytearray.length);
						out.write(mybytearray, 0, mybytearray.length);
						out.flush();
						break;
					default:
						break;
				}
			}
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("client handelded");
	}
	/*
	 * manages the cd command
	 * return true if base path is changed, false if not.
	 * */
	private String cdCommand(String[] args, int length) {
		if (args.length == 1) {
			currPath = basePath;
			return "Vous etes dans le dossier de depart.";
		}
		
		if(args[1].equals("..") && !currPath.equals(basePath)){
			currPath = currPath.substring(0, currPath.length() - 1);
			currPath = currPath.substring(0, currPath.lastIndexOf("/") + 1);
			String path[] = currPath.split("/");
			return "Vous etes dans le dossier " + path[path.length - 1] + ".";
		}
		
		if (folderIsAvailable(args[1]) && !args[1].equals("..")) {
			currPath += args[1] + "/";
			if (currPath.equals(basePath))
				return "Vous etes dans le dossier de depart.";
			return "Vous etes dans le dossier " + args[1] + ".";
		}
		return "Fichier introuvable";
	}
	
	private boolean folderIsAvailable(String argument) {
		File file = new File(currPath + argument + "/");
		return file.exists();
		
	}
	private List<String> getFiles() {
		List<String> list = new ArrayList<String>();
		File dir = new File(currPath);
		File[] fileNames = dir.listFiles();
			
		for(File file : fileNames) {
			if (file.isDirectory()) {
				list.add("[Folder] " + file.getName());
			} else {
				list.add("[File] " + file.getName());
			}
		}
		
		return list;
	}
}
