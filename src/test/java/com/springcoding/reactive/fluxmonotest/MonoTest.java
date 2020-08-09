package com.springcoding.reactive.fluxmonotest;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MonoTest {

	@Test
	void testMono() {
		
		Mono<String> monoString = Mono.just("Spring");
		
		StepVerifier.create(monoString)
			.expectNext("Spring")
			.verifyComplete();
	}


	@Test
	void testMono_WithError() {
				
		StepVerifier.create(Mono.error(new RuntimeException("Exception Occured")))
			.expectError(RuntimeException.class)
			.verify();
	}

	
	@Test
	void testMono_UsingJustOrEmpty() {
				
		Mono<String> mono = Mono.justOrEmpty(null);
		StepVerifier.create(mono.log())
			.verifyComplete();
	}
	
	@Test
	void testMono_UsingSupplier() {
		
		Supplier<String> stringSupplier = ()-> "Spring";
				
		Mono<String> fromSupplier = Mono.fromSupplier(stringSupplier);
		StepVerifier.create(fromSupplier.log())
			.expectNext("Spring")
			.verifyComplete();	}
}
