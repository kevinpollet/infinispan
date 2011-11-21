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

import org.infinispan.jcache.event.CacheListener;
import org.infinispan.jcache.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import javax.cache.Cache;
import javax.cache.CacheBuilder;
import javax.cache.CacheConfiguration;
import javax.cache.CacheLoader;
import javax.cache.CacheManager;
import javax.cache.CacheWriter;
import javax.cache.InvalidConfigurationException;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.NotificationScope;
import javax.cache.transaction.IsolationLevel;
import javax.cache.transaction.Mode;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.cache.CacheConfiguration.Duration;
import static javax.cache.CacheConfiguration.Duration.ETERNAL;
import static javax.cache.CacheConfiguration.ExpiryType;
import static org.infinispan.jcache.util.Contracts.assertNotNull;

/**
 * The cache builder implementation.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
class CacheBuilderImpl<K, V> implements CacheBuilder<K, V> {

   private static final Log LOG = LogFactory.getLog(CacheBuilderImpl.class, Log.class);

   private final String name;
   private final CacheManager cacheManager;
   private boolean readThrough;
   private boolean writeThrough;
   private boolean storeByValue;
   private boolean statisticsEnabled;
   private IsolationLevel transactionsIsolationLevel;
   private Mode transactionMode;
   private Duration modifiedDuration;
   private Duration accessedDuration;
   private CacheLoader<K, V> cacheLoader;
   private CacheWriter<K, V> cacheWriter;
   private final Set<CacheListener<K, V>> listeners;

   CacheBuilderImpl(String name, CacheManager cacheManager) {
      this.name = name;
      this.cacheManager = cacheManager;
      this.readThrough = false;
      this.writeThrough = false;
      this.storeByValue = false;
      this.statisticsEnabled = false;
      this.transactionsIsolationLevel = null;
      this.transactionMode = null;
      this.modifiedDuration = ETERNAL;
      this.accessedDuration = ETERNAL;
      this.cacheLoader = null;
      this.cacheWriter = null;
      this.listeners = new LinkedHashSet<CacheListener<K, V>>();
   }

   @Override
   public Cache<K, V> build() {
      if (readThrough && cacheLoader == null) {
         LOG.invalidReadThroughConfiguration(name);
         throw new InvalidConfigurationException("No CacheLoader defined for read-through cache '" + name + "'");
      }
      if (writeThrough && cacheWriter == null) {
         LOG.invalidWriteThroughConfiguration(name);
         throw new InvalidConfigurationException("No CacheWriter defined for write-through cache '" + name + "'");
      }

      final CacheConfiguration cacheConfiguration = new CacheConfigurationImpl(
            readThrough,
            writeThrough,
            storeByValue,
            statisticsEnabled,
            cacheLoader,
            cacheWriter,
            transactionsIsolationLevel,
            transactionMode,
            modifiedDuration,
            accessedDuration
      );

      return cacheManager.unwrap(InfinispanCacheManagerAdapter.class)
            .createCache(name, cacheConfiguration, listeners);
   }

   @Override
   public CacheBuilder<K, V> setCacheLoader(CacheLoader<K, V> cacheLoader) {
      assertNotNull(cacheLoader, "cacheLoader must not be null");

      this.cacheLoader = cacheLoader;
      return this;
   }

   @Override
   public CacheBuilder<K, V> setCacheWriter(CacheWriter<K, V> cacheWriter) {
      assertNotNull(cacheWriter, "cacheWriter must not be null");

      this.cacheWriter = cacheWriter;
      return this;
   }

   @Override
   public CacheBuilder<K, V> registerCacheEntryListener(CacheEntryListener<K, V> cacheEntryListener, NotificationScope scope, boolean synchronous) {
      assertNotNull(cacheEntryListener, "cacheEntryListener must not be null");

      listeners.add(new CacheListener<K, V>(cacheEntryListener, scope, synchronous));
      return this;
   }

   @Override
   public CacheBuilder<K, V> setStoreByValue(boolean storeByValue) {
      this.storeByValue = storeByValue;
      return this;
   }

   @Override
   public CacheBuilder<K, V> setTransactionEnabled(IsolationLevel isolationLevel, Mode mode) {
      assertNotNull(isolationLevel, "isolationLevel must not be null");
      assertNotNull(mode, "mode must not be null");

      this.transactionsIsolationLevel = isolationLevel;
      this.transactionMode = mode;
      return this;
   }

   @Override
   public CacheBuilder<K, V> setStatisticsEnabled(boolean enableStatistics) {
      this.statisticsEnabled = enableStatistics;
      return this;
   }

   @Override
   public CacheBuilder<K, V> setReadThrough(boolean readThrough) {
      this.readThrough = readThrough;
      return this;
   }

   @Override
   public CacheBuilder<K, V> setWriteThrough(boolean writeThrough) {
      this.writeThrough = writeThrough;
      return this;
   }

   @Override
   public CacheBuilder<K, V> setExpiry(ExpiryType type, Duration timeToLive) {
      assertNotNull(type, "type must not be null");
      assertNotNull(timeToLive, "mode must not be null");

      if (type == ExpiryType.ACCESSED) {
         accessedDuration = timeToLive;
      } else {
         modifiedDuration = timeToLive;
      }
      return this;
   }
}
