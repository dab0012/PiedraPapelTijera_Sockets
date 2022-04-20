/**
 * @author Daniel Alonso Báscones (dnllns)
 * @version PiedraPapelTijera-ubu-sdis-1
 * 2022-03-14 16:58:35 +0100
 * 
 */
package es.ubu.lsi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

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



		ct1 = new UserConection(serverSocket);
		ct2 = new UserConection(serverSocket);

		
		//Solicitar nombres de usuario a los jugadores		
		String usernameP1 = (String) Util.readFrom(ct1.getIn());
		ct1.setUsername(usernameP1);
		String usernameP2 = (String) Util.readFrom(ct2.getIn()); 
		ct2.setUsername(usernameP2);


		int numRonda = 0;

		//Bucle del juego
		while (true) {


			//Informamos del comienzo de la partida
			broadcastRoom(new GameElement(0, GameResult.WAITING));

			Util.printFormated("Ronda " + ++numRonda, "i");
			Util.printFormated("Esperando a que los jugadores envien su jugada", "i");
			GameElement player1Game = (GameElement) Util.readFrom(ct1.getIn());
			GameElement player2Game = (GameElement) Util.readFrom(ct2.getIn());


			
			//Se controla la solicitud de final de partida por parte de los jugadores
			boolean playerDisconected = false;

						
			//Se informa al otro participante de que ha habido una desconexion de su contrincante
			if (player1Game.getOption().equals(ElementType.LOGOUT)) {
				// Eliminamos al jugador
				remove(player1Game.getPlayerId());
				Util.printFormated("El usuario " + ct1.getUsername() + " se ha desconectado", "+");
				Util.printFormated("Esperando a que se conecte un contrincate", "+");
				playerDisconected = true;
				ct2.send(new GameElement(player2Game.getPlayerId() , ElementType.DISCONECTED));
				ct1 = new UserConection(serverSocket);
				usernameP1 = (String) Util.readFrom(ct1.getIn());
				ct1.setUsername(usernameP1);
	
			}
			if (player2Game.getOption().equals(ElementType.LOGOUT)) {
				// Eliminamos al jugador
				remove(player2Game.getPlayerId());
				Util.printFormated("El usuario " + ct1.getUsername() + " se ha desconectado", "i");
				Util.printFormated("Esperando a que se conecte un contrincate", "+");
				playerDisconected = true;
				ct1.send(new GameElement(player1Game.getPlayerId() , ElementType.DISCONECTED));
				ct2 = new UserConection(serverSocket);
				usernameP2 = (String) Util.readFrom(ct2.getIn()); 
				ct2.setUsername(usernameP2);

			}
			//Se informa a los clientes de que ambos jugadores estan listos
			else{
				ct1.send(new GameElement(player1Game.getPlayerId() , ElementType.CONTINUE));
				ct2.send(new GameElement(player2Game.getPlayerId() , ElementType.CONTINUE));
			}


			/**
			 * Comienzo de la partida "Evaluacion de piedra papel tijera"
			 */

			if (!playerDisconected){

				Util.printFormated("El usuario " + ct1.getUsername() + " ha enviado " +  player1Game.getOption().toString(), "i");
				Util.printFormated("El usuario " + ct2.getUsername() + " ha enviado " +  player2Game.getOption().toString(), "i");

				//Informar a los jugadores de la jugada del rival
				Util.sendTo(ct1.getOut(), "El usuario " + ct2.getUsername() + " ha enviado " +  player2Game.getOption().toString());
				Util.sendTo(ct2.getOut(), "El usuario " + ct1.getUsername() + " ha enviado " +  player1Game.getOption().toString());


				//Evaluacion
				int winner = obtenerResultados(player1Game, player2Game);

				//Mostrar el resultado en el servidor y responder a los jugadores
				Util.printFormated("Resultado de la partida " + usernameP1 + " vs " + usernameP2 , "*");

				if (winner == player1Game.getPlayerId()){
					
					ct1.send(new GameElement(player1Game.getPlayerId() ,GameResult.WIN));
					ct2.send(new GameElement(player2Game.getPlayerId() ,GameResult.LOSE));

					Util.printFormated("Gana " + usernameP1, "*");
				}
				else if (winner == player2Game.getPlayerId()){
					ct2.send(new GameElement(player2Game.getPlayerId() ,GameResult.WIN));
					ct1.send(new GameElement(player1Game.getPlayerId() ,GameResult.LOSE));

					Util.printFormated("Gana " + usernameP2, "*");
				}
				else if (winner == -1){
					ct1.send(new GameElement(player1Game.getPlayerId() ,GameResult.DRAW));
					ct2.send(new GameElement(player2Game.getPlayerId() ,GameResult.DRAW));

					Util.printFormated("Ha habido un empate", "*");
				}
			}
			else {

				Util.printFormated("Se reinicia la partida debido a la desconexion de un jugador", "i");

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

			Util.printLogo();
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



