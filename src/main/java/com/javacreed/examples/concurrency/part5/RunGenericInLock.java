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

public abstract class RunGenericInLock<T> extends RunInLock<RunGenericInLock<T>> {

  public RunGenericInLock(final Lock lock) {
    super(lock);
  }

  public T run() throws InterruptedException, FailedToAcquireLockException {
    tryLock();
    try {
      return runInLock();
    } finally {
      unlock();
    }
  }

  protected abstract T runInLock() throws InterruptedException;

}
