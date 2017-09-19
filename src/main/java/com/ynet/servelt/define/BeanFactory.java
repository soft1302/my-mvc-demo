package com.ynet.servelt.define;

public class BeanFactory extends Handler {

	public BeanFactory(String key, Object bean) {
		super();
		this.setKey(key);
		this.setBean(bean);
	}

	@Override
	public String toString() {
		return "BeanFactory [getKey()=" + getKey() + ", getBean()=" + getBean()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
