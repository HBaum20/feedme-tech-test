package io.stars.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@ComponentScan(basePackages = "io.stars.store.*")
public class DataStoreApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(DataStoreApplication.class, args);
    }
}
