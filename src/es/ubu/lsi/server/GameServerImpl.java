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
import java.util.Map.Entry;

import es.ubu.lsi.common.ElementType;
import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.GameResult;
import es.ubu.lsi.common.Util;

public class GameServerImpl implements GameServer {

	// ATRIBUTOS
	// ------------------------

	private ServerSocket serverSocket;
	private final int PORT = 1500;

	private final static int MAX_PLAYERS = 2;
	private int numPlayers;

	ServerThreadForClient ct1;
	ServerThreadForClient ct2;

	private int currentPlayerId = 0;

	// CONSTRUCTOR
	// ------------------------

	public GameServerImpl() {
		super();
		try {
			this.serverSocket = new ServerSocket(this.PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Inicializando servidor del juego Piedra, papel o tijera...\n-------");
		Util.printFormated("Servidor a la escucha en: " + this.serverSocket.getLocalSocketAddress().toString(), "i");

	}

	// METODOS DE CLASE
	// ------------------------

	public int getNumPlayers() {
		return this.numPlayers;
	}

	@Override
	public void startup() {
		ct1 = new ServerThreadForClient(1);
		ct2 = new ServerThreadForClient(2);
	}

	/**
	 * Cierra los flujos de entrada/salida del servidor y el socket correspondiente
	 * a cada cliente.
	 */
	@Override
	public void shutdown() {
		// Finalizamos cada ServerThreadForClient almacenado
		// clientThreads.forEach((key, value) -> value.finalize());
		// //threads.forEach(t -> t.interrupt());

		ct1.finalize();
		ct2.finalize();
	}

	/**
	 * Envía el resultado a los clientes de una determinada sala (flujo de salida).
	 */
	@Override
	public void broadcastRoom(GameElement element) {
		// A cada cliente se le envia el GameElement serializado

		// for (Entry<Integer, ServerThreadForClient> e : clientThreads.entrySet())
		// e.getValue().send(element);

		ct1.send(element);
		ct2.send(element);

		// clientThreads.forEach((id, thread) -> thread.send(element));
	}

	public void broadcastRoom(GameResult r) {
		// A cada cliente se le envia el GameElement serializado
		ct1.send(r);
		ct2.send(r);
	}

	/**
	 * Elimina un cliente de la lista
	 */
	@Override
	public void remove(int id) {
		getClientThread(id).finalize();
	}


	public ServerThreadForClient getClientThread(int id) {
		if (id == 1)
			return ct1;
		else if (id == 2)
			return ct2;
		else
			return null;
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
		private int playerId;

		// CONSTRUCTOR
		// ----------------
		public ServerThreadForClient(int playerId) {

			try {
				// Se crea el socket y se
				// Acepta la solicitud entrante al socket
				this.clientSocket = serverSocket.accept();

				// Creamos un Stream de datos de entrada y otro de salida
				this.out = new PrintWriter(clientSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			} catch (IOException e) {
				e.printStackTrace();
			}

			this.playerId = playerId;

			t = new Thread(this);
			t.start();
		}

		// METODOS
		// -----------------
		@Override
		public void run() {

			Util.printFormated("Conexion entrante de: " + this.clientSocket.getRemoteSocketAddress().toString(), "+");

			// Recibir el nombre de usuario
			try {
				// this.username = in.readLine();
				this.username = (String) Util.readFrom(in);

			} catch (Exception e) {
			}

			numPlayers++;
			Util.printFormated("Se ha conectado el usuario " + this.username, "+");

			// Esperar hasta que haya 2 jugadores
			while (numPlayers < MAX_PLAYERS) {
				Util.printFormated("Esperando a que se conecte otro jugador...", "i");

				// try {
				// Thread.sleep(15000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
			}

			// Comienza la partida
			broadcastRoom(GameResult.WAITING);

			jugarPartida();

			System.out.println("Finalizando...");
			finalize();

		}

		public int getOponentId() {
			if (playerId == 1)
				return 2;
			else
				return 1;
		}

		public GameElement getOponentGame() {
			//BufferedReader out = getThread(getOponentId()).in;
			return (GameElement) Util.readFrom(getClientThread(getOponentId()).in);

		}

		public GameElement getMyGame() {
			//BufferedReader out = getThread(getOponentId()).in;
			return (GameElement) Util.readFrom(in);

		}


		/**
		 * Cierra los streams y socket y finaliza el hilo
		 */
		public void finalize() {

			try {
				// Cerramos los stream
				this.in.close();
				this.out.close();

				// Cerramos los socket
				this.clientSocket.close();

			} catch (IOException e) {
			}

			// Por ultimo se interrumpe el thread
			this.t.interrupt();

		}

		public void send(Object data) {
			this.out.println(Util.serialize(data));
		}

		/**
		 * Método que gestiona las acciones de los jugadores
		 */
		public void jugarPartida() {

			// int numPartida = 0;

			while (true) {

				GameElement player1Game = getMyGame();
				GameElement player2Game = getOponentGame();

				/*
				 * Se espera hasta que ambos jugadores envien su respuesta
				 * enviamos la opcion waiting continuamente
				 */
				while (player1Game == null || player2Game == null) {

					Util.printFormated("Esperando a que los jugadores envien su jugada", "i");
					broadcastRoom(new GameElement(0, GameResult.WAITING));

					// try {
					// Thread.sleep(5000);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }

					player1Game = getMyGame();
					player2Game = getOponentGame();

				}

				if (player1Game.getOption().equals(ElementType.LOGOUT)) {
					// Eliminamos al jugador
					remove(this.playerId);
					break;
				}

				// Se juega esta partida
				// numPartida++;

				int winner = obtenerResultados(player1Game, player2Game);

				// Util.printFormated("Resultado de la partida " + this.username + " vs " +
				// o.username + " numero " + numPartida, "*");

				if (this.playerId == winner) {
					send(GameResult.WIN);
					System.out.println("Gana " + this.username);
				} else if (-1 == winner) {
					send(GameResult.DRAW);
					System.out.println("Ha habido un empate");
				} else {
					send(GameResult.LOSE);
				}

			}

		}

		/**
		 * Obtiene el id del jugador que gana la partida
		 * 
		 * @param optionPlayer1
		 * @param optionPlayer2
		 * @return
		 */
		public int obtenerResultados(GameElement optP1, GameElement optP2) {

			ElementType p1 = optP1.getOption();
			ElementType p2 = optP2.getOption();

			if (p1.equals(p2))
				return -1;
			else if (p1.equals(ElementType.PIEDRA))
				if (p2.equals(ElementType.TIJERA))
					return optP1.getPlayerId();
				else
					return optP2.getPlayerId();
			else if (p1.equals(ElementType.PAPEL))
				if (p2.equals(ElementType.PIEDRA))
					return optP1.getPlayerId();
				else
					return optP2.getPlayerId();
			else if (p1.equals(ElementType.TIJERA))
				if (p2.equals(ElementType.PAPEL))
					return optP1.getPlayerId();
				else
					return optP2.getPlayerId();
			else
				return -2;
		}

		// GETTERS
		// -----------------

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
		 * 
		 * @return el id de la sala
		 */
		public int getIdRoom() {
			// Como solo va a existir una sala, se hardcodea el id
			return 1;
		}

		/**
		 * 
		 * @return
		 */
		public Thread getThread() {
			return t;
		}

	}
}
