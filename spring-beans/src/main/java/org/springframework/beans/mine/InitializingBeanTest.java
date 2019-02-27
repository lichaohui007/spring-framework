package org.springframework.beans.mine;

import org.springframework.beans.factory.InitializingBean;

public class InitializingBeanTest implements InitializingBean {

	private String name;

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("InitializingBeanTest initializing....");
		this.name = "2222";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
