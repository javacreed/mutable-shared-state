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
package com.javacreed.examples.concurrency.part3;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This test is marked as ignored as it <strong>may fail</strong> due to a deadlock. The sole purpose of this test was
 * to demonstrate how the classes under test can fail due to a deadlock.
 *
 * @author Albert Attard
 */
@Ignore
public class DeadlockTest {

  @Test(timeout = 5000)
  public void test() throws Exception {
    /* The objects under test */
    final Account account = new Account(1000);

    final Customer customer = new Customer();
    customer.setName("Java Creed");
    customer.addAccount(new Account(500));
    customer.addAccount(account);
    customer.addAccount(new Account(100));

    /* Ensures that both threads start together */
    final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    /*
     * Any exceptions thrown from within the threads are saved here and checked later. This makes sure that no errors go
     * undetected.
     */
    final AtomicReference<Exception> exception = new AtomicReference<>();

    /* Get the account's owner name */
    final Thread threadA = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          cyclicBarrier.await();
          account.getOwnerName();
        } catch (final Exception e) {
          exception.compareAndSet(null, e);
        }
      }
    }, "Thread-A");

    /* Get the customer's total balance by summing all accounts balances */
    final Thread threadB = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          cyclicBarrier.await();
          customer.getBalance();
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
  }
}
