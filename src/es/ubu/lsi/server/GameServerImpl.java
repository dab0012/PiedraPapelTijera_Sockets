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

	UserConection ct1;
	UserConection ct2;

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

		ct1 = new UserConection(serverSocket);
		ct2 = new UserConection(serverSocket);

		new Thread(new ServerThreadForClient()).start();


		
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

		ct1.send(element);
		ct2.send(element);

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
		//getClientThread(id).finalize();
	}



	public static void main(String[] args) {
		GameServerImpl game = new GameServerImpl();
		game.startup();

	}


	public void jugarPartida() {

		// int numPartida = 0;
		
		
		String usernameP1 = (String) Util.readFrom(ct1.getIn());
		ct1.setUsername(usernameP1);
		String usernameP2 = (String) Util.readFrom(ct2.getIn()); 
		ct2.setUsername(usernameP2);

		while (true) {




			//Informamos del comienzo de la partida
			broadcastRoom(new GameElement(0, GameResult.WAITING));


			GameElement player1Game = (GameElement) Util.readFrom(ct1.getIn());
			GameElement player2Game = (GameElement) Util.readFrom(ct2.getIn());

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

				player1Game = (GameElement) Util.readFrom(ct1.getIn());
				player2Game = (GameElement) Util.readFrom(ct2.getIn());

			}

			if (player1Game.getOption().equals(ElementType.LOGOUT)) {
				// Eliminamos al jugador
				remove(player1Game.getPlayerId());
				break;
			}
			else if (player2Game.getOption().equals(ElementType.LOGOUT)) {
				// Eliminamos al jugador
				remove(player2Game.getPlayerId());
				break;
			}

			// Se juega esta partida
			// numPartida++;

			Util.printFormated("El usuario " + ct1.getUsername() + " ha enviado " +  player1Game.getOption().toString(), "i");
			Util.printFormated("El usuario " + ct2.getUsername() + " ha enviado " +  player2Game.getOption().toString(), "i");


			int winner = obtenerResultados(player1Game, player2Game);

			Util.printFormated("Resultado de la partida " + usernameP1 + " vs " + usernameP2 , "*");


			if (winner == player1Game.getPlayerId()){
				ct1.send(GameResult.WIN);
				ct2.send(GameResult.LOSE);
				Util.printFormated("Gana " + usernameP1, "i");

			}
			else if (winner == player2Game.getPlayerId()){
				ct2.send(GameResult.WIN);
				ct1.send(GameResult.LOSE);
				Util.printFormated("Gana " + usernameP2, "i");

			}
			else if (winner == -1){
				ct1.send(GameResult.DRAW);
				ct2.send(GameResult.DRAW);
				Util.printFormated("Ha habido un empate", "i");

			}


		}
		


	}


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


	/////////////////

	/**
	 * Clase que gestiona la comunicacion con el cliente desde el servidor
	 */
	public class ServerThreadForClient implements Runnable {


		// METODOS
		// -----------------
		@Override
		public void run() {

			jugarPartida();

			System.out.println("Finalizando...");

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
		 * Obtiene el id de la sala
		 * 
		 * @return el id de la sala
		 */
		public int getIdRoom() {
			// Como solo va a existir una sala, se hardcodea el id
			return 1;
		}



	}



}



