package de.aclue.demojdbc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoJdbcApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DemoJdbcApplication.class);

	public static void main(String args[]) {
		SpringApplication.run(DemoJdbcApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... strings) throws Exception {

		log.info("Creating tables");

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" "))
				.collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

		// Uses JdbcTemplate's batchUpdate operation to bulk load data
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		log.info("Querying for customer records where first_name = 'Josh':");
		List<Customer> customers = jdbcTemplate.query(
				"SELECT id, first_name, last_name FROM customers WHERE first_name = ?", 
				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")),
				"Josh");
		
		customers.forEach(customer -> log.info(customer.toString()));
	}

	public static class Customer {
		private long id;
		private String firstName, lastName;

		public Customer(long id, String firstName, String lastName) {
			this.id = id;
			this.firstName = firstName;
			this.lastName = lastName;
		}

		@Override
		public String toString() {
			return String.format(
					"Customer[id=%d, firstName='%s', lastName='%s']",
					id, firstName, lastName);
		}

		// getters & setters omitted for brevity
	}

}
