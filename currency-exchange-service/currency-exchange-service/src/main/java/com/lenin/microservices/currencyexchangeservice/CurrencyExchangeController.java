package com.lenin.microservices.currencyexchangeservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class    CurrencyExchangeController {

    private Logger logger = LoggerFactory.getLogger(CurrencyExchangeController.class.getName());

    private Environment environment;

    private CurrencyExchangeRepository repository;

    public CurrencyExchangeController(Environment environment, CurrencyExchangeRepository repository) {
        this.environment = environment;
        this.repository = repository;
    }

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange retrieveExchangeValue(
            @PathVariable String from,
            @PathVariable String to){
        logger.info("retrieveExchangeValue called with {} to {}", from, to);
//       CurrencyExchange currencyExchange =
//               new CurrencyExchange(1000L, from, to, BigDecimal.valueOf(50));
        CurrencyExchange currencyExchange =
                repository.findByFromAndTo(from, to);
        if(currencyExchange == null){
            throw new RuntimeException("Unable to find data for "+from+" to "+to);
        }
       String port = environment.getProperty("local.server.port");
        // change-kubernetes
        String host = environment.getProperty("HOSTNAME");
        String version = "v12";

       //System.out.println("Port: "+port);
       currencyExchange.setEnvironment(port+" "+version+" "+host);

       return currencyExchange;
    }


}
