package qrcodeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения QRCode Service.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Запуск Spring Boot приложения
        SpringApplication.run(Application.class, args);
    }
}
