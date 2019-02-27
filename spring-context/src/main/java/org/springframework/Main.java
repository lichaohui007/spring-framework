package org.springframework;

import org.springframework.beans.mine.Student;
import org.springframework.beans.mine.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args){
		ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
		//User user = (User) context.getBean("user");
		//System.out.println(user.getUserName() + "----" + user.getEmail());

		//自定义属性解析器被spring调用  即完成对spring的扩展
		Student student = (Student) context.getBean("student");
		System.out.println(student.getStudentService().getAge());
	}
}
