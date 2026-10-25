package com.example.weather;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
@SpringBootApplication
public class WeatherApplication {
  public static void main(String[] a){ SpringApplication.run(WeatherApplication.class,a); }
  @Bean RestClient.Builder restClientBuilder(){ return RestClient.builder(); }
}
