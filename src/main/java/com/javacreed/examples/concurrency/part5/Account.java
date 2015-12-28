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
package com.javacreed.examples.concurrency.part5;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Account {

  private final Lock lock = new ReentrantLock();

  @GuardedBy("lock")
  private int balance;

  @GuardedBy("lock")
  private Customer owner;

  public Account(final int balance) {
    this.balance = balance;
  }

  public void adjustBy(final int amount) throws InterruptedException {
    new RunVoidInLock(lock) {
      @Override
      protected void runInLock() {
        balance -= amount;
      }
    }.run();
  }

  public int getBalance() throws InterruptedException {
    return new RunIntInLock(lock) {
      @Override
      protected int runInLock() {
        return balance;
      }
    }.run();
  }

  public Customer getOwner() throws InterruptedException {
    return new RunGenericInLock<Customer>(lock) {
      @Override
      protected Customer runInLock() {
        return owner;
      }
    }.run();
  }

  public String getOwnerName() throws InterruptedException {
    return new RunGenericInLock<String>(lock) {
      @Override
      protected String runInLock() throws InterruptedException {
        if (owner == null) {
          return null;
        }
        return owner.getName();
      }
    }.run();
  }

  public void setOwner(final Customer owner) throws InterruptedException {
    new RunVoidInLock(lock) {
      @Override
      protected void runInLock() {
        Account.this.owner = owner;
      }
    }.run();
  }

  @Override
  public String toString() {
    return String.format("Balance: %d", balance);
  }
}
