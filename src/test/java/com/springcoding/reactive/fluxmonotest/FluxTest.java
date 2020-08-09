package com.springcoding.reactive.fluxmonotest;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxTest {

	@Test
	void testElements_WithoutError() {

		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

		StepVerifier.create(stringFlux)
					.expectNext("Spring")
					.expectNext("Spring Boot")
					.expectNext("Reactive Spring")
					.verifyComplete();

	}

	@Test
	void testElements_WithError() {

		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception Occured"))).log();

		StepVerifier.create(stringFlux)
					.expectNext("Spring", "Spring Boot")
					.expectNext()
					.expectNext("Reactive Spring")
					// .expectError(RuntimeException.class)
					.expectErrorMessage("Exception Occured").verify();

	}

	@Test
	void testElementsCount_WithError() {

		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception Occured"))).log();

		StepVerifier.create(stringFlux)
					.expectNextCount(3)
					.expectErrorMessage("Exception Occured")
					.verify();

	}

	@Test
	void testFactory_UsingIterable() {

		List<String> names = Arrays.asList("Spring", "Spring Boot", "Reactive Spring");

		Flux<String> namesFlux = Flux.fromIterable(names).log();

		StepVerifier.create(namesFlux)
					.expectNext("Spring", "Spring Boot", "Reactive Spring")
					.verifyComplete();

	}

	@Test
	void testFactory_UsingArrays() {

		String[] names = new String[] { "Spring", "Spring Boot", "Reactive Spring" };
		Flux<String> namesFlux = Flux.fromArray(names).log();

		StepVerifier.create(namesFlux)
					.expectNext("Spring", "Spring Boot", "Reactive Spring")
					.verifyComplete();

	}

	@Test
	void testFactory_UsingStreams() {

		List<String> names = Arrays.asList("Spring", "Spring Boot", "Reactive Spring");

		Flux<String> namesFlux = Flux.fromStream(names.stream()).log();

		StepVerifier.create(namesFlux)
					.expectNext("Spring", "Spring Boot", "Reactive Spring")
					.verifyComplete();

	}

	@Test
	void testFactory_UsingRange() {

		Flux<Integer> rangeFlux = Flux.range(1, 5);

		StepVerifier.create(rangeFlux)
					.expectNext(1, 2, 3, 4, 5)
					.verifyComplete();

	}

	@Test
	void testFactory_UsingFilter() {

		List<String> names = Arrays.asList("adam", "anna", "jenny", "jack");

		Flux<String> namesFlux = Flux.fromIterable(names).filter(s -> s.startsWith("a")).log();

		StepVerifier.create(namesFlux)
					.expectNext("adam", "anna")
					.verifyComplete();

	}

	@Test
	void testTransform_UsingMap() {

		List<String> names = Arrays.asList("adam", "anna", "jenny", "jack");

		Flux<String> namesFlux = Flux.fromIterable(names).map(s -> s.toUpperCase()).log();

		StepVerifier.create(namesFlux)
					.expectNext("ADAM", "ANNA", "JENNY", "JACK")
					.verifyComplete();

	}

	@Test
	void testTransform_UsingFlatMap() {

		List<String> idList = Arrays.asList("id1", "id2", "id3", "id4");

		Flux<String> idFlux = Flux.fromIterable(idList).flatMap(id -> {
			return Flux.fromIterable(addToList(id));
		}).log();

		StepVerifier.create(idFlux)
					.expectNextCount(8)
					.verifyComplete();

	}

	private List<String> addToList(String id) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Arrays.asList(id, "newValue");
	}


	@Test
	void testFluxCombine_UsingMerge() {

		Flux<String> flux1 = Flux.just("id1", "id2", "id3", "id4").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("id5", "id6", "id7", "id8").delayElements(Duration.ofSeconds(1));
		
		//Flux<String> mergedFlux = Flux.merge(flux1,flux2);
		Flux<String> mergedFlux = Flux.concat(flux1,flux2);


		StepVerifier.create(mergedFlux.log())
					.expectSubscription()
					//.expectNextCount(8)
					.expectNext("id1", "id2", "id3", "id4","id5", "id6", "id7", "id8")
					.verifyComplete();

	}
	
}
