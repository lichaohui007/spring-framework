package org.springframework.beans.mine;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

//注册标签解析器
public class UserNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("user",new UserDefinitionParser());
	}
}
