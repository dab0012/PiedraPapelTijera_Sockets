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


    private int playerId;
    private ElementType option;
    private GameResult result;

    public GameElement(int playerId, ElementType option) {
        this.playerId = playerId;
        this.option = option;
    }

    public GameElement(int playerId, GameResult result) {
        this.playerId = playerId;
        this.result = result;
    }


    public int getPlayerId() {
        return playerId;
    }

    public ElementType getOption() {
        return option;
    }

    public GameResult getResult() {
        return result;
    }



}
