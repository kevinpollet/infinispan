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
package org.infinispan.jcache.event;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.NotificationScope;

import static junit.framework.Assert.assertEquals;

/**
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
@Test(groups = "functional", testName = "jcache.event.CacheEntryCreatedEventTest")
public class CacheEntryCreatedEventTest {

   @BeforeTest
   public void beforeTest() {
      CacheEntryCreatedListenerImpl.nbCall = 0;
      CacheEntryCreatedListenerImpl.key = null;
      CacheEntryCreatedListenerImpl.value = null;
   }

   @Test
   public void syncCacheEntryCreatedEventTest() {
      CacheManager cacheManager = Caching.getCacheManager();
      Cache<String, String> cache = cacheManager.<String, String>createCacheBuilder("test")
            .registerCacheEntryListener(new CacheEntryCreatedListenerImpl(), NotificationScope.LOCAL, true)
            .build();

      cache.put("key", "value");

      assertEquals(1, CacheEntryCreatedListenerImpl.nbCall);
      assertEquals("key", CacheEntryCreatedListenerImpl.key);
      assertEquals("value", CacheEntryCreatedListenerImpl.value);
   }

   public static class CacheEntryCreatedListenerImpl implements CacheEntryCreatedListener<String, String> {

      public static int nbCall = 0;
      public static String key = null;
      public static String value = null;

      @Override
      public void entryCreated(CacheEntryEvent<? extends String, ? extends String> event) {
         nbCall++;
         key = event.getKey();
         value = event.getValue();
      }

      @Override
      public void entriesCreated(Iterable<CacheEntryEvent<? extends String, ? extends String>> events) {
      }
   }
}
