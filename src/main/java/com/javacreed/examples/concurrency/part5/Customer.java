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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Customer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Customer.class);

  private final Lock lock = new ReentrantLock();

  @GuardedBy("lock")
  private final Set<Account> accounts = new LinkedHashSet<>();

  @GuardedBy("lock")
  private String name;

  public void addAccount(final Account account) throws InterruptedException {
    new RunVoidInLock(lock) {
      @Override
      protected void runInLock() throws InterruptedException {
        account.setOwner(Customer.this);
        accounts.add(account);
      }
    }.run();
  }

  public int getBalance() throws InterruptedException {
    return new RunIntInLock(lock) {
      @Override
      protected int runInLock() throws InterruptedException {
        int balance = 0;
        for (final Account account : accounts) {
          balance += account.getBalance();
        }
        return balance;
      }
    }.run();
  }

  public String getName() throws InterruptedException {
    return new RunGenericInLock<String>(lock) {
      @Override
      protected String runInLock() throws InterruptedException {
        Customer.LOGGER.debug("Invoking getName() on customer");
        return name;
      }
    }.run();
  }

  public void setName(final String name) throws InterruptedException {
    new RunVoidInLock(lock) {
      @Override
      protected void runInLock() throws InterruptedException {
        Customer.this.name = name;
      }
    }.run();
  }
}
