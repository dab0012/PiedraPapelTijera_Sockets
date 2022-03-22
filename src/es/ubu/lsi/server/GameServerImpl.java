/**
 * @author Daniel Alonso Báscones (dnllns)
 * @version PiedraPapelTijera-ubu-sdis-1
 * 2022-03-14 16:58:35 +0100
 * 
 */
package es.ubu.lsi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.Serial;

public class GameServerImpl implements GameServer {


	//ATRIBUTOS
	//------------------------

	private final static int PORT = 1500;
	private final static int MAX_PLAYERS = 2;
	private int numPlayers;
	private HashMap<Integer, ServerThreadForClient> clientThreads;
	private int currentPlayerId = 0;


	//CONSTRUCTOR
	//------------------------

	/**
	 * 
	 */
	public GameServerImpl() {
		super();
		clientThreads = new HashMap<Integer, ServerThreadForClient>();
	}


	//METODOS DE CLASE
	//------------------------

	/**
	 * 
	 * @return
	 */
	public int getNumPlayers() {
		return this.numPlayers;
	}

	/**
	 * 
	 */
	@Override
	public void startup() {

		//Se inicializan 1 ServerThreadForClient para cada jugador
		//El id del jugador se asigna en este momento, y es secuencial
		while (numPlayers < MAX_PLAYERS){
			clientThreads.put(++currentPlayerId, new ServerThreadForClient());
		}
	}

	/**
	 * Cierra los flujos de entrada/salida del servidor y el socket correspondiente
	 * a cada cliente.
	 */
	@Override
	public void shutdown() {
		//Finalizamos cada ServerThreadForClient almacenado
		clientThreads.forEach((key, value) -> value.finalize());
	}

	/**
	 * Envía el resultado a los clientes de una determinada sala (flujo de salida).
	 */
	@Override
	public void broadcastRoom(GameElement element) {
		//A cada cliente se le envia el GameElement serializado
		clientThreads.forEach((id, thread) -> thread.getOut().println(Serial.serialize(element)));
	}

	/**
	 * Elimina un cliente de la lista
	 */
	@Override
	public void remove(int id) {
		clientThreads.remove(id);
	}



	public static void main(String[] args) {

	}

	/////////////////

	class ServerThreadForClient implements Runnable {


		private ServerSocket serverSocket;
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;


		//CONSTRUCTOR
		//----------------
		public ServerThreadForClient() {

			try {
				//Se crea el socket y se
				//Acepta la solicitud entrante al socket
				this.serverSocket = new ServerSocket(GameServerImpl.PORT);
				this.clientSocket = serverSocket.accept();
				
				//Creamos un Stream de datos de entrada y otro de salida
				this.out = new PrintWriter(clientSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (IOException e) {
			}
		}

		//METODOS
		//-----------------
		@Override
		public void run() {

		}

		/**
		 * Cierra los streams y los socket
		 */
		public void finalize(){

			try {
				//Cerramos los stream
				this.in.close();
				this.out.close();
				//Cerramos los socket
				this.clientSocket.close();
				this.serverSocket.close();
			} catch (IOException e) {

			}
		}


		//GETTERS
		//-----------------

		/**
		 * 
		 * @return
		 */
		public ServerSocket getServerSocket() {
			return serverSocket;
		}

		/**
		 * 
		 * @return
		 */
		public Socket getClientSocket() {
			return clientSocket;
		}

		/**
		 * 
		 * @return
		 */
		public PrintWriter getOut() {
			return out;
		}

		/**
		 * 
		 * @return
		 */
		public BufferedReader getIn() {
			return in;
		}

		/**
		 * Obtiene el id de la sala
		 * @return el id de la sala
		 */
		public int getIdRoom() {
			//Como solo va a existir una sala, se hardcodea el id
			return 1;
		}


	}

}
