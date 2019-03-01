package org.springframework.beans.mine;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class UserDefinitionParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return User.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String id = element.getAttribute("id");
		String userName = element.getAttribute("userName");
		String email = element.getAttribute("email");

		if(StringUtils.hasText(id)){
			builder.addPropertyValue("id",id);
		}

		if(StringUtils.hasText(userName)){
			builder.addPropertyValue("userName",userName);
		}
		//builder中包含beanDefinition
		if(StringUtils.hasText(email)){
			builder.addPropertyValue("email",email);
		}


	}
}
