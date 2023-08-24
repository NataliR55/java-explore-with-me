package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.practicum.client.StatsClient;

@SpringBootApplication
public class MainServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainServerApplication.class, args);
    }

    @Bean
    public StatsClient getStatsClient() {
        return new StatsClient();
    }
}
