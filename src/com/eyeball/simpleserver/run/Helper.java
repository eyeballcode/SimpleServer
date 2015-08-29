package com.eyeball.simpleserver.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.eyeball.html4j.elements.HEAD;
import com.eyeball.html4j.elements.HTMLTEXT;

public class Helper {

	public static HEAD getHTMLSource(BufferedReader input) throws IOException {
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

		HEAD head = new HEAD();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("require script")) {
				HTMLTEXT script = new HTMLTEXT();
				script.setText("<script src=\"" + line.substring("reqire script ".length()) + "\"></script>");
				head.addElement(script);
			} else if (line.startsWith("require style")) {
				HTMLTEXT style = new HTMLTEXT();
				style.setText("<link rel=\"stylesheet\" href=\"" + line.substring("reqire style ".length()) + "\">");
				head.addElement(style);
			} else if (line.startsWith("favicon")) {
				HTMLTEXT fav = new HTMLTEXT();
				fav.setText("<link rel=\"icon\" href=\"" + line.substring("favicon ".length()) + "\">");
				head.addElement(fav);
			} else if (line.startsWith("title")) {
				HTMLTEXT title = new HTMLTEXT();
				title.setText("<title>" + line.substring("title ".length()) + "</title>");
				head.addElement(title);
			} else if (line.startsWith("define script")) {
				int p = i + 1;
				StringBuilder scriptContents = new StringBuilder();
				for (; p < lines.size(); p++) {
					if (!lines.get(p).equals("end")) {
						scriptContents.append(lines.get(p) + "\n\n");
					} else {
						break;
					}
				}
				i = p;
				HTMLTEXT script = new HTMLTEXT();
				script.setText("<script>\n" + scriptContents + "</script>");
				head.addElement(script);
			} else if (line.startsWith("define style")) {
				int p = i + 1;
				StringBuilder scriptContents = new StringBuilder();
				for (; p < lines.size(); p++) {
					if (!lines.get(p).equals("end")) {
						scriptContents.append(lines.get(p) + "\n\n");
					} else {
						break;
					}
				}
				i = p;
				HTMLTEXT script = new HTMLTEXT();
				script.setText("<style>\n" + scriptContents + "</style>");
				head.addElement(script);
			}
		}
		return head;
	}

}
