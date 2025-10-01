package com.devops.apigateway.config;


import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "product-service", configuration = LoadBalancerConfig.ProductConfig.class),
        @LoadBalancerClient(name = "cart-service", configuration = LoadBalancerConfig.CartConfig.class)
})
public class LoadBalancerConfig {

    public static class ProductConfig {
        @Bean
        public ServiceInstanceListSupplier productServiceInstanceListSupplier() {
            return new ServiceInstanceListSupplier() {
                @Override
                public String getServiceId() {
                    return "product-service";
                }

                @Override
                public Flux<List<ServiceInstance>> get() {
                    return Flux.just(Arrays.asList(
                            new DefaultServiceInstance("product-1", "product-service",
                                    "product-service-1", 8082, false),
                            new DefaultServiceInstance("product-2", "product-service",
                                    "product-service-2", 8083, false)
                    ));
                }
            };
        }
    }

    public static class CartConfig {
        @Bean
        public ServiceInstanceListSupplier cartServiceInstanceListSupplier() {
            return new ServiceInstanceListSupplier() {
                @Override
                public String getServiceId() {
                    return "cart-service";
                }

                @Override
                public Flux<List<ServiceInstance>> get() {
                    return Flux.just(Arrays.asList(
                            new DefaultServiceInstance("cart-1", "cart-service",
                                    "cart-service-1", 8084, false),
                            new DefaultServiceInstance("cart-2", "cart-service",
                                    "cart-service-2", 8085, false)
                    ));
                }
            };
        }
    }
}