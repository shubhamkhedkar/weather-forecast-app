package com.example.weather;
import org.junit.jupiter.api.Test;
import java.time.*; import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.*;
class TtlCacheTest {
  static class MutableClock extends Clock { Instant now=Instant.EPOCH;
    public ZoneId getZone(){return ZoneOffset.UTC;} public Clock withZone(ZoneId z){return this;} public Instant instant(){return now;} }
  @Test void expires(){
    MutableClock c=new MutableClock(); TtlCache<Integer> cache=new TtlCache<>(Duration.ofMinutes(10),c); AtomicInteger n=new AtomicInteger();
    assertThat(cache.get("k",n::incrementAndGet)).isEqualTo(1);
    assertThat(cache.get("k",n::incrementAndGet)).isEqualTo(1);
    c.now=c.now.plus(Duration.ofMinutes(11));
    assertThat(cache.get("k",n::incrementAndGet)).isEqualTo(2);
  }
}
