/**
 * @author Daniel Alonso Báscones (dnllns)
 * @version PiedraPapelTijera-ubu-sdis-1
 * 2022-03-14 16:58:35 +0100
 * 
 */

package es.ubu.lsi.common;



/**
 * Define el mensaje que se envía al servidor, incluyendo la jugada actual del jugador.
 * @author dnllns
 *
 */
public class GameElement implements java.io.Serializable {


    private String status;
    private String option;

    public GameElement(String status, String option) {
        this.status = status;
        this.option = option;
    }

}
