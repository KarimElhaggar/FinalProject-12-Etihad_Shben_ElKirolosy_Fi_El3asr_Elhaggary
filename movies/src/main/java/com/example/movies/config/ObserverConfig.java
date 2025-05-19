package com.example.movies.config;

import com.example.movies.observer.MoviePublisher;
import com.example.movies.rabbitmq.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class ObserverConfig {

    @Autowired
    private MoviePublisher moviePublisher;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @PostConstruct
    public void setupObservers() {
        moviePublisher.subscribe(rabbitMQProducer);
    }
}