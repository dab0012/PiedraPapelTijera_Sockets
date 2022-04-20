package es.ubu.lsi.common;

import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Base64;

public class Util {
	
	
	public static final String RESET = "\033[0m"; // Text Reset 
	
	// Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE
	
	public static void sendTo(PrintWriter w, Object o) {
		w.println(serialize(o));
	}
	
	public static Object readFrom(BufferedReader b) {
		try {
			return deserialize(b.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

    public static String genMessage(String message, String type){
    	
    	String color = "";
    	switch(type) {
    	
    		case "*":
    			color = GREEN;
    			break;
    		case "+":
    			color = RED;
    			break;
    		case "i":
    			color = CYAN;
    			break;
    		case "?":
    			color = YELLOW;
    			break;
    			
    	
    	
    	}
    	
        return color + "[" + type + "][" + getTime()  + "] " + message + RESET;
    }
    
    public static void printFormated(String message, String type){
    	System.out.println(genMessage(message, type));
    }

    public static String getTime(){
		Date d = new Date();
		int min = d.getMinutes();
		int sec = d.getSeconds();
		int hor = d.getHours();
	
		return hor+":"+min+":"+sec;
	}
    

        /**
     * Serializa un objeto
     * @param o
     * @return string con el objeto codificado en base64
     */
    public static String serialize(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject(o) ;
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }

    /**
     * Obtiene un objeto a partir de un serial
     * @param b64Serial
     * @return
     */
	public static Object deserialize(String b64Serial){

		byte [] data = Base64.getDecoder().decode(b64Serial);
		Object o = null;

		try {

			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			o = ois.readObject();
			ois.close();

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return o;
	}


	public static void printLogo(){
		System.out.println(GREEN + ".---------------------.\n| Piedra Papel Tijera |\n'---------------------'" + RESET);
	}

}
