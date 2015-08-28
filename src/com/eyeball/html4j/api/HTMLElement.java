package com.eyeball.html4j.api;

import java.util.ArrayList;

/**
 * 
 * Declares a class as an HTML element.
 * 
 */
public abstract class HTMLElement {

	/**
	 * Gets the children
	 * 
	 * <div> <a href="#"> Hi </a> <button value="Click"></button> </div>
	 * 
	 * Where <code>a</code> and <code>b</code> are children
	 * 
	 * @return The children
	 */
	public abstract ArrayList<HTMLElement> getChildren();

	public abstract String getElementTagName();

	public abstract String getSource(int spaces);

	public abstract void addElement(HTMLElement element);

	public abstract void removeElement(HTMLElement element);

	/**
	 * HTML class name. <code>
	 * <button class="A"></button>
	 * </code>
	 * 
	 * Sorry class not accepted, use clazz
	 */
	@Attribute
	public String clazz = "";

	/**
	 * This element's id
	 */
	@Attribute
	public String id = "";

	@Attribute
	public String title = "";

}
