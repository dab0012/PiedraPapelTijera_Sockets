/**
 * @author Daniel Alonso Báscones (dnllns)
 * @version PiedraPapelTijera-ubu-sdis-1
 * 2022-03-14 16:58:35 +0100
 *
 */

package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import es.ubu.lsi.common.ElementType;
import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.GameResult;
import es.ubu.lsi.common.Util;

public class GameClientImpl implements GameClient {

	private String server;
	private int port;
	private String username;
	private GameClientListener clientListener;
	private int id;

	public GameClientImpl(String server, int port, String username, int id) {
		super();
		this.server = server;
		this.port = port;
		this.username = username;
		this.id = id;
		this.clientListener = new GameClientListener();


	}

	public static void main(String[] args) {



		System.out.println("Inicializando cliente...");
		System.out.println("------------------------");

		//Util.printFormated("Introduce tu nombre de usuario: ", "?");

		//For test
		//String randomUsername = "pepito_" + (int)(Math.random()*1000);


		int randomId = (int)(Math.random()*1000);

		GameClientImpl client1 = new GameClientImpl(args[0], 1500, args[1], randomId);

		client1.start();


	}

	/**
	 * Inicia la conexion con el servidor
	 */
	@Override
	public boolean start() {

		//Enviamos al servidor nuestro nombre de usuario

		//out.println(username);
		Util.printFormated("Enviando el username al servidor", "i");
		Util.sendTo(clientListener.out, username);
		clientListener.t.start();
		return true;
	}

	/**
	 *
	 */
	@Override
	public void sendElement(GameElement element) {
		Util.sendTo(clientListener.out, element);
	}


	/**
	 * Desconecta a el cliente del servidor
	 */
	@Override
	public void disconnect() {

	}

	public GameElement selectOption(){

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String option = "";

		do {

			Util.printFormated("Introduce tu jugada (piedra, papel, tijera o logout) ", "?");
			try {
				option = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}


		}
		while(!option.equalsIgnoreCase("piedra") && !option.equalsIgnoreCase("papel") &&
		!option.equalsIgnoreCase("tijera") && !option.equalsIgnoreCase("logout"));


		GameElement g;
		if (option.equalsIgnoreCase("piedra")){
			g = new GameElement(this.id, ElementType.PIEDRA);
		}
		else if (option.equalsIgnoreCase("papel")){
			g = new GameElement(this.id, ElementType.PAPEL);
		}
		else if (option.equalsIgnoreCase("tijera")){
			g = new GameElement(this.id, ElementType.TIJERA);
		}
		else {
			g = new GameElement(this.id, ElementType.LOGOUT);
		}

		return g;

	}


	class GameClientListener implements Runnable {

		private Socket s;
		private PrintWriter out;
		private BufferedReader in;
		// private BufferedReader stdIn;
		private Thread t;

		public GameClientListener(){
			try {
				this.s = new Socket(server, port);
				this.out = new PrintWriter(this.s.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				// this.stdIn = new BufferedReader(new InputStreamReader(System.in));
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.t = new Thread(this);
		}


		public GameResult readGameResult() throws IOException{
			String data = in.readLine();
			if (data == null)
				return null;
			else {
				GameElement ge = (GameElement) Util.deserialize(data);
				GameResult gr = ge.getResult();

				return  gr;
			}
		}

		public ElementType readOption() throws IOException{
			String data = in.readLine();
			if (data == null)
				return null;
			else {
				GameElement ge = (GameElement) Util.deserialize(data);
				return ge.getOption();
			}
		}


		@Override
		public void run() {


			int numRonda = 0;
			while (true){

				//Esperamos a que el servidor indique el
				//comienzo de la partida
				try {

					//Mientras el servidor no nos responda con Waiting
					//El cliente se bloquea
					GameResult r = readGameResult();
					while (!r.equals(GameResult.WAITING) ){

						Util.printFormated("Esperando a que comience la partida", "i");
						Thread.sleep(5000);

					}

					Util.printFormated("Comienza la partida", "i");

				} catch (Exception e) {
					e.printStackTrace();
				}

				Util.printFormated("Ronda " + ++numRonda, "i");


				//Seleccion de jugada
				GameElement opt = selectOption();

				//Envio de jugada al servidor
				Util.sendTo(out, opt);

				//Se comprueba que el cliente haya decidido finalizar la ejecucion
				ElementType o = opt.getOption();
				if (o.equals(ElementType.LOGOUT))
					break;

				Util.printFormated("Esperando al oponente...", "i");


				//Comprobar ok del servidor para jugar
				ElementType serverOk = null;
				try {
					while (serverOk == null){
						serverOk = readOption();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				//Recibir y Mostrar mensaje con la jugada del oponente 
				String message = (String) Util.readFrom(this.in);
				Util.printFormated(message, "i");
				

				
				if (serverOk.equals(ElementType.CONTINUE)){


					//Esperar respuesta del servidor
					GameResult r = null;
					try {

						//Mientras el servidor no nos responda con un resultado
						//El cliente se bloquea
						r = readGameResult();
						while (r == null){
							Thread.sleep(5000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}



					if (r.equals(GameResult.WIN))
						Util.printFormated("Has ganado la partida", "*");
					else if (r.equals(GameResult.LOSE))
						Util.printFormated("Has perdido la partida", "*");
					else
						Util.printFormated("Has empatado", "*");
				}
				else {
					Util.printFormated("Se procede a reiniciar la partida por la desconexion del contrincante", "+");
				}

			}

			Util.printFormated("Fin de la ejecucion del cliente", "i");

		}

	}

}
