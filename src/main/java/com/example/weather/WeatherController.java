package com.example.weather;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api") @Validated
public class WeatherController {
  private final OpenMeteoService svc;
  public WeatherController(OpenMeteoService s){ svc=s; }
  @GetMapping("/search") public JsonNode search(@RequestParam @NotBlank @Size(min=2,max=100) String q){ return svc.search(q.trim()); }
  @GetMapping("/forecast") public JsonNode forecast(@RequestParam @DecimalMin("-90") @DecimalMax("90") double lat,
      @RequestParam @DecimalMin("-180") @DecimalMax("180") double lon){ return svc.forecast(lat,lon); }
  @GetMapping("/air-quality") public JsonNode air(@RequestParam @DecimalMin("-90") @DecimalMax("90") double lat,
      @RequestParam @DecimalMin("-180") @DecimalMax("180") double lon){ return svc.airQuality(lat,lon); }
}
