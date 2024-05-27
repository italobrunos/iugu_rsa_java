package com.iugu.domain;

/**
 * @author italobrunos
 */
public class CustomVariable {

	private String name;
	private Object value;

	public CustomVariable() {
	}

	public CustomVariable(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public static CustomVariable create(String name, Object value) {
		return new CustomVariable(name, value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
