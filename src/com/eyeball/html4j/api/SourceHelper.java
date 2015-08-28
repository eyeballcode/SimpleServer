package com.eyeball.html4j.api;

import java.lang.reflect.Field;

import com.eyeball.html4j.elements.HTMLTEXT;

public class SourceHelper {

	public static String getSource(int spaces, HTMLElement element) {
		StringBuilder source = new StringBuilder();

		for (int i = 0; i < spaces; i++)
			source.append(" ");

		if (element instanceof HTMLTEXT) {
			source.append(((HTMLTEXT) element).getText(0));
			return source.toString();
		}

		source.append("<" + element.getElementTagName());

		for (Field f : element.getClass().getFields()) {
			if (f.getAnnotation(Attribute.class) != null)
				try {
					f.setAccessible(true);
					if (f.get(element) != null
							&& !f.get(element).toString().isEmpty()) {
						if (f.getName().equals("clazz"))
							source.append(" class=\"" + f.get(element) + "\"");
						else
							source.append(" " + f.getName() + "=\""
									+ f.get(element) + "\"");
					}
				} catch (Exception e) {
					// Ignore
				}
		}

		source.append(">\n");
		for (HTMLElement element2 : element.getChildren()) {
			source.append(element2.getSource(spaces + 2) + "\n");
		}
		for (int i = 0; i < spaces; i++)
			source.append(" ");
		source.append("</" + element.getElementTagName() + ">");
		return source.toString();
	}
}
