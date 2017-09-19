package com.ynet.servelt.define;

public class HandlerMapping extends Handler {
	public HandlerMapping(String key, Object bean) {
		super();
		key = key.replaceAll("//", "/");
		this.setKey(key);
		this.setBean(bean);
	}

	@Override
	public String toString() {
		return "HandlerMapping [getKey()=" + getKey() + ", getBean()="
				+ getBean() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

}
