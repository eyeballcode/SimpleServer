package com.eyeball.simpleserver.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
		BufferedWriter out = null;
		try {
			System.out.print((char) 27 + "[0m");
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String request = in.readLine();
			if (request == null)
				// Client disconnected.
				return;
			String requestType = request.split(" ")[0];
			String requestPath = request.split(" ")[1];
			System.out.println("Got request from client: " + client.getInetAddress().getHostAddress());
			System.out.println(requestType.toUpperCase() + ": " + requestPath);

			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			File views = new File(server.getParentFile(), "views/");
			String additional = "";
			String[] paths = requestPath.split("/");
			String requestedFile = "";
			if (paths.length == 0 | requestPath.trim().equals("/"))
				requestedFile = "index.html";
			else {
				requestedFile = requestPath;
			}
			if (requestedFile.endsWith(".html")) {
				additional = ".view";
			}
			File pageController = new File(views, requestedFile + additional);
			File pageHTML = new File(views, requestedFile);
			BufferedReader input = null;
			BufferedReader pageHTMLReader = null;
			try {
				input = new BufferedReader(new FileReader(pageController));
				out.write("HTTP/1.0 200 OK\n");
			} catch (IOException e) {
				System.out.print((char) 27 + "[0m" + (char) 27 + "[31m");
				System.out.println(requestType.toUpperCase() + " failed --- 404 not found!");
				System.out.print((char) 27 + "[0m");
				if (!(out == null)) {
					out.write("HTTP/1.1 404 Not Found\n");
					out.write("Content-Type: text/html\n\n\n");
					try {
						BufferedReader read404 = new BufferedReader(
								new FileReader(new File(views.getParentFile(), "errors/404.html")));
						out.write("<html>\n");
						try {
							BufferedReader read404View = new BufferedReader(
									new FileReader(new File(views.getParentFile(), "errors/404.html.view")));
							out.write(Helper.getHTMLSource(read404View).getSource(2));
						} catch (Exception e1) {
						}

						for (String line = read404.readLine(); line != null; line = read404.readLine()) {
							out.write("  " + line + "\n");
						}
						out.write("</html>");
						read404.close();
						out.flush();
						out.close();
					} catch (IOException e2) {
						e.printStackTrace();
					}
				}
				return;
			}
			boolean customHTML = false;
			try {
				pageHTMLReader = new BufferedReader(new FileReader(pageHTML));
				customHTML = true;
			} catch (Exception e) {

			}

			if (requestedFile.endsWith(".html")) {
				out.write("Content-Type: text/html\n\n\n");
			} else {
				out.write("Content-Type: text/" + requestedFile.split("[\\w]*\\.([\\w]*)")[0] + "\n\n\n");
			}
			out.write("<html>");
			out.write(Helper.getHTMLSource(input).getSource(2));
			if (customHTML) {
				out.write("  <body>");
				for (String line = pageHTMLReader.readLine(); line != null; line = pageHTMLReader.readLine()) {
					out.write("  " + line + "\n");
				}
				out.write("</body>");
			}
			out.write("</html>");

			out.flush();
			out.close();
		} catch (Exception e) {
			System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
			System.out.println("Error in running server --- " + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.out.print((char) 27 + "[0m");
			System.exit(2);
		}

	};
}
