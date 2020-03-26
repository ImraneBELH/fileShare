package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
// Java program to validate an IPv4 address
class ValidateIPv4
{
    // an IPv4 address
    private static final String INET4ADDRESS = "172.8.9.28";
 
    private static final String IPv4_REGEX =
                    "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static final String PORT =  "(50[0-4][0-9]|5050)";
 
    private static final Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);
    private static final Pattern PORT_PATTERN = Pattern.compile(PORT);

 
 
    public static boolean isValidInet4Address(String ip) {
        if (ip == null) {
            return false;
        }
 
        Matcher matcher = IPv4_PATTERN.matcher(ip);
 
        return matcher.matches();
    }
    
    public static boolean isValidPort(String port){
        if (port == null) {
            return false;
        }
 
        Matcher matcher = PORT_PATTERN.matcher(port);
 
        return matcher.matches();

    }
    public static String GetIpAdress(BufferedReader reader) throws IOException {
    	String ipAddr;
    	System.out.println("Entrez l'addresse ip du serveur:");
    	while (!isValidInet4Address(ipAddr = reader.readLine())) {
    		System.out.println("l'address ip n'est pas valide, entrz une nouvelle");
    		
    	}
    	System.out.println("l'address ip est valide");
    	return ipAddr;
    }
    
    public static int GetPort(BufferedReader reader) throws IOException {
    	String port;
    	
    	System.out.println("Entrez le port du serveur:");
    	while (!isValidPort(port = reader.readLine())) {
    		System.out.println("le port n'est pas valide");
    		
    	}
    	System.out.println("le port est valide");
    	return Integer.parseInt(port);
    }
    
    public static void main(String[] args)
    {
        // Validate an IPv4 address
        if (isValidInet4Address(INET4ADDRESS) && isValidPort(PORT)) {
            System.out.print("The IP address " + INET4ADDRESS + " and the port " + PORT + "are valid");
        }
        else if (!isValidInet4Address(INET4ADDRESS) && isValidPort(PORT)) {
            System.out.print("The IP adress entered doesn't respect the format IPv4");
        }
        else if (!isValidPort(INET4ADDRESS) && isValidInet4Address(INET4ADDRESS)) {
            System.out.print("The port entered isn't between 5000 and 5050");
        }
        else {
            System.out.print("The information entered aren't valid");
        }
    }
}