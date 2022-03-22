package es.ubu.lsi.common;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;


/**
 * Clase de utilidad para serializar y leer objetos serializados
 */
public class Serial {
    

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
