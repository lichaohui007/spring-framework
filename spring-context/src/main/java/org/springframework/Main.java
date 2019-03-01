package org.springframework;

import org.mine.UserService;
import org.springframework.beans.mine.Student;
import org.springframework.beans.mine.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args){
		//自定义标签解析器
		//ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		//User user = (User) context.getBean("user");
		//System.out.println(user.getUserName() + "----" + user.getEmail());

		//自定义属性解析器被spring调用  即完成对spring的扩展
		//ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
		//Student student = (Student) context.getBean("student");
		//System.out.println(student.getStudentService().getAge());

		//接口配置aop
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans3.xml");
		UserService userService = (UserService) context.getBean("userServiceProxy");
		userService.createUser("tom","li",18);
		userService.queryUser();
	}
}
