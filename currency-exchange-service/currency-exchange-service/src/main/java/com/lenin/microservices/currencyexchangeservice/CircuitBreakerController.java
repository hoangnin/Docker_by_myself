package com.lenin.microservices.currencyexchangeservice;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@RestController
public class CircuitBreakerController {
    private Logger logger = Logger.getLogger(CircuitBreakerController.class.getName());
    @GetMapping("/sample-api")
    //@Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
    @Bulkhead(name = "default")
    //@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
    //@RateLimiter(name = "default")
    // rate limiting: 10s => 10000 calls to the sample-api (configured in application.properties)
    public String sampleApi(){
        logger.info("Sample API call received");
//        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://dummy-server", String.class);

       // return forEntity.getBody();
        return "sample-api";
    }

    public String hardcodedResponse(Exception ex){
        return "fallback-response";
    }
}
