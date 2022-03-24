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

import es.ubu.lsi.common.GameElement;
import es.ubu.lsi.common.GameResult;
import es.ubu.lsi.common.Util;

public class GameClientImpl implements GameClient {

	private String server;
	private int port;
	private String username;
	private GameClientListener com;


	public GameClientImpl(String server, int port, String username) {
		super();
		this.server = server;
		this.port = port;
		this.username = username;
	
	}

	public static void main(String[] args) {




		System.out.println("Inicializando cliente...");
		System.out.println("------------------------");

		System.out.println("Introduce tu nombre de usuario: ");

		//For test
		String randomUsername = "pepito_" + (int)(Math.random()*1000);
		GameClientImpl client = new GameClientImpl("localhost", 1500, randomUsername);
		client.start();

	}

	/**
	 * Inicia la conexion con el servidor
	 */
	@Override
	public boolean start() {

		com = new GameClientListener();
		return true;
	}

	/**
	 * 
	 */
	@Override
	public void sendElement(GameElement element) {
		// TODO Auto-generated method stub

	}


	/**
	 * Desconecta a el cliente del servidor
	 */
	@Override
	public void disconnect() {

	}



	class GameClientListener implements Runnable {

		private Socket s;
		private PrintWriter out; 
		private BufferedReader in;
		private BufferedReader stdIn; 
		private Thread t;
		private GameResult status;

		public GameClientListener(){
			try {
				this.s = new Socket(server, port);
				this.out = new PrintWriter(this.s.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				this.stdIn = new BufferedReader(new InputStreamReader(System.in));
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.t = new Thread(this);
			t.start();
		}


		public GameResult readStatus() throws IOException{
			String data = in.readLine();
			if (data == null)
				return null;
			else
				return (GameResult) Util.deserialize(in.readLine());
		}


		@Override
		public void run() {
			



			//Enviamos al servidor nuestro nombre de usuario
			out.println(username);

			//Esperamos a que empiece la partida
			
			try {
				while (readStatus() == null || !readStatus().equals(GameResult.DRAW) ){

					System.out.println(Util.genMessage("Esperando a que comience la partida", "i"));
					Thread.sleep(5000);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}




		}

	}

}
