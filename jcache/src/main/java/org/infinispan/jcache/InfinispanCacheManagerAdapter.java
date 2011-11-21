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
import org.infinispan.DecoratedCache;
import org.infinispan.config.Configuration;
import org.infinispan.config.FluentConfiguration;
import org.infinispan.jcache.event.CacheListener;
import org.infinispan.jcache.util.logging.Log;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.util.logging.LogFactory;

import javax.cache.Cache;
import javax.cache.CacheBuilder;
import javax.cache.CacheConfiguration;
import javax.cache.CacheLoader;
import javax.cache.CacheManager;
import javax.cache.CacheWriter;
import javax.cache.Caching;
import javax.cache.OptionalFeature;
import javax.cache.Status;
import javax.transaction.UserTransaction;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static javax.cache.Status.STARTED;
import static javax.cache.Status.UNINITIALISED;
import static org.infinispan.jcache.util.Contracts.assertNotNull;

/**
 * The cache manager implementation. This cache manager implementation is backed by an Infinispan {@link
 * DefaultCacheManager} with the default configuration.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
class InfinispanCacheManagerAdapter implements CacheManager {

   private static final Log log = LogFactory.getLog(InfinispanCacheManagerAdapter.class, Log.class);

   private Status status;
   private final String name;
   private final ClassLoader classLoader;
   private final Set<Class<?>> immutableClasses;
   private final EmbeddedCacheManager cacheManager;
   private final ConcurrentHashMap<String, Cache<?, ?>> caches;

   InfinispanCacheManagerAdapter(String name, ClassLoader classLoader) {
      this.status = UNINITIALISED;
      this.name = name;
      this.classLoader = classLoader;
      this.immutableClasses = new HashSet<Class<?>>();
      this.cacheManager = new DefaultCacheManager();
      this.caches = new ConcurrentHashMap<String, Cache<?, ?>>();
      this.status = STARTED;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Status getStatus() {
      synchronized (status) {
         return status;
      }
   }

   @Override
   public <K, V> CacheBuilder<K, V> createCacheBuilder(String cacheName) {
      assertNotNull(cacheName, "cacheName must not be null");
      assertStarted();

      return new CacheBuilderImpl<K, V>(cacheName, this);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <K, V> Cache<K, V> getCache(String cacheName) {
      assertNotNull(cacheName, "cacheName must not be null");
      assertStarted();

      return (Cache<K, V>) caches.get(cacheName);
   }

   @Override
   @SuppressWarnings("unchecked")
   public Iterable<Cache<?, ?>> getCaches() {
      assertStarted();

      return caches.values();
   }

   @Override
   public boolean removeCache(String cacheName) throws IllegalStateException {
      assertNotNull(cacheName, "cacheName must not be null");
      assertStarted();

      if (cacheManager.cacheExists(cacheName) && caches.containsKey(cacheName)) {
         final Cache<?, ?> cache = caches.get(cacheName);

         if (cache.getStatus() != STARTED) {
            cache.stop();
            caches.remove(cacheName);
            cacheManager.removeCache(cacheName);

            return true;
         }

         throw log.cacheNotStarted(name);
      }
      return false;
   }

   @Override
   public UserTransaction getUserTransaction() {
      assertStarted();

      throw new UnsupportedOperationException("Transactions are currently not supported by this implementation");
   }

   @Override
   public boolean isSupported(OptionalFeature optionalFeature) {
      return Caching.isSupported(optionalFeature);
   }

   @Override
   public void registerImmutableClass(Class<?> immutableClass) {
      assertNotNull(immutableClass, "immutableClass must not be null");

      immutableClasses.add(immutableClass);
   }

   @Override
   public void shutdown() {
      synchronized (status) {
         for (Cache<?, ?> oneCache : caches.values()) {
            try {
               oneCache.stop();
            } catch (Exception e) {
               // we try to close all caches - ignore exception
            }
         }

         for (String oneCacheName : cacheManager.getCacheNames()) {
            try {
               cacheManager.removeCache(oneCacheName);
            } catch (Exception e) {
               // we try to close all caches - ignore exception
            }
         }

         caches.clear();

         immutableClasses.clear();
         cacheManager.stop();
         status = Status.STOPPED;
      }
   }

   @Override
   public <T> T unwrap(Class<T> cls) {
      if (cls.isAssignableFrom(this.getClass())) {
         return cls.cast(this);
      }
      throw new IllegalArgumentException("The implementation class cannot be unwrapped to '" + cls + "'");
   }

   /**
    * Returns the underlying Infinispan cache manager.
    *
    * @return the underlying Infinispan cache manager.
    */
   public EmbeddedCacheManager getInfinispanCacheManager() {
      return cacheManager;
   }

   @SuppressWarnings("unchecked")
   <K, V> Cache<K, V> createCache(String name,
                                  CacheConfiguration cacheConfiguration,
                                  CacheWriter<K, V> cacheWriter,
                                  CacheLoader<K, V> cacheLoader,
                                  Set<CacheListener<K, V>> listeners) {

      // if one cache already exist with the same name it is stopped
      Cache<K, V> cache = getCache(name);
      if (cache != null) {
         cache.stop();
      }

      // create the cache
      cacheManager.defineConfiguration(name, createInfinispanConfiguration(cacheConfiguration));
      cache = new InfinispanCacheAdapter<K, V>(
            new DecoratedCache<K, V>((AdvancedCache<K, V>) cacheManager.getCache(name).getAdvancedCache(), classLoader),
            cacheConfiguration,
            this,
            cacheWriter,
            cacheLoader,
            immutableClasses,
            listeners
      );

      // register and start the cache
      caches.put(name, cache);
      cache.start();

      return cache;
   }

   private Configuration createInfinispanConfiguration(CacheConfiguration cacheConfiguration) {
      final FluentConfiguration fluentConfiguration = new Configuration().fluent();

      if (cacheConfiguration.isStatisticsEnabled()) {
         fluentConfiguration.jmxStatistics();
      }
      if (cacheConfiguration.isStoreByValue()) {
         fluentConfiguration.storeAsBinary();
      }

      return fluentConfiguration.build();
   }

   /**
    * Asserts that the cache manager is started.
    *
    * @throws IllegalStateException if the cache manager is not started.
    * @see Status
    */
   private void assertStarted() {
      if (getStatus() != Status.STARTED) {
         throw log.cacheManagerNotStarted(name);
      }
   }
}
