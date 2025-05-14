package me.darkakyloff.subscriptionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "me.darkakyloff.subscriptionservice")
@EnableJpaRepositories("me.darkakyloff.subscriptionservice.repository")
@EntityScan("me.darkakyloff.subscriptionservice.model")
public class SubscriptionServiceApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(SubscriptionServiceApplication.class, args);
	}
}
