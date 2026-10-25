package com.example.weather;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.*; import java.util.Locale;

@Service
public class OpenMeteoService {
  private final RestClient http; private final TtlCache<JsonNode> cache;
  private final String forecastUrl, geoUrl, aqUrl;
  public OpenMeteoService(RestClient.Builder b,
      @Value("${openmeteo.forecast-url}") String f, @Value("${openmeteo.geocoding-url}") String g,
      @Value("${openmeteo.air-quality-url}") String a, @Value("${cache.ttl-seconds:600}") long ttl){
    this.http=b.build(); this.forecastUrl=f; this.geoUrl=g; this.aqUrl=a;
    this.cache=new TtlCache<>(Duration.ofSeconds(ttl), Clock.systemUTC());
  }
  private static String r(double v){ return String.format(Locale.ROOT,"%.3f",v); }
  public JsonNode search(String q){
    String uri = UriComponentsBuilder.fromHttpUrl(geoUrl).queryParam("name",q).queryParam("count",8)
        .queryParam("language","en").queryParam("format","json").encode().toUriString();
    return fetch("search:"+q.toLowerCase(Locale.ROOT), uri);
  }
  public JsonNode forecast(double lat,double lon){
    String uri = forecastUrl+"?latitude="+r(lat)+"&longitude="+r(lon)
      +"&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,pressure_msl,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index"
      +"&hourly=temperature_2m,apparent_temperature,relative_humidity_2m,precipitation_probability,precipitation,weather_code,wind_speed_10m,wind_direction_10m,uv_index,is_day"
      +"&daily=weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,uv_index_max,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,wind_direction_10m_dominant"
      +"&timezone=auto&forecast_days=7&forecast_hours=48";
    return fetch("fc:"+r(lat)+","+r(lon), uri);
  }
  public JsonNode airQuality(double lat,double lon){
    String uri = aqUrl+"?latitude="+r(lat)+"&longitude="+r(lon)
      +"&current=us_aqi,european_aqi,pm2_5,pm10,carbon_monoxide,nitrogen_dioxide,ozone,sulphur_dioxide&timezone=auto";
    return fetch("aq:"+r(lat)+","+r(lon), uri);
  }
  private JsonNode fetch(String key,String uri){
    return cache.get(key, () -> {
      try { JsonNode n = http.get().uri(java.net.URI.create(uri)).retrieve().body(JsonNode.class);
        if (n == null) throw new UpstreamException("Empty response from Open-Meteo", null);
        return n;
      } catch (RestClientException e){ throw new UpstreamException("Open-Meteo request failed: "+e.getMessage(), e); }
    });
  }
}
