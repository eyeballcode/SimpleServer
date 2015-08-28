package com.eyeball.simpleserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

import com.eyeball.html4j.elements.BODY;
import com.eyeball.html4j.elements.HEAD;
import com.eyeball.html4j.elements.HTML;
import com.eyeball.html4j.elements.HTMLTEXT;
import com.eyeball.html4j.elements.P;
import com.eyeball.simpleserver.run.ServerListener;
import com.eyeball.utils.optionreading.ChangeableOptionsReader;
import com.eyeball.utils.optionreading.OptionsReader;

public class UbuntuMain {

	private static void helpCommand(String name, String[] args, String[] argsdes, String des) {
		System.out.println("------------------------------------");
		System.out.print((char) 27 + "[1m" + (char) 27 + "[33m");
		System.out.print("  " + name);
		for (String arg : args) {
			System.out.print(" " + arg);
		}
		System.out.println();
		System.out.print((char) 27 + "[0m");
		System.out.println(des);
		System.out.println("Arguments: ");
		if (args.length == 0)
			System.out.println("  None");
		else {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				System.out.print((char) 27 + "[33m" + (char) 27 + "[1m");
				System.out.println("  " + arg + (char) 27 + "[0m" + " -- " + argsdes[i]);
			}
		}
		System.out.print((char) 27 + "[0m");
	}

	private static void printMode(String name, String path) {
		System.out.println(
				(char) 27 + "[32m" + (char) 27 + "[1m" + name + " " + (char) 27 + "[0m" + (char) 27 + "[1m" + path);
	}

	public static void printHelp() {
		helpCommand("new", new String[] { "path" }, new String[] { "The path to create the server" },
				"Use the \"new\" command to create a new server.");
		helpCommand("run", new String[] {}, new String[] {}, "Use the \"run\" command to start the server");
		helpCommand("settings", new String[] {}, new String[] {},
				"Use the \"settings\" command to edit the server settings.");
	}

	public static void run(String args[]) throws InterruptedException {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread("Shutdown-Thread") {
				@Override
				public void run() {
					System.out.println((char) 27 + "[0m");
				}
			});
			if (args[0].equals("new")) {
				try {
					File path = new File(new File(args[1]).getCanonicalPath());
					System.out.println("Create server at " + path.getAbsolutePath());
					if (new File(path, ".server/").exists()) {
						System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
						System.out.println("Could not create new server --- server already exists at path");
						System.out.print((char) 27 + "[0m");
						System.exit(1);
					}
					generateServer(path);
				} catch (IOException e) {
					System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
					System.err.println("Could not create new server --- Invalid path!");
					e.printStackTrace();
					System.out.print((char) 27 + "[0m");
					System.exit(1);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
					System.err.println("Could not create new server --- No path specified!");
					System.out.print((char) 27 + "[0m");
					System.exit(1);
				}
			} else if (args[0].equals("run")) {
				run();
			} else if (args[0].equals("settings")) {
				editSettings();
			} else if (args[0].equals("info")) {
				File serverFiles = new File(System.getProperty("user.dir") + "/.server/");
				printMode("Info", serverFiles.getParentFile().getAbsolutePath());
			} else {
				System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
				System.err.println("Unkown command: " + args[0]);
				System.out.print((char) 27 + "[0m");
				System.out.println("Here is a list of commands:");
				printHelp();
				System.exit(1);
			}
			System.out.print((char) 27 + "[0m");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.print((char) 27 + "[1m");
			System.out.println("SimpleServer Help");
			System.out.println("Here are the options:");
			printHelp();
		}
	}

	private static void editSettings() {
		File serverFiles = new File(System.getProperty("user.dir") + "/.server/");
		printMode("Edit", serverFiles.getParentFile().getAbsolutePath());
		if (!serverFiles.exists()) {
			System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
			System.err.println("Could not edit server --- server not found!");
			System.out.print((char) 27 + "[0m");
			System.exit(1);
		}
		try {
			File serverDetails = new File(serverFiles, "settings.ini");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			ChangeableOptionsReader serverDetailsOR = new ChangeableOptionsReader(serverDetails);
			String name = "";
			while (name.equals("")) {
				System.out.print((char) 27 + "[36m" + (char) 27 + "[1m");
				System.out.print("Please enter the server name: " + (char) 27 + "[0m");
				name = in.readLine();
			}
			System.out.print((char) 27 + "[0m");
			serverDetailsOR.setValue("name", name);
			in.close();
		} catch (IOException e) {
			System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
			System.out.println("Could not edit server --- java.io.IOException: " + e.getMessage());
			e.printStackTrace();
			System.out.print((char) 27 + "[0m");
			System.exit(2);
		}

	}

	private static void run() {
		File serverFiles = new File(System.getProperty("user.dir") + "/.server/");
		printMode("Run", serverFiles.getParentFile().getAbsolutePath());
		if (!serverFiles.exists()) {
			System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
			System.err.println("Could not run server --- server not found!");
			System.out.print((char) 27 + "[0m");
			System.exit(1);
		}
		ServerSocket serverSocket = null;
		try {
			File serverDetails = new File(serverFiles, "settings.ini");
			ChangeableOptionsReader serverDetailsOR = new ChangeableOptionsReader(serverDetails);
			String name = serverDetailsOR.readString("name", "");
			if (name.equals("")) {
				serverDetailsOR.setValue("name", System.getProperty("user.name") + "'s server");
				name = serverDetailsOR.readString("name", "");
			}
			new ServerListener(serverDetails.getParentFile(), name).start();
		} catch (IOException e) {
			System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
			System.out.println("Could not run server --- java.io.IOException: " + e.getMessage());
			e.printStackTrace();
			System.out.print((char) 27 + "[0m");
			System.exit(2);
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
				System.exit(0);
			} catch (IOException e) {
			}
		}
	}

	private static void createFilePrint(String filename) {
		System.out.print((char) 27 + "[32m" + (char) 27 + "[1m");
		System.out.print("  Create");
		System.out.print((char) 27 + "[0m" + (char) 27 + "[1m");
		System.out.println(" " + filename);
	}

	static boolean isDone = false;

	private static void generateServer(File path) {
		try {
			path.mkdir();
			File serverFiles = new File(path, ".server/");
			serverFiles.mkdir();
			createFilePrint(".server");
			File viewsFolder = new File(path, "views/");
			File serverDetails = new File(serverFiles, "settings.ini");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			OptionsReader serverDetailsOR = new OptionsReader(serverDetails);
			createFilePrint(".server/settings.ini");
			System.out.print((char) 27 + "[36m" + (char) 27 + "[1m");
			String name = "";
			while (name.trim().equals("")) {
				System.out.print("Please enter the server name: ");
				name = in.readLine();
			}
			serverDetailsOR.readString("name", name);
			System.out.print((char) 27 + "[32m" + (char) 27 + "[1m");
			System.out.println("  Server name: " + (char) 27 + "[0m" + (char) 27 + "[1m" + name);
			createFilePrint("views/");
			System.out.print((char) 27 + "[32m" + (char) 27 + "[1m");
			System.out.print("  Generating error pages");
			System.out.print((char) 27 + "[0m");
			viewsFolder.mkdir();
			createErrorPages(serverFiles);
			new Thread() {
				int dots = 0;

				@SuppressWarnings("deprecation")
				public void run() {
					System.out.print("   ");
					int finalDots = 4;
					while (!isDone) {
						if (!(dots == finalDots)) {
							for (int i = 0; i < finalDots; i++) {
								System.out.print("\b");
							}
							for (int i = 0; i < dots; i++) {
								System.out.print(".");
							}
							for (int i = 0; i < (finalDots - dots); i++) {
								System.out.print(" ");
							}
							dots++;
						} else {
							System.out.print("\b\b\b.  ");
							dots = 1;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
					this.stop();
				};
			}.start();
		} catch (IOException e) {
			System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
			System.out.println("Could not create server --- java.io.IOException: " + e.getMessage());
			e.printStackTrace();
			System.out.print((char) 27 + "[0m");
			System.exit(2);
		}
	}

	private static void createErrorPages(File serverFiles) {
		serverFiles = new File(serverFiles.getParentFile(), "errors/");
		serverFiles.mkdir();
		try {
			BufferedWriter write404 = new BufferedWriter(new FileWriter(new File(serverFiles, "404.html")));
			BufferedWriter write500 = new BufferedWriter(new FileWriter(new File(serverFiles, "500.html")));
			BufferedWriter write403 = new BufferedWriter(new FileWriter(new File(serverFiles, "403.html")));
			BufferedWriter write401 = new BufferedWriter(new FileWriter(new File(serverFiles, "401.html")));
			write404.write(getErrorPageHTML(404, "Page Not Found!").getSource(0));
			write403.write(getErrorPageHTML(403, "Unauthorized!").getSource(0));
			write401.write(getErrorPageHTML(401, "Unauthorized!").getSource(0));
			write500.write(getErrorPageHTML(500, "Internal Server Error").getSource(0));
			write404.close();
			write401.close();
			write403.close();
			write500.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isDone = true;
	}

	private static HTML getErrorPageHTML(int error, String errorText) {
		HTML html = new HTML();
		HEAD head = new HEAD();
		BODY body = new BODY();
		HTMLTEXT title = new HTMLTEXT("<h1>ERROR: " + error + " --- " + errorText + "</h1>");
		P text = new P();
		text.addElement(new HTMLTEXT("<i><sub>Powered by SimpleServer</sub></i>"));
		body.addElement(title);
		body.addElement(new HTMLTEXT("<hr>"));
		body.addElement(text);
		html.addElement(head);
		html.addElement(body);
		return html;
	}

}
