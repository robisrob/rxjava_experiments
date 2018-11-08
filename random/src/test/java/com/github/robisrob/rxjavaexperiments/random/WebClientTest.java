package com.github.robisrob.rxjavaexperiments.random;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

public class WebClientTest {

  private WebClient webClient;

  @BeforeEach
  public void setup() {
    webClient = WebClient.create();
  }

  @Test
  public void testExchange() {
    webClient.get().uri("http://www.google.com/doesgivea404").exchange().block();
  }

  @Test
  public void testRetrieve() {
    assertThatThrownBy(() -> webClient
        .get()
        .uri("http://www.google.com/doesgivea404")
        .retrieve()
        .bodyToMono(Void.class).block())
        .isInstanceOf(WebClientException.class)
        .hasMessage("404 Not Found");
  }

}
