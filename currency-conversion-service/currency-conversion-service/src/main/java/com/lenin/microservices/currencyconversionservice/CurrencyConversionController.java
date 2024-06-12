package com.lenin.microservices.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@Configuration(proxyBeanMethods = false)
class RestTemplateConfigurations {
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@RestController
public class CurrencyConversionController {

    private Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);
    private CurrencyExchangeProxy currencyExchangeProxy;
    private RestTemplate restTemplate;

    private CurrencyConversionController(CurrencyExchangeProxy currencyExchangeProxy, RestTemplate restTemplate) {
        this.currencyExchangeProxy = currencyExchangeProxy;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion caculateCurrencyConversion(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ){
        // change-kubernetes
        logger.info("calculateCurrencyConversion called with {} to {} with quantity {}", from, to, quantity);

        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversion> responseEntity = restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class, uriVariables);
        CurrencyConversion response = responseEntity.getBody();
        return new CurrencyConversion(response.getId(),
                from,
                to,
                quantity,
                response.getConversionMultiple(),
                quantity.multiply(response.getConversionMultiple()),
                response.getEnvironment()+ " " + "rest template");
    }


    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion caculateCurrencyConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ){
        // change-kubernetes
        logger.info("calculateCurrencyConversion called with {} to {} with quantity {}", from, to, quantity);

        CurrencyConversion currencyConversion = currencyExchangeProxy.retrieveExchangeValue(from, to);
        return new CurrencyConversion(currencyConversion.getId(),
                from,to, quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " " + "feign");
    }

}
