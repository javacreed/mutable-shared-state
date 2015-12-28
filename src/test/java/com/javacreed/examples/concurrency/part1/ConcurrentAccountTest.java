/*
 * #%L
 * Mutable Shared State
 * %%
 * Copyright (C) 2012 - 2015 Java Creed
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.javacreed.examples.concurrency.part1;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test the Account class using two threads. One thread deposits while another thread withdraws money. Both threads are
 * started together to ensure collision (both threads access the shared object at the same time). The account starts
 * with a balance of 1000. The first thread withdraws a 1000 while the second thread deposits another 1000. Therefore
 * the balance should remain unchanged.
 * <p>
 * This test fails (may be not all the time) because the Account class is not thread-safe and its state will become
 * inconsistent (invalid) when accessed by more than one thread.
 *
 * @author Albert Attard
 */
@Ignore
public class ConcurrentAccountTest {

  /**
   * Test the Account class using two threads. One thread deposits while another thread withdraws money. Both threads
   * are started together to ensure collision (both threads access the shared object at the same time). The account
   * starts with a balance of 1000. The first thread withdraws a 1000 while the second thread deposits another 1000.
   * Therefore the balance should remain unchanged.
   * <p>
   * This test fails (may be not all the time) because the Account class is not thread-safe and its state will become
   * inconsistent (invalid) when accessed by more than one thread.
   *
   * @throws Exception
   *           if an error occurs while testing
   */
  @Test
  public void test() throws Exception {
    /* The object under test */
    final Account account = new Account(1000);

    /* Ensures that both threads start together */
    final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    /*
     * Any exceptions thrown from within the threads are saved here and checked later. This makes sure that no errors go
     * undetected.
     */
    final AtomicReference<Exception> exception = new AtomicReference<>();

    /* This thread withdraws 1000 from the account */
    final Thread threadA = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          cyclicBarrier.await();
          for (int i = 0; i < 1000; i++) {
            account.adjustBy(-1);
          }
        } catch (final Exception e) {
          exception.compareAndSet(null, e);
        }
      }
    }, "Thread-A");

    /* This thread deposits 1000 into the account */
    final Thread threadB = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          cyclicBarrier.await();
          for (int i = 0; i < 1000; i++) {
            account.adjustBy(1);
          }
        } catch (final Exception e) {
          exception.compareAndSet(null, e);
        }
      }
    }, "Thread-B");

    /* Start both threads */
    threadA.start();
    threadB.start();

    /* Wait for both threads to finish */
    threadA.join();
    threadB.join();

    /* Check that no thread failed */
    if (exception.get() != null) {
      throw exception.get();
    }

    /* Confirms that the balance is 1000. We started with 1000, then withdrawn 1000 and deposited another 1000. */
    Assert.assertEquals(1000, account.getBalance());
  }
}
