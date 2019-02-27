package org.springframework.beans.mine;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

//属性解析类
public class StudentConversionService implements Converter<String, StudentService> {
	@Override
	public StudentService convert(String source) {
		if(StringUtils.hasLength(source)){
			String[] sources = source.split("#");

			StudentService studentService = new StudentService();
			studentService.setAge(Integer.parseInt(sources[0]));
			studentService.setName(sources[1]);

			return studentService;
		}

		return null;
	}
}
