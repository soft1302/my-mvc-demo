package com.ynet.servelt.define;

public abstract class Handler {
	private String key;
	private Object bean;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

}
