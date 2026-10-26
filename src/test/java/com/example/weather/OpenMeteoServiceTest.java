package com.example.weather;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class OpenMeteoServiceTest {
  MockRestServiceServer server; OpenMeteoService svc;
  @BeforeEach void setup(){
    RestClient.Builder b = RestClient.builder();
    server = MockRestServiceServer.bindTo(b).build();
    svc = new OpenMeteoService(b,"http://fc/v1/forecast","http://geo/v1/search","http://aq/v1/air-quality",600);
  }
  @Test void searchProxiesAndCaches(){
    server.expect(org.springframework.test.web.client.ExpectedCount.once(), requestTo(org.hamcrest.Matchers.startsWith("http://geo/v1/search?name=Pune")))
      .andRespond(withSuccess("{\"results\":[{\"name\":\"Pune\",\"latitude\":18.52,\"longitude\":73.86}]}", MediaType.APPLICATION_JSON));
    assertThat(svc.search("Pune").get("results").get(0).get("name").asText()).isEqualTo("Pune");
    svc.search("pune"); // cached (case-insensitive)
    server.verify();
  }
  @Test void forecastBuildsUrl(){
    server.expect(requestTo(org.hamcrest.Matchers.allOf(org.hamcrest.Matchers.containsString("latitude=18.520"),
        org.hamcrest.Matchers.containsString("forecast_days=7"), org.hamcrest.Matchers.containsString("forecast_hours=48"))))
      .andRespond(withSuccess("{\"current\":{\"temperature_2m\":25.1}}", MediaType.APPLICATION_JSON));
    assertThat(svc.forecast(18.52,73.86).at("/current/temperature_2m").asDouble()).isEqualTo(25.1);
  }
  @Test void upstreamErrorWrapped(){
    server.expect(requestTo(org.hamcrest.Matchers.startsWith("http://aq"))).andRespond(withServerError());
    assertThatThrownBy(() -> svc.airQuality(1,2)).isInstanceOf(UpstreamException.class);
  }
}
