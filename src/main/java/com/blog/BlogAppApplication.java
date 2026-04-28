package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.blog.repository.UserRepository;
import com.blog.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BlogAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(UserRepository repo, PasswordEncoder encoder) {
		return args -> {
			if(repo.findByUsername("admin") == null) {
				User user = new User();
				user.setUsername("admin");
				user.setPassword(encoder.encode("1234"));
				repo.save(user);
			}
		};
	}
}
