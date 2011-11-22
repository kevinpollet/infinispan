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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.cache.Cache;
import javax.cache.CacheStatistics;
import javax.cache.Caching;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

import static javax.cache.Status.STARTED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
//TODO KP: add tests for time and eviction
@Test(groups = "functional", testName = "jcache.CacheStatisticsTest")
public class CacheStatisticsTest {

   private Cache<Object, Object> cache;
   private CacheStatistics cacheStatistics;

   @BeforeClass
   public void beforeClass() {
      cache = Caching.getCacheManager("test-cache-manager")
            .createCacheBuilder("test-cache")
            .setStatisticsEnabled(true)
            .build();

      cacheStatistics = cache.getStatistics();
   }

   @BeforeMethod
   public void beforeMethod() {
      cache.removeAll();
      cacheStatistics.clearStatistics();
   }

   public void testGetName() {
      assertEquals(cacheStatistics.getName(), "test-cache");
   }

   public void testGetStatus() {
      assertEquals(cacheStatistics.getStatus(), STARTED);
   }

   public void testGetCacheHits() {
      cache.put("Kevin", "Hi");
      cache.get("Kevin");
      cache.get("Pete");

      // only get requests satisfied by the cache are counted
      assertEquals(cacheStatistics.getCacheHits(), 1);
   }

   public void testGetCacheHitPercentage() {
      cache.put("Kevin", "Hi");
      cache.get("Kevin");
      cache.get("Pete");

      // cache get hits / total cache gets
      assertEquals(cacheStatistics.getCacheHitPercentage(), 0.5f);
   }

   public void testGetCacheMisses() {
      cache.get("Kevin");
      cache.get("Pete");

      assertEquals(cacheStatistics.getCacheMisses(), 2);
   }

   public void testGetCacheMissPercentage() {
      cache.put("Kevin", "Hi");
      cache.get("Kevin");
      cache.get("Pete");

      // cache get misses / total cache gets
      assertEquals(cacheStatistics.getCacheMissPercentage(), 0.5f);
   }

   public void testGetCacheGets() {
      cache.put("Kevin", "Hi");
      cache.get("Kevin");
      cache.get("Pete");

      assertEquals(cacheStatistics.getCacheGets(), 2);
   }

   public void testGetCachePuts() {
      cache.put("Kevin", "Hi");
      cache.put("Pete", "Hi");

      assertEquals(cacheStatistics.getCachePuts(), 2);
   }

   public void testGetCacheRemovals() {
      cache.put("Kevin", "Hi");
      cache.put("Pete", "Hi");
      cache.remove("Kevin");
      cache.remove("Manik");

      // only remove requests satisfied by the cache are counted
      assertEquals(cacheStatistics.getCacheRemovals(), 1);
   }

   public void testJMXStatisticsMBeanRegistration() throws Exception {
      final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
      final CacheStatistics cacheStatistics = JMX.newMBeanProxy(mbeanServer,
                                                                new ObjectName("javax.cache:type=CacheStatistics,manager=test-cache-manager,cache=test-cache"),
                                                                CacheStatistics.class);

      assertNotNull(cacheStatistics);
      assertEquals(cacheStatistics.getName(), "test-cache");
   }

   public void testNoJMXStatisticsMBeanRegistration() throws Exception {
      Caching.getCacheManager("test-cache-manager")
            .createCacheBuilder("test-cache2")
            .build();

      final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
      final CacheStatistics cacheStatistics = JMX.newMBeanProxy(mbeanServer,
                                                                new ObjectName("javax.cache:type=CacheStatistics,manager=test-cache-manager,cache=test-cache2"),
                                                                CacheStatistics.class);

      assertNotNull(cacheStatistics);

      try {

         assertEquals(cacheStatistics.getName(), "test-cache");
         fail();

      } catch (Exception ex) {
         // success
      }
   }
}
