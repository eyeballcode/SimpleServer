package com.eyeball.simpleserver.run;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener {

	String name;
	File server;

	ServerSocket serverSocket;

	public ServerListener(File path, String name) throws IOException {
		// Assumes that server exists.
		this.name = name;
		server = path;
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(3000);
			while (true) {
				Socket client = serverSocket.accept();
				ClientConnection clientConnection = new ClientConnection(client, server);
				clientConnection.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
