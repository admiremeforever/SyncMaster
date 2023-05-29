package com.infybuzz.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


import com.infybuzz.source.entity.Student;

@Component
public class FirstItemProcessor implements ItemProcessor<Student, com.infybuzz.destination.etity.Student> {

	@Override
	public com.infybuzz.destination.etity.Student process(Student item) throws Exception {
		System.out.println(item.getId());
		com.infybuzz.destination.etity.Student student = new com.infybuzz.destination.etity.Student();
		student.setId(item.getId());
		student.setFirstName(item.getFirstName());
		student.setLastName(item.getLastName());
		student.setDeptId(item.getDeptId());
		student.setEmail(item.getEmail());
		student.setActive(item.getIsActive() != null ? Boolean.valueOf(item.getIsActive()) : false);
		return student;
	}

}
