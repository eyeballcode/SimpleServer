package com.eyeball.html4j.api;

import com.eyeball.html4j.elements.HEAD;
import com.eyeball.html4j.elements.HTMLTEXT;
import com.eyeball.html4j.elements.LINK;

public class Helper {

	public static HEAD setFavicon(HTMLElement html, String iconLocation) {
		HEAD head = new HEAD();
		for (HTMLElement element : html.getChildren()) {
			if (element instanceof HEAD) {
				head = (HEAD) element;
				break;
			}
		}
		head.profile = "http://www.w3.org/2005/10/profile";
		LINK favicon = new LINK();
		favicon.rel = "icon";
		String uri = iconLocation;
		String extension = uri.substring(uri.lastIndexOf(".") + 1);
		favicon.type = "image/" + extension;
		favicon.href = iconLocation;
		head.addElement(favicon);
		return head;
	}

	public static HEAD setTitle(HTMLElement html, String title) {
		HEAD head = new HEAD();
		boolean hasFound = false;
		for (HTMLElement element : html.getChildren()) {
			if (element instanceof HEAD) {
				hasFound = true;
				head = (HEAD) element;
				break;
			}
		}
		if (!hasFound) {
			html.addElement(head);
		}
		for (HTMLElement element : head.getChildren()) {
			if (element.getSource(0).contains("<title>")) {
				head.removeElement(element);
			}
		}
		head.addElement(new HTMLTEXT("<title>" + title + "</title>"));
		return head;
	}

}
