package com.springcoding.reactive.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.springcoding.reactive.document.Item;

import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveMongoRepository<Item,String>{
	
	public Flux<Item> findByDescription(String description); // the string after findBy should match with property name in Item . findByDesc will not work

}
