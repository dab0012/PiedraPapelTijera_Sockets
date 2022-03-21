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
import java.net.UnknownHostException;

import es.ubu.lsi.common.GameElement;

public class GameClientImpl implements GameClient {

	private String server;
	private int port;
	private String username;

	class GameClientListener implements Runnable {

		@Override
		public void run() {
			

			try (
					Socket echoSocket = new Socket(server, port);
					PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
					BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
			) {
				String userInput;
				while ((userInput = stdIn.readLine()) != null) {
					out.println(userInput);
					System.out.println("Se envia al servidor tu respuesta: " + in.readLine());
				}
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host " + server);
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to " + server);
				System.exit(1);
			}

		}

	}

	public GameClientImpl(String server, int port, String username) {
		super();
		this.server = server;
		this.port = port;
		this.username = username;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Inicia la conexion con el servidor
	 */
	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return false;
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

}
