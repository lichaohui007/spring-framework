package org.springframework.beans.mine;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

//后置处理器BeanPostProcessor
public class BeanPostProcessorTest implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("Bean " + beanName + " 开始初始化");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("Bean" + beanName + " 完成初始化");
		return bean;
	}

	public void display(){
		System.out.println("hello BeanPostProcessor");
	}

	public static void main(String[] args){
		ClassPathResource resource = new ClassPathResource("beans.xml");
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		reader.loadBeanDefinitions(resource);

		BeanPostProcessorTest test = (BeanPostProcessorTest) factory.getBean("pp");
		//factory.addBeanPostProcessor(test);

		InitializingBeanTest initializingBeanTest = (InitializingBeanTest) factory.getBean("ii");
		System.out.println(initializingBeanTest.getName());
		test.display();
	}
}
