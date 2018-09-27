package com.github.robisrob.rxjavaexperiments;

import static java.time.Duration.ofMillis;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FlatmapTest {

  @Test
  public void rxJava_flatMap_doesNotSpinUpThreads() {

    List<String> threads = Observable.range(0, 10)
        .flatMap(e -> Observable.just(Thread.currentThread().getName()))
        .distinct()
        .toList()
        .toBlocking()
        .single();

    assertThat(threads).hasSize(1);
  }

  @Test
  public void reactor_flatMap_doesNotSpinUpThreads() {

    List<String> threads = Flux.range(0, 10)
        .flatMap(e -> Flux.just(Thread.currentThread().getName()))
        .distinct()
        .toStream()
        .collect(toList());

    assertThat(threads).hasSize(1);
  }

  @Test
  public void rxJava_delay_WillSpinupThreads() {

    List<String> threads = Observable.range(0, 1000)
        .flatMap(e ->
            Observable.just("something")
                .delay(1, TimeUnit.MILLISECONDS, Schedulers.newThread())
                .map(i -> Thread.currentThread().getName()))
        .distinct()
        .toList()
        .toBlocking()
        .single();

    assertThat(threads).hasSize(1000);
  }

  @Test
  public void reactor_delay_WillSpinupThreads() {

    List<String> threads = Flux.range(0, 1000)
        .flatMap(e ->
            Flux.just("something")
                .delayElements(ofMillis(1), reactor.core.scheduler.Schedulers.newParallel("scheduler", 1000))
                .map(i -> Thread.currentThread().getName()))
        .distinct()
        .toStream().collect(toList());

    assertThat(threads).hasSize(1000);
  }

  @Test
  public void rxJava_delay_configureMaxConcurrentSubscribes() {

    List<Integer> events = Observable.range(0, 1000)
        .flatMap(e ->
            Observable.just(e)
                .delay(1, TimeUnit.MILLISECONDS, Schedulers.newThread()), 1)
        .distinct()
        .toList()
        .toBlocking()
        .single();

    assertThat(events).isEqualTo(IntStream.range(0, 1000).boxed().collect(toList()));
  }

  @Test
  public void reactor_delay_configureMaxConcurrentSubscribes() {

    List<Integer> events = Flux.range(0, 1000)
        .flatMap(e ->
            Flux.just(e)
                .delayElements(ofMillis(1), reactor.core.scheduler.Schedulers.newParallel("scheduler", 1000)), 1)
        .distinct()
        .toStream()
        .collect(toList());


    assertThat(events).isEqualTo(IntStream.range(0, 1000).boxed().collect(toList()));
  }

  @Test
  public void rxJava_concatMapNotConcurrent() {

    List<Integer> events = Observable.range(0, 1000)
        .concatMap(e ->
            Observable.just(e)
                .delay(1, TimeUnit.MILLISECONDS, Schedulers.newThread()))
        .distinct()
        .toList()
        .toBlocking()
        .single();

    assertThat(events).isEqualTo(IntStream.range(0, 1000).boxed().collect(toList()));
  }

  @Test
  public void reactor_concatMapNotConcurrent() {

    List<Integer> events = Flux.range(0, 1000)
        .concatMap(e ->
            Flux.just(e)
                .delayElements(ofMillis(1), reactor.core.scheduler.Schedulers.newParallel("scheduler", 1000)))
        .distinct()
        .toStream()
        .collect(toList());

    assertThat(events).isEqualTo(IntStream.range(0, 1000).boxed().collect(toList()));
  }

}
