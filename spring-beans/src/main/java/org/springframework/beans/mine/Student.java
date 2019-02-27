package org.springframework.beans.mine;

//作为一个拥有StudentService属性的bean
public class Student {
	private StudentService studentService;

	public StudentService getStudentService() {
		return studentService;
	}

	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}
}
