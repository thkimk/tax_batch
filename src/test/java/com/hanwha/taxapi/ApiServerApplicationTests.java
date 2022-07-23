package com.hanwha.taxapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//@SpringBootTest
class ApiServerApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(LocalDate.now().format(DateTimeFormatter.ofPattern("YYMM")));
		System.out.println(String.format("%05d", 11));

	}

}
