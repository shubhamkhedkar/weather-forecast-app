package com.example.weather;
import java.time.*; import java.util.concurrent.ConcurrentHashMap; import java.util.function.Supplier;
/** Simple in-memory TTL cache. */
public class TtlCache<V> {
  private record Entry<V>(V value, Instant expires) {}
  private final ConcurrentHashMap<String, Entry<V>> map = new ConcurrentHashMap<>();
  private final Duration ttl; private final Clock clock;
  public TtlCache(Duration ttl, Clock clock){ this.ttl=ttl; this.clock=clock; }
  public V get(String key, Supplier<V> loader){
    Instant now = clock.instant(); Entry<V> e = map.get(key);
    if (e != null && now.isBefore(e.expires())) return e.value();
    V v = loader.get(); map.put(key, new Entry<>(v, now.plus(ttl)));
    if (map.size() > 1000) map.entrySet().removeIf(x -> !now.isBefore(x.getValue().expires()));
    return v;
  }
  public int size(){ return map.size(); }
}
