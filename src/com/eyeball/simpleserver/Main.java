package com.eyeball.simpleserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.eyeball.utils.optionreading.ChangeableOptionsReader;
import com.eyeball.utils.optionreading.OptionsReader;

public class Main {

	public static void printHelp() {
		System.out.print((char) 27 + "[1m");
		System.out.println("New project:");
		System.out.print((char) 27 + "[33m");
		System.out.println("  new path");
		System.out.print((char) 27 + "[0m");
		System.out.println("Use the \"new\" command to create a new server.");
		System.out.println("Arguments:");
		System.out.println("  path -- The path to create the project");
		System.out.println("------------------------------------");
		System.out.print((char) 27 + "[33m" + (char) 27 + "[1m");
		System.out.println("  run");
		System.out.print((char) 27 + "[0m");
		System.out.println("Use the \"run\" command to start the server");
		System.out.println("Arguments:");
		System.out.println("  None");
		System.out.println("------------------------------------");
		System.out.print((char) 27 + "[33m" + (char) 27 + "[1m");
		System.out.println("  settings");
		System.out.print((char) 27 + "[0m");
		System.out.println("Use the \"settings\" command to edit the server settings.");
		System.out.println("Arguments:");
		System.out.println("  None");
		System.out.print((char) 27 + "[0m");
	}

	public static void main(String args[]) {
		try {
			if (args[0].equals("new")) {
				try {
					File path = new File(new File(args[1]).getCanonicalPath());
					System.out.println("Create project at " + path.getAbsolutePath());
					if (new File(path, ".server/").exists()) {
						System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
						System.out.println("Could not create new Project --- Project already exists at path");
						System.out.print((char) 27 + "[0m");
						System.exit(1);
					}
					try {
						path.mkdir();
						File serverFiles = new File(path, ".server/");
						serverFiles.mkdir();
						System.out.print((char) 27 + "[32m" + (char) 27 + "[1m");
						System.out.print("  Create");
						System.out.print((char) 27 + "[0m" + (char) 27 + "[1m");
						System.out.println(" .server");
						File serverDetails = new File(serverFiles, "settings.ini");
						BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
						OptionsReader serverDetailsOR = new OptionsReader(serverDetails);
						System.out.print((char) 27 + "[36m" + (char) 27 + "[1m");
						String name = "";
						while (name.trim().equals("")) {
							System.out.print("Please enter the server name: ");
							name = in.readLine();
						}
						serverDetailsOR.readString("name", name);
						System.out.print((char) 27 + "[32m" + (char) 27 + "[1m");
						System.out.println("Project name: " + (char) 27 + "[0m" + (char) 27 + "[1m" + name);
						System.out.print((char) 27 + "[0m");
					} catch (IOException e) {
						System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
						System.out.println("Could not create Project --- java.io.IOException: " + e.getMessage());
						e.printStackTrace();
						System.out.print((char) 27 + "[0m");
						System.exit(2);
					}
					System.out.print((char) 27 + "[0m");
				} catch (IOException e) {
					System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
					System.err.println("Could not create new Project --- Invalid path!");
					e.printStackTrace();
					System.out.print((char) 27 + "[0m");
					System.exit(1);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
					System.err.println("Could not create new Project --- No path specified!");
					System.out.print((char) 27 + "[0m");
					System.exit(1);
				}
			} else if (args[0].equals("run")) {
				File serverFiles = new File(System.getProperty("user.dir") + "/.server/");
				System.out.println("Run project at " + serverFiles.getAbsolutePath());
				if (!serverFiles.exists()) {
					System.out.print((char) 27 + "[31m" + (char) 27 + "[1m");
					System.err.println("Could not run Project --- Project not found!");
					System.out.print((char) 27 + "[0m");
					System.exit(1);
				}
				try {
					File serverDetails = new File(serverFiles, "settings.ini");
					ChangeableOptionsReader serverDetailsOR = new ChangeableOptionsReader(serverDetails);
					String name = serverDetailsOR.readString("name", "");
					if (name.equals("")) {
						serverDetailsOR.setValue("name", System.getProperty("user.name") + "'s server");
						name = serverDetailsOR.readString("name", "");
					}
					System.out.print((char) 27 + "[32m" + (char) 27 + "[1m");
					System.out.println("Run " + (char) 27 + "[0m" + (char) 27 + "[1m" + name);
				} catch (IOException e) {
				}
			} else if (args[0].equals("settings")) {

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
			System.out.println("------------------------------------");
			printHelp();
		}
	}

}
