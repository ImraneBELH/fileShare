package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;



/* Le client doit �tre capable de lire un fichier texte et d�envoyer son contenu
 *  au serveur qui retransmettra aussit�t son contenu au client. Ce dernier devra 
 *  intercepterle contenu du fichier texte. Une fois la r�ception termin�e, le 
 *  serveur devra inverser le contenu du fichier de sorte � ce que la premi�re 
 *  ligne re�ue soit la derni�re ligne envoy�e vers le client. **/


public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
		String ipAdress = ValidateIPv4.GetIpAdress(reader);
		int port = ValidateIPv4.GetPort(reader); 
		Socket clientSocket = null;
		
		
		try {
			clientSocket = new Socket(ipAdress, port);
			DataOutputStream objectOutput = new DataOutputStream(clientSocket.getOutputStream());
			ObjectInputStream objectInput =  new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			byte[] mybytearray = null;
			boolean exit = false;
			
			while (!exit) {
				System.out.print(">");
				String command = reader.readLine();
				objectOutput.writeUTF(command);
				args = command.split(" ");
				switch (args[0]) {
					case "exit" : 
						exit = true;
						break;
					case "cd":
						System.out.println((String)objectInput.readObject());
						break;
					case "mkdir":
						break;
					case "ls" :
						@SuppressWarnings("unchecked") List<String> strings = (List<String>) objectInput.readObject();
						for (String name : strings) {
							System.out.println(name);
						}
						break;
					case "upload" :
						File file = new File("./src/client/upload_folder/" + args[1]);
						mybytearray= new byte[(int) file.length()];
						System.out.print(file.length());
						FileInputStream fis = new FileInputStream(file);
						BufferedInputStream bis = new BufferedInputStream(fis);
						
						DataInputStream dis = new DataInputStream(bis);   
				        dis.readFully(mybytearray, 0, mybytearray.length);
						
						objectOutput.writeLong(mybytearray.length);
						objectOutput.write(mybytearray, 0, mybytearray.length);
						objectOutput.flush();
						break;
					case "download":
						boolean isAvailable = objectInput.readBoolean();
						if (!isAvailable) {
							System.out.println("File not found");
							break;
						}
						OutputStream output = new FileOutputStream("./src/client/upload_folder/" + args[1]);
						long size = objectInput.readLong();
						System.out.print(size);
						int bytesRead;
						byte[] buffer = new byte[1024];
						while (size > 0 && (bytesRead = objectInput.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
						{
						    output.write(buffer, 0, bytesRead);
						    size -= bytesRead;
						}
						break;
					default:
						if (command.length() != 0)
							System.out.println("La commande n'est pas reconnue.");
						break;
				}
			}

		} finally {
			// Fermeture du socket.
			clientSocket.close();
			System.out.print("Connection closed.");
		}
	}
}