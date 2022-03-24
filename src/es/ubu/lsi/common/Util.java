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
        return "[" + type + "][" + getTime()  + "] " + message;
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
}
