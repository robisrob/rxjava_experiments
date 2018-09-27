package com.github.robisrob.rxjavaexperiments;

import static java.util.List.of;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import rx.Observable;

public class ExperimentsTest {

  private final List<String> topic = of("a", "b", "c", "d");
  private int offset = 0;

  @Test
  public void makeObserver() {
    var consumedEvents = new ArrayList<String>();

    Observable<String> observable = makeObservable();
    observable.subscribe(message -> {
          consumedEvents.add(message);
         System.out.println(Thread.currentThread().getName() + " " +message);
        }
    );
    observable.subscribe(message -> {
          consumedEvents.add(message);
          System.out.println(Thread.currentThread().getName() + " " +message);
        }
    );    await().until(() -> isEquals(consumedEvents));
  }

  private boolean isEquals(ArrayList<String> consumedEvents) {
    System.out.println(consumedEvents);
    return topic.equals(consumedEvents);
  }

  private Observable<String> makeObservable() {
    return Observable.create(s -> {
      new Thread(() -> {
        while (offset < topic.size()) {
          String message = getMessage();
          if(message!=null) {
            s.onNext(message);
          }
        }
        s.onCompleted();
      }).start();
    });
  }

  private synchronized String getMessage() {
    if (offset < topic.size()) {
      System.out.println(offset);
      String message = topic.get(offset);
      offset++;
      blockThread();
      return message;
    }
    return null;
  }

  private void blockThread() {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


}
