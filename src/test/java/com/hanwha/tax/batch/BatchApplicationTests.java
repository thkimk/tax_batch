package com.hanwha.tax.batch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class BatchApplicationTests {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Test
	void contextLoads() {
		System.out.println(LocalDate.now().format(DateTimeFormatter.ofPattern("YYMM")));
		System.out.println(String.format("%05d", 11));

		String cToken = jwtTokenProvider.createTokenCoocon();
		System.out.println("## Coocon Token : "+ cToken);	// Coocon
															// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJDb29jb24iLCJpYXQiOjE2NTY5MTk1NTUsImV4cCI6MTk3MjI3OTU1NX0.rMvLk9iC-ICmQ_zPdcLTdgaInlCe0zvViBhehOyeFHE
	}

}
