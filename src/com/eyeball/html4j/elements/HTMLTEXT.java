package com.eyeball.html4j.elements;

import java.util.ArrayList;

import com.eyeball.html4j.api.HTMLElement;
import com.eyeball.html4j.api.SourceHelper;

public class HTMLTEXT extends HTMLElement {

	String text;

	public HTMLTEXT(String text) {
		setText(text);
	}

	public HTMLTEXT() {
	}

	@Override
	public ArrayList<HTMLElement> getChildren() {
		return new ArrayList<HTMLElement>();
	}

	@Override
	public String getElementTagName() {
		return "";
	}

	@Override
	public String getSource(int spaces) {
		return SourceHelper.getSource(spaces, this);
	}

	@Override
	public void addElement(HTMLElement element) {
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText(int indent) {
		StringBuilder source = new StringBuilder();

		for (int i = 0; i < indent; i++)
			source.append(" ");
		return source.append(text).toString();
	}

	@Override
	public void removeElement(HTMLElement element) {
	}

}
