/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.jcache;

import javax.cache.CacheConfiguration;
import javax.cache.transaction.IsolationLevel;
import javax.cache.transaction.Mode;

import static org.infinispan.jcache.util.Contracts.assertNotNull;

/**
 * This the cache configuration implementation.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
//TODO finish configuration
public class CacheConfigurationImpl implements CacheConfiguration {

   private final boolean readThrough;
   private final boolean writeThrough;
   private final boolean storeByValue;
   private final boolean staticsEnabled;
   private final IsolationLevel transactionsIsolationLevel;
   private final Mode transactionMode;
   private final Duration modifiedDuration;
   private final Duration accessedDuration;

   public CacheConfigurationImpl(boolean readThrough,
                                 boolean writeThrough,
                                 boolean storeByValue,
                                 boolean staticsEnabled,
                                 IsolationLevel transactionsIsolationLevel,
                                 Mode transactionMode,
                                 Duration modifiedDuration,
                                 Duration accessedDuration) {

      this.readThrough = readThrough;
      this.writeThrough = writeThrough;
      this.storeByValue = storeByValue;
      this.staticsEnabled = staticsEnabled;
      this.transactionsIsolationLevel = transactionsIsolationLevel;
      this.modifiedDuration = modifiedDuration;
      this.accessedDuration = accessedDuration;
      this.transactionMode = transactionMode;
   }

   @Override
   public boolean isReadThrough() {
      return readThrough;
   }

   @Override
   public void setReadThrough(boolean readThrough) {
      throw new UnsupportedOperationException("Cache configuration cannot be modified at runtime");
   }

   @Override
   public boolean isWriteThrough() {
      return writeThrough;
   }

   @Override
   public void setWriteThrough(boolean writeThrough) {
      throw new UnsupportedOperationException("Cache configuration cannot be modified at runtime");
   }

   @Override
   public boolean isStoreByValue() {
      return storeByValue;
   }

   @Override
   public boolean isStatisticsEnabled() {
      return staticsEnabled;
   }

   @Override
   public void setStatisticsEnabled(boolean enableStatistics) {
      throw new UnsupportedOperationException("Cache configuration cannot be modified at runtime");
   }

   @Override
   public boolean isTransactionEnabled() {
      return transactionMode != null;
   }

   @Override
   public Mode getTransactionMode() {
      return transactionMode;
   }

   @Override
   public IsolationLevel getTransactionIsolationLevel() {
      return transactionsIsolationLevel;
   }

   @Override
   public void setExpiry(ExpiryType type, Duration duration) {
      throw new UnsupportedOperationException("Cache configuration cannot be modified at runtime");
   }

   @Override
   public Duration getExpiry(ExpiryType type) {
      assertNotNull(type, "type parameter cannot be null");

      return type == ExpiryType.ACCESSED ? accessedDuration : modifiedDuration;
   }
}
