/**
 * @author Daniel Alonso Báscones (dnllns)
 * @version PiedraPapelTijera-ubu-sdis-1
 * 2022-03-14 16:58:35 +0100
 *
 */

package es.ubu.lsi.client;

import es.ubu.lsi.common.GameElement;

public interface GameClient {

	public boolean start();
	public void sendElement(GameElement element);
	public void disconnect();


}
