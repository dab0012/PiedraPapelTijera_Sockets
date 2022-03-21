/**
 * @author Daniel Alonso Báscones (dnllns)
 * @version PiedraPapelTijera-ubu-sdis-1
 * 2022-03-14 16:58:35 +0100
 * 
 */
package es.ubu.lsi.server;

import es.ubu.lsi.common.GameElement;


public class GameServerImpl implements GameServer{
	
	private final int PORT = 1500;
	
	class ServerThreadForClient implements Runnable{

		
		/**
		 * Como solo va a existir una sala, se hardcodea el id
		 * @return el id de la sala
		 */
		public int getIdRoom(){
			return 1;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}


	/**
	 * Constructor
	 * @param port
	 */
	public GameServerImpl() {
		super();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 *  Implementa el bucle con el servidor de sockets (ServerSocket), esperando y aceptado 
	 *  peticiones. Ante cada petición entrante y aceptada, se instancia un nuevo ServerThreadForClient 
	 *  y se arranca el hilo correspondiente para que cada cliente tenga su hilo independiente asociado 
	 *  en el servidor (con su socket, flujo de entrada y flujo de salida). 
	 *  Es importante ir guardando un registro de los hilos creados para poder posteriormente realizar 
	 *  el push de los mensajes y un apagado correcto.
	 */
	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  Cierra los flujos de entrada/salida del servidor y el socket correspondiente a cada cliente.
	 */
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  Envía el resultado a los clientes de una determinada sala (flujo de salida).
	 */
	@Override
	public void broadcastRoom(GameElement element) {
		// TODO Auto-generated method stub
		
	}

	/**
	 *  Elimina un cliente de la lista
	 */
	@Override
	public void remove(int id) {
		// TODO Auto-generated method stub
		
	}

}
