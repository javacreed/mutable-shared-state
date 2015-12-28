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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Account {

  private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);

  @GuardedBy("this")
  private int balance;

  @GuardedBy("this")
  private Customer owner;

  public Account(final int balance) {
    this.balance = balance;
  }

  public synchronized void adjustBy(final int amount) {
    balance -= amount;
  }

  public synchronized int getBalance() {
    Account.LOGGER.debug("Invoking getBalance() on {}", this);
    return balance;
  }

  public synchronized Customer getOwner() {
    return owner;
  }

  public synchronized String getOwnerName() {
    if (owner == null) {
      return null;
    }

    return owner.getName();
  }

  public synchronized void setOwner(final Customer owner) {
    this.owner = owner;
  }

  @Override
  public String toString() {
    return String.format("Balance: %d", balance);
  }
}
