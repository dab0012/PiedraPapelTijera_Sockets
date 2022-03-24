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
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.GameResult;
import es.ubu.lsi.common.Util;

public class GameServerImpl implements GameServer {


	//ATRIBUTOS
	//------------------------

	private ServerSocket serverSocket;
	private final int PORT = 1500;
	
	private final static int MAX_PLAYERS = 2;
	private int numPlayers;
	private static HashMap<Integer, ServerThreadForClient> clientThreads;
	//private List<Thread> threads;
	private int currentPlayerId = 0;


	//CONSTRUCTOR
	//------------------------

	/**
	 * 
	 */
	public GameServerImpl() {
		super();
		try {
			this.serverSocket = new ServerSocket(this.PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		clientThreads = new HashMap<Integer, ServerThreadForClient>();
		
		System.out.println("Inicializando servidor del juego Piedra, papel o tijera...\n-------");
		System.out.println(Util.genMessage("Servidor a la escucha en: " + this.serverSocket.getLocalSocketAddress().toString(), "i"));

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
		int c = 0;
		while (c < MAX_PLAYERS){
			ServerThreadForClient t = new ServerThreadForClient();
			clientThreads.put(++currentPlayerId, t);
			c++;
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
		//threads.forEach(t -> t.interrupt());
	}

	/**
	 * Envía el resultado a los clientes de una determinada sala (flujo de salida).
	 */
	@Override
	public void broadcastRoom(GameElement element) {
		//A cada cliente se le envia el GameElement serializado
		
		for (Entry<Integer, ServerThreadForClient> e : clientThreads.entrySet())
			e.getValue().send(element);
			
			
			
		//clientThreads.forEach((id, thread) -> thread.send(element));
	}

	public void broadcastRoom(GameResult r) {
		//A cada cliente se le envia el GameElement serializado
		
		for (Entry<Integer, ServerThreadForClient> e : clientThreads.entrySet())
			e.getValue().send(r);
		
		//clientThreads.forEach((id, thread) -> thread.send(r));
	}

	/**
	 * Elimina un cliente de la lista
	 */
	@Override
	public void remove(int id) {
		clientThreads.get(id).finalize();
		clientThreads.remove(id);
	}


	public static void main(String[] args) {
		GameServerImpl game = new GameServerImpl();
		game.startup();

	}

	/////////////////

	/**
	 * Clase que gestiona la comunicacion con el cliente desde el servidor
	 */
	public class ServerThreadForClient implements Runnable {

		private Thread t;
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		private String username;
	


		//CONSTRUCTOR
		//----------------
		public ServerThreadForClient() {

			try {
				//Se crea el socket y se
				//Acepta la solicitud entrante al socket
				this.clientSocket = serverSocket.accept();
				
				//Creamos un Stream de datos de entrada y otro de salida
				this.out = new PrintWriter(clientSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			} catch (IOException e) {
				e.printStackTrace();	
			}

			

			t = new Thread(this);
			t.start();
		}

		//METODOS
		//-----------------
		@Override
		public void run() {

			System.out.println( 
					Util.genMessage(
							"Conexion entrante de: " + this.clientSocket.getRemoteSocketAddress().toString(),
							"+"
					)
			);
			
			//Recibir el nombre de usuario
			try{ 
				//this.username = in.readLine();
				this.username = (String) Util.readFrom(in);
				
			} catch (Exception e) {}
			numPlayers++;
			System.out.println(Util.genMessage("Se ha conectado el usuario " + this.username, "+"));
			
			//Esperar hasta que haya 2 jugadores
			while (numPlayers < MAX_PLAYERS){
				System.out.println(Util.genMessage("Esperando a que se conecte otro jugador...", "i"));
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			//Enviamos la señal de comenzar a jugar
			broadcastRoom(GameResult.DRAW);

			
			
			

			System.out.println("Finalizando...");
			finalize();

		}
		

		/**
		 * Cierra los streams y socket y finaliza el hilo
		 */
		public void finalize(){

			try {
				//Cerramos los stream
				this.in.close();
				this.out.close();

				//Cerramos los socket
				this.clientSocket.close();

			} catch (IOException e) {}

			//Por ultimo se interrumpe el thread
			this.t.interrupt();
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

		/**
		 * 
		 * @return
		 */
		public Thread getThread(){
			return t;
		}

		public void send(Object data){
			this.out.println(Util.serialize(data));
		}



	}

}
