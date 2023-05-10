package com.example.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.chat.client.ClientApplication;
import javafx.application.Platform;

/* Thread Class for each incoming client */
public class ClientThread implements Runnable {

	/* The socket of the client */
	private Socket clientSocket;
	/* Server class from which thread was called */
	private Server baseServer;
	private BufferedReader incomingMessageReader;
	private PrintWriter outgoingMessageWriter;
	/* The name of the client */
	private String clientName;

	public ClientThread(){};

	public ClientThread(Socket clientSocket, Server baseServer) {
		this.setClientSocket(clientSocket);
		this.baseServer = baseServer;
		try {
			/*
			 * Reader to get all incoming messages that the client passes to the
			 * server
			 */
			incomingMessageReader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			/* Writer to write outgoing messages from the server to the client */
			outgoingMessageWriter = new PrintWriter(
					clientSocket.getOutputStream(), true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			this.clientName = getClientNameFromNetwork();
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					baseServer.clientNames.add(clientName + " - "
							+ clientSocket.getRemoteSocketAddress()
					); // /ip 로 나옴
					//추가

					baseServer.writeToAllSockets(clientName+"님이 참가하셨습니다.", false);
					//
				}

			});
			String inputToServer;
			while (true) {
				inputToServer = incomingMessageReader.readLine();
				baseServer.writeToAllSockets(inputToServer, true);
			}

		} catch (SocketException e) {
			baseServer.clientDisconnected(this, true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeToServer(String input, ClientThread thread) {
//		Date now = new Date(System.currentTimeMillis());
//		SimpleDateFormat s = new SimpleDateFormat("[a hh:mm] ");
		System.out.println("ClientTrhead에서: "+thread);
		System.out.println("ClientTrhead에서 baseServer의 쓰레드: "+baseServer.clientThreads);

//		if(thread==null){
//			outgoingMessageWriter.println("강퇴당하셨습니다");
//		}
		if(baseServer.clientThreads.contains(thread)) {
			outgoingMessageWriter.println(input);
		}
		else{
			outgoingMessageWriter.println("강퇴당하셨습니다");
		}
	}

	public String getClientNameFromNetwork() throws IOException {
		/*
		 * Get the name of the client, which is the first data transaction the
		 * server-client make
		 */
		return incomingMessageReader.readLine();
	}

	public String getClientName() {
		return this.clientName;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
}
