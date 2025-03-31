package dev.pavanbhat.jenkinstest;

import dev.pavanbhat.jenkinstest.service.CalculationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JenkinstestApplicationTests {

	@Test
	void contextLoads() {
	}

	private final CalculationService calculationService = new CalculationService();

	@Test
	void testAddition() {
		assertEquals(5, calculationService.add(2, 3));
		assertEquals(0, calculationService.add(-2, 2));
	}

	@Test
	void testMultiplication() {
		assertEquals(6, calculationService.multiply(2, 3));
		assertEquals(0, calculationService.multiply(5, 0));
	}

	@Test
	void testIsEven() {
		assertTrue(calculationService.isEven(4));
		assertFalse(calculationService.isEven(5));
	}

}
