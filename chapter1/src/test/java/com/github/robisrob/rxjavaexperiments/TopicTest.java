package com.github.robisrob.rxjavaexperiments;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FirstClassTest {

  @Test
  void helloWorld() {
    assertThat(new FirstClass().helloWorld()).isEqualTo("Hello World");
  }
}