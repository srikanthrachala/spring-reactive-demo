package com.springcoding.reactive.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.springcoding.reactive.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemRepositoryTest {

	@Autowired
	private ItemRepository itemRepository;
	
	List<Item> itemList = Arrays.asList(new Item(null,"Samsung Note 10",1299.99), 
										new Item(null,"iPhone ProMax",1099.99),
										new Item("test-id","test-description",99.99),
										new Item("delete-id","delete-item-description",99.99));
	
	@BeforeEach
	public void setUp() {
				
		itemRepository.deleteAll()
			.thenMany(Flux.fromIterable(itemList))
			.flatMap(itemRepository::save)
			.doOnNext(item->System.out.println("Item inserted"+item))
			.blockLast();
	}
	
	@Test
	public void testGetAllItems() {
		
		StepVerifier.create(itemRepository.findAll())
			.expectSubscription()
			.expectNextCount(4)
			.verifyComplete();		
	}
	
	@Test
	public void testGetItemById() {
		
		StepVerifier.create(itemRepository.findById("test-id"))
			.expectSubscription()
			.expectNextMatches(item->"test-description".equals(item.getDescription()))
			.verifyComplete();		
	}

	@Test
	public void testGetItemByDescription() {
		
		StepVerifier.create(itemRepository.findByDescription("test-description"))
			.expectSubscription()
			.expectNextMatches(item->"test-description".equals(item.getDescription()))
			.verifyComplete();		
	}

	@Test
	public void testSaveItem() {
		
		Mono<Item> savedItem = itemRepository.save(new Item(null,"saved-item-description",20.00));
		
		StepVerifier.create(savedItem)
			.expectSubscription()
			.expectNextMatches(item-> item.getId()!=null && "saved-item-description".equals(item.getDescription()))
			.verifyComplete();		
	}

	@Test
	public void testUpdateItem() {
		
		double newPrice = 40.00;
		
		Flux<Item> updatedItem = itemRepository.findByDescription("test-description")
									.map(item-> {
										item.setPrice(newPrice);
										return item;
									})
									.flatMap(itemRepository::save);
		
		StepVerifier.create(updatedItem)
			.expectSubscription()
			.expectNextMatches(item -> item.getPrice()== newPrice)
			.verifyComplete();		
	}

	
	@Test
	public void testDeleteItemById() {
				
		Mono<Void> deletedItem = itemRepository.findById("delete-id")
									.map(Item::getId)
									.flatMap(id -> {return itemRepository.deleteById(id);});
		
		StepVerifier.create(deletedItem)
			.expectSubscription()
			.verifyComplete();
		
		StepVerifier.create(itemRepository.findAll())
			.expectSubscription()
			.expectNextCount(3)
			.verifyComplete();		

	}

}
