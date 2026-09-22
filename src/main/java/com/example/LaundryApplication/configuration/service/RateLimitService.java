package com.example.LaundryApplication.configuration.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {

        Bucket bucket = buckets.computeIfAbsent(key, k -> {

            Refill refill = Refill.intervally(
                    3,
                    Duration.ofMinutes(15)
            );

            Bandwidth limit = Bandwidth.classic(3, refill);

            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });

        return bucket.tryConsume(1);
    }
}