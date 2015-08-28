package com.eyeball.simpleserver.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
			System.out.print((char) 27 + "[0m");
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String request = in.readLine();
			String requestType = request.split(" ")[0];
			String requestPath = request.split(" ")[1];
			System.out.println("Got request from client: " + client.getInetAddress().getHostAddress());
			System.out.println(requestType.toUpperCase() + ": " + requestPath);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			out.write(
					"<!DOCTYPE html><html><head><title>Hello</title></head><body><h1 style=\"text-align: center;\">Hello</h1></body></html>");
			out.close();

			File views = new File(server.getParentFile(), "views/");
			String additional = "";
			String[] paths = requestPath.split("/");
			String requestedFile = "";
			if (paths.length == 0 | requestPath.trim().equals("/"))
				requestedFile = "index.html";
			else {
				requestedFile = requestPath;
			}
			if (requestedFile.endsWith(".html"))
				additional = ".view";
			File pageController = new File(views, requestedFile + additional);
			BufferedReader input = null;
			try {
				input = new BufferedReader(new FileReader(pageController));
			} catch (IOException e) {
				System.out.print((char) 27 + "[0m" + (char) 27 + "[31m");
				System.out.println(requestType.toUpperCase() + " failed --- 404 not found!");
				System.out.print((char) 27 + "[0m");
				return;
			}

			ArrayList<String> lines = new ArrayList<String>();
			for (String line = input.readLine(); line != null; line = input.readLine()) {
				lines.add(line);
			}
			input.close();
			// BufferedWriter out = new BufferedWriter(new
			// OutputStreamWriter(client.getOutputStream()));
			// out.write(
			// "<html><head><title>Hello</title></head><body><h1
			// style=\"text-align: center;\">Hello</h1></body></html>");
			// out.close();
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
