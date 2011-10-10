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

import org.testng.annotations.Test;

import javax.cache.CacheManager;
import javax.cache.Caching;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
@Test(groups = "functional", testName = "jcache.InfinispanCachingProviderTest")
public class InfinispanCachingProviderTest {

   public void testCreateDefaultCacheManager() throws InterruptedException {
      final CacheManager cacheManager = Caching.getCacheManager();

      assertNotNull(cacheManager);
      assertEquals(cacheManager.getName(), Caching.DEFAULT_CACHE_MANAGER_NAME);
   }

   public void testCreateCacheManagerWithName() {
      final CacheManager cacheManager = Caching.getCacheManager("foo");

      assertNotNull(cacheManager);
      assertEquals(cacheManager.getName(), "foo");
   }

   public void testCreateCacheManagerWithNameAndClassLoader() {
      final CacheManager cacheManager = Caching.getCacheManager(this.getClass().getClassLoader(), "foo");

      assertNotNull(cacheManager);
      assertEquals(cacheManager.getName(), "foo");
   }
}
