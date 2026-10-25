package com.example.weather;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(WeatherController.class)
class WeatherControllerTest {
  @Autowired MockMvc mvc; @MockBean OpenMeteoService svc; ObjectMapper om=new ObjectMapper();
  @Test void search() throws Exception {
    when(svc.search("Pune")).thenReturn(om.readTree("{\"results\":[{\"name\":\"Pune\"}]}"));
    mvc.perform(get("/api/search").param("q","Pune")).andExpect(status().isOk()).andExpect(jsonPath("$.results[0].name").value("Pune"));
  }
  @Test void searchTooShort() throws Exception { mvc.perform(get("/api/search").param("q","a")).andExpect(status().isBadRequest()); }
  @Test void forecastInvalidLat() throws Exception { mvc.perform(get("/api/forecast").param("lat","100").param("lon","0")).andExpect(status().isBadRequest()); }
  @Test void forecastMissing() throws Exception { mvc.perform(get("/api/forecast").param("lat","10")).andExpect(status().isBadRequest()); }
  @Test void forecastNotNumber() throws Exception { mvc.perform(get("/api/forecast").param("lat","x").param("lon","1")).andExpect(status().isBadRequest()); }
  @Test void upstreamFailure() throws Exception {
    when(svc.airQuality(anyDouble(),anyDouble())).thenThrow(new UpstreamException("x",null));
    mvc.perform(get("/api/air-quality").param("lat","18.5").param("lon","73.8")).andExpect(status().isBadGateway()).andExpect(jsonPath("$.error").exists());
  }
}
