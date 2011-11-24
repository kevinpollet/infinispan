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


import org.infinispan.AdvancedCache;
import org.infinispan.jcache.event.CacheEventBridge;
import org.infinispan.jcache.event.CacheListener;
import org.infinispan.jcache.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.CacheStatistics;
import javax.cache.Status;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.NotificationScope;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import static javax.cache.Status.STARTED;
import static javax.cache.Status.STOPPED;
import static javax.cache.Status.UNINITIALISED;
import static org.infinispan.jcache.util.JMXHelper.registerStatisticsMBean;
import static org.infinispan.jcache.util.JMXHelper.unregisterStatisticsMBean;

/**
 * The cache implementation. This cache implementation is backed by an Infinispan {@link Cache}.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
public class InfinispanCacheAdapter<K, V> implements Cache<K, V> {

   private static final Log log = LogFactory.getLog(InfinispanCacheAdapter.class, Log.class);

   private Status status;
   private final AdvancedCache<K, V> cache;
   private final CacheConfiguration cacheConfiguration;
   private final CacheManager cacheManager;
   private final CacheEventBridge<K, V> cacheEventBridge;
   private final CacheStatistics cacheStatistics;

   InfinispanCacheAdapter(AdvancedCache<K, V> cache,
                          CacheConfiguration cacheConfiguration,
                          CacheManager cacheManager,
                          Set<CacheListener<K, V>> listeners) {

      this.status = UNINITIALISED;
      this.cache = cache;
      this.cacheConfiguration = cacheConfiguration;
      this.cacheManager = cacheManager;
      this.cacheEventBridge = new CacheEventBridge<K, V>(this);
      this.cacheStatistics = new CacheStatisticsImpl(this);

      // register listeners
      for (CacheListener<K, V> listener : listeners) {
         cacheEventBridge.registerCacheEntryListener(
               listener.getCacheEntryListener(),
               listener.getNotificationScope(),
               listener.isSynchronous()
         );
      }
   }


   @Override
   public V get(Object key) throws CacheException {
      assertStarted();

      return cache.get(key);
   }

   @Override
   public Map<K, V> getAll(Collection<? extends K> keys) throws CacheException {
      assertStarted();

      throw new UnsupportedOperationException("getAll operation is currently not supported");
   }

   @Override
   public boolean containsKey(Object key) throws CacheException {
      assertStarted();

      return cache.containsKey(key);
   }

   @Override
   public Future<V> load(K key) throws CacheException {
      assertStarted();

      throw new UnsupportedOperationException("load operation is currently not supported");
   }

   @Override
   public Future<Map<K, V>> loadAll(Collection<? extends K> keys) throws CacheException {
      assertStarted();

      throw new UnsupportedOperationException("loadAll operation is currently not supported");
   }

   @Override
   public CacheStatistics getStatistics() {
      return cacheConfiguration.isStatisticsEnabled() ? cacheStatistics : null;
   }

   @Override
   public void put(K key, V value) throws CacheException {
      assertStarted();

      cache.put(key, value);
   }

   @Override
   public V getAndPut(K key, V value) throws CacheException {
      assertStarted();

      return cache.put(key, value);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) throws CacheException {
      assertStarted();

      cache.putAll(map);
   }

   @Override
   public boolean putIfAbsent(K key, V value) throws CacheException {
      assertStarted();

      return cache.putIfAbsent(key, value) == null;
   }

   @Override
   public boolean remove(Object key) throws CacheException {
      assertStarted();

      return cache.remove(key) != null;
   }

   @Override
   public boolean remove(K key, V oldValue) throws CacheException {
      assertStarted();

      return cache.remove(key, oldValue);
   }

   @Override
   public V getAndRemove(Object key) throws CacheException {
      assertStarted();

      return cache.remove(key);
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) throws CacheException {
      assertStarted();

      return cache.replace(key, oldValue, newValue);
   }

   @Override
   public boolean replace(K key, V value) throws CacheException {
      assertStarted();

      return cache.replace(key, value) != null;
   }

   @Override
   public V getAndReplace(K key, V value) throws CacheException {
      assertStarted();

      V oldValue = cache.get(key);
      cache.put(key, value);
      return oldValue;
   }

   @Override
   public void removeAll(Collection<? extends K> keys) throws CacheException {
      assertStarted();

      for (K key : keys) {
         cache.remove(key);
      }
   }

   @Override
   public void removeAll() throws CacheException {
      assertStarted();

      cache.clear();
   }

   @Override
   public CacheConfiguration getConfiguration() {
      return cacheConfiguration;
   }

   @Override
   public boolean registerCacheEntryListener(CacheEntryListener<? super K, ? super V> cacheEntryListener, NotificationScope scope, boolean synchronous) {
      return cacheEventBridge.registerCacheEntryListener(cacheEntryListener, scope, synchronous);
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean unregisterCacheEntryListener(CacheEntryListener<?, ?> cacheEntryListener) {
      return cacheEventBridge.unregisterCacheEntryListener(cacheEntryListener);
   }

   @Override
   public String getName() {
      return cache.getName();
   }

   @Override
   public CacheManager getCacheManager() {
      return cacheManager;
   }

   @Override
   public <T> T unwrap(Class<T> cls) {
      if (cls.isAssignableFrom(this.getClass())) {
         return cls.cast(this);
      }
      throw log.unableToUnwrapImplementation(cls);
   }

   @Override
   public void start() throws CacheException {
      synchronized (status) {
         if (cacheConfiguration.isStatisticsEnabled()) {
            registerStatisticsMBean(cacheStatistics, cacheManager.getName(), getName());
         }

         cache.start();
         status = STARTED;
      }
   }

   @Override
   public void stop() throws CacheException {
      synchronized (status) {
         if (cacheConfiguration.isStatisticsEnabled()) {
            unregisterStatisticsMBean(cacheManager.getName(), getName());
         }

         cache.clear();
         cache.stop();
         status = STOPPED;
      }
   }

   @Override
   public Status getStatus() {
      synchronized (status) {
         return status;
      }
   }

   @Override
   public Iterator<Entry<K, V>> iterator() {
      return new IteratorImpl<K, V>(cache.entrySet().iterator());
   }

   /**
    * Returns the underlying Infinispan cache.
    *
    * @return the underlying Infinispan cache.
    */
   public org.infinispan.Cache<K, V> getInfinispanCache() {
      return cache;
   }

   /**
    * Asserts that the cache is started.
    *
    * @throws IllegalStateException if the cache is not started.
    * @see Status
    */
   private void assertStarted() {
      if (getStatus() != STARTED) {
         throw log.cacheNotStarted(cache.getName());
      }
   }

   /**
    * Implementation of {@link Cache.Entry}.
    *
    * @param <K> the type of the key.
    * @param <V> the type of the value.
    * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
    */
   private static class EntryImpl<K, V> implements Entry<K, V> {

      private final Map.Entry<K, V> entry;

      private EntryImpl(Map.Entry<K, V> entry) {
         this.entry = entry;
      }

      @Override
      public K getKey() {
         return entry.getKey();
      }

      @Override
      public V getValue() {
         return entry.getValue();
      }
   }

   /**
    * Implementation of {@link Cache.Entry} iterator.
    *
    * @param <K> the type of the key.
    * @param <V> the type of the value.
    * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
    */
   private static class IteratorImpl<K, V> implements Iterator<Entry<K, V>> {

      private final Iterator<Map.Entry<K, V>> iterator;

      private IteratorImpl(Iterator<Map.Entry<K, V>> iterator) {
         this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
         return iterator.hasNext();
      }

      @Override
      public Entry<K, V> next() {
         return new EntryImpl<K, V>(iterator.next());
      }

      @Override
      public void remove() {
         iterator.remove();
      }
   }
}
