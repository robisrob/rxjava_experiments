package com.github.robisrob.rxjavaexperiments;

import rx.Single;

public class SubscribeReturnsMono {

  public static void main(String... args) {
    saveSomethingAsync().subscribe(savedId -> System.out.println(savedId));
  }

  public static Single<String> saveSomethingAsync() {
    return getExtraDataFromAsyncService()
        .doOnSuccess(asyncData -> saveSomethingSync(asyncData));
  }

  public static void saveSomethingSync(String e) {
    System.out.println("Saving: " + e);
  }

  public static Single<String> getExtraDataFromAsyncService() {
    return Single.just("Some ansync data");
  }

}
