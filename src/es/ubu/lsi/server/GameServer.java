/**
 * @author Daniel Alonso Báscones (dnllns)
 * @version PiedraPapelTijera-ubu-sdis-1
 * 2022-03-14 16:58:35 +0100
 * 
 */

package es.ubu.lsi.server;

import es.ubu.lsi.common.GameElement;


/**
 * Define la signatura de los métodos de arranque, multidifusión y eliminación de clientes.
 * @author dnllns
 *
 */
public interface GameServer {

	public void startup();
	public void shutdown();
	public void broadcastRoom(GameElement element);
	public void remove(int id);
	
}
