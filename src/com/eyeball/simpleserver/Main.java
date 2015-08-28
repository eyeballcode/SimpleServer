package com.eyeball.simpleserver;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("windows") != -1) {
			WindowsMain.run(args);
		} else {
			UbuntuMain.run(args);
		}
	}
}
