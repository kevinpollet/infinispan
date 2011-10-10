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

import org.infinispan.stats.Stats;

import javax.cache.Cache;
import javax.cache.CacheStatistics;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.infinispan.jcache.util.Contracts.assertPositive;

/**
 * The cache statistics implementation.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
//TODO KP: add operation time average
class CacheStatisticsImpl implements CacheStatistics {

   private final Stats stats;
   private final Cache<?, ?> cache;
   private AtomicLong totalCacheHits;
   private AtomicLong totalCacheMisses;
   private AtomicLong totalCacheGets;
   private AtomicLong totalCachePuts;
   private AtomicLong totalCacheRemovals;
   private AtomicLong totalCacheEvictions;
   private AtomicLong totalGetMillis;
   private AtomicLong totalPutMillis;
   private AtomicLong totalRemoveMillis;
   private AtomicReference<Date> statsCollectionStartDate;

   CacheStatisticsImpl(Cache<?, ?> cache, Stats stats) {
      this.stats = stats;
      this.cache = cache;
      this.totalCacheHits = new AtomicLong();
      this.totalCacheMisses = new AtomicLong();
      this.totalCacheGets = new AtomicLong();
      this.totalCachePuts = new AtomicLong();
      this.totalCacheRemovals = new AtomicLong();
      this.totalCacheEvictions = new AtomicLong();
      this.totalGetMillis = new AtomicLong();
      this.totalPutMillis = new AtomicLong();
      this.totalRemoveMillis = new AtomicLong();
      this.statsCollectionStartDate = new AtomicReference<Date>(new Date());
   }

   @Override
   public String getName() {
      return cache.getName();
   }

   @Override
   public String getStatus() {
      return cache.getStatus().toString();
   }

   @Override
   public void clearStatistics() {
      totalCacheHits.addAndGet(getCacheHits());
      totalCacheMisses.addAndGet(getCacheMisses());
      totalCacheGets.addAndGet(getCacheGets());
      totalCachePuts.addAndGet(getCachePuts());
      totalCacheEvictions.addAndGet(getCacheEvictions());
      totalCacheRemovals.addAndGet(getCacheRemovals());
      totalGetMillis.set(0);
      totalPutMillis.set(0);
      totalRemoveMillis.set(0);
      statsCollectionStartDate.set(new Date());
   }

   @Override
   public Date statsAccumulatingFrom() {
      return statsCollectionStartDate.get();
   }

   @Override
   public long getCacheHits() {
      return stats.getHits() - totalCacheHits.get();
   }

   @Override
   public float getCacheHitPercentage() {
      return getCacheGets() != 0 ? ((float) getCacheHits()) / getCacheGets() : Float.NaN;
   }

   @Override
   public long getCacheMisses() {
      return stats.getMisses() - totalCacheMisses.get();
   }

   @Override
   public float getCacheMissPercentage() {
      return getCacheGets() != 0 ? ((float) getCacheMisses()) / getCacheGets() : Float.NaN;
   }

   @Override
   public long getCacheGets() {
      return stats.getRetrievals() - totalCacheGets.get();
   }

   @Override
   public long getCachePuts() {
      return stats.getStores() - totalCachePuts.get();
   }

   @Override
   public long getCacheRemovals() {
      return stats.getRemoveHits() - totalCacheRemovals.get();
   }

   @Override
   public long getCacheEvictions() {
      return stats.getEvictions() - totalCacheEvictions.get();
   }

   @Override
   public long getAverageGetMillis() {
      return totalGetMillis.get() / getCacheGets();
   }

   @Override
   public long getAveragePutMillis() {
      return totalPutMillis.get() / getCachePuts();
   }

   @Override
   public long getAverageRemoveMillis() {
      return totalRemoveMillis.get() / getCacheRemovals();
   }

   public void addGetMillis(long millis) {
      assertPositive(millis, "millis parameter must be positive");

      totalGetMillis.addAndGet(millis);
   }

   public void addPutMillis(long millis) {
      assertPositive(millis, "millis parameter must be positive");

      totalPutMillis.addAndGet(millis);
   }

   public void addRemoveMillis(long millis) {
      assertPositive(millis, "millis parameter must be positive");

      totalRemoveMillis.addAndGet(millis);
   }
}
