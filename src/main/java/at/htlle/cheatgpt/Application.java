package at.htlle.cheatgpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"at.htlle.cheatgpt"})
public class Application {

    public static void main(String[] args) {
    	SpringApplication.run(Application.class);
    }
}
