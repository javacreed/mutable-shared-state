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

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Customer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Customer.class);

  @GuardedBy("this")
  private final Set<Account> accounts = new LinkedHashSet<>();

  @GuardedBy("this")
  private String name;

  public synchronized void addAccount(final Account account) {
    account.setOwner(this);
    accounts.add(account);
  }

  public synchronized int getBalance() {
    int balance = 0;
    for (final Account account : accounts) {
      balance += account.getBalance();
    }

    return balance;
  }

  public synchronized String getName() {
    Customer.LOGGER.debug("Invoking getName() on customer");
    return name;
  }

  public synchronized void setName(final String name) {
    this.name = name;
  }

}
