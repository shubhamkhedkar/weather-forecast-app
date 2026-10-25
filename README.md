# Weather Forecasting App

Java 17 Spring Boot backend + plain HTML/CSS/JavaScript frontend showing live weather from **[Open-Meteo](https://open-meteo.com/)** (free, no API key, CC BY 4.0).

## Features
- City search with suggestions (Open-Meteo Geocoding API)
- "Use my location" via browser geolocation
- Current conditions: temperature, feels-like, humidity, wind speed/direction/gusts, precipitation, UV, pressure, clouds, sunrise/sunset
- 48-hour hourly chart (Chart.js) + hourly strip, 7-day daily cards
- Air quality: US & European AQI, PM2.5, PM10, O₃, NO₂, SO₂, CO
- °C/°F toggle, WMO weather-code icons & descriptions, recent searches (localStorage), responsive design

## Data sources
| Data | API |
|---|---|
| Forecast | https://api.open-meteo.com/v1/forecast |
| Geocoding | https://geocoding-api.open-meteo.com/v1/search |
| Air quality | https://air-quality-api.open-meteo.com/v1/air-quality |

Weather data by Open-Meteo.com, licensed CC BY 4.0.

## Backend API
- `GET /api/search?q=Pune` (q: 2–100 chars)
- `GET /api/forecast?lat=18.52&lon=73.86`
- `GET /api/air-quality?lat=18.52&lon=73.86`

Responses are cached in memory for 10 minutes (`cache.ttl-seconds`). Invalid input → 400, upstream failure → 502 (JSON `{status,error}`).

## Run
```bash
./mvnw test             # unit tests (JUnit 5, MockRestServiceServer, MockMvc)
./mvnw spring-boot:run  # http://localhost:8080
```
Docker:
```bash
docker build -t weather-forecast-app . && docker run -p 8080:8080 weather-forecast-app
```
