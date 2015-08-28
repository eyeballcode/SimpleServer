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

import com.eyeball.html4j.elements.HEAD;
import com.eyeball.html4j.elements.HTML;
import com.eyeball.html4j.elements.HTMLTEXT;

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
			BufferedReader input = null;
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
						for (String line = read404.readLine(); line != null; line = read404.readLine()) {
							out.write(line + "\n");
						}
						read404.close();
						out.flush();
						out.close();
					} catch (IOException e2) {
						e.printStackTrace();
					}
				}
				return;
			}

			if (requestedFile.endsWith(".html")) {
				out.write("Content-Type: text/html\n\n\n");
			} else {
				out.write("Content-Type: text/" + requestedFile.split("[\\w]*\\.([\\w]*)")[0] + "\n\n\n");
			}
			ArrayList<String> lines = new ArrayList<String>();
			for (String line = input.readLine(); line != null; line = input.readLine()) {
				StringBuilder lineBuilder = new StringBuilder();
				String last = "";
				for (String c : line.split("")) {
					if (last.equals(" ") && c.equals(" "))
						continue;
					lineBuilder.append(c);
				}
				lines.add(lineBuilder.toString());
			}
			input.close();
			HTML html = new HTML();
			HEAD head = new HEAD();
			for (String line : lines) {
				if (line.startsWith("reqire script")) {
					HTMLTEXT script = new HTMLTEXT();
					script.setText("<script src=\"" + line.substring("reqire script ".length()) + "\"></script>");
					head.addElement(script);
				} else if (line.startsWith("title")) {
					HTMLTEXT script = new HTMLTEXT();
					script.setText("<title>" + line.substring("title ".length()) + "</title>");
					head.addElement(script);
				}
			}
			html.addElement(head);
			out.write(html.getSource(0));
			System.out.println(pageController.getAbsolutePath());

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
