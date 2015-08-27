package com.eyeball.simpleserver.run;

import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection extends Thread {

	Socket client;

	String connection;

	File server;

	public ClientConnection(Socket client, File server) {
		this.client = client;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String request = in.readLine();
			String requestType = request.split(" ")[0];
			String requestPath = request.split(" ")[1];
			System.out.println("Got request from client: " + client.getInetAddress().getHostAddress());
			System.out.println(requestType.toUpperCase() + ": " + requestPath);
			File views = new File(server, "views/");
			String additional = "";
			String requestedFile = requestPath.split("/")[requestPath.split("/").length - 1];
			if (!requestedFile.endsWith(".html"))
				additional = "/index.html.view";
			else
				additional = ".view";
			File pageController = new File(views, requestPath + additional);
			BufferedReader input = new BufferedReader(new FileReader(pageController));
			ArrayList<String> lines = new ArrayList<String>();
			for (String line = input.readLine(); line != null; line = input.readLine()) {
				lines.add(line);
			}
			input.close();
			System.out.println(pageController.getAbsolutePath());
		} catch (Exception e) {
			System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
			System.out.println("Error in running server --- " + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.out.print((char) 27 + "[0m");
			System.exit(2);
		}
	};
}
