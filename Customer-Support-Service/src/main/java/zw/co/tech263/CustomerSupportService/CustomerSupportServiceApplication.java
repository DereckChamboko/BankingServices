package zw.co.tech263.CustomerSupportService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoTransactionManager;

@SpringBootApplication
public class CustomerSupportServiceApplication {




	public static void main(String[] args) {
		SpringApplication.run(CustomerSupportServiceApplication.class, args);
	}

}
