package io.stars.transform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "io.stars.transform.*")
public class JsonTransformApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(JsonTransformApplication.class, args);
    }
}
