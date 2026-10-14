package com.example.LaundryApplication.configuration.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    private final Cache<String, Bucket> buckets =
            Caffeine.newBuilder()
                    .expireAfterAccess(Duration.ofMinutes(30))
                    .maximumSize(10_000)
                    .build();

    public boolean isAllowed(String key) {

        Bucket bucket = buckets.get(key, k -> {

            Refill refill = Refill.intervally(
                    10,
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