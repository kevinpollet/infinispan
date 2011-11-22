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


import org.infinispan.jcache.InfinispanCacheAdapter;

import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.NotificationScope;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
public class CacheEventBridge<K, V> {

   private final InfinispanCacheAdapter<K, V> cache;
   private final Set<CacheEntryListener<? super K, ? super V>> listeners;

   public CacheEventBridge(InfinispanCacheAdapter<K, V> cache) {
      this.cache = cache;
      this.listeners = new LinkedHashSet<CacheEntryListener<? super K, ? super V>>();
   }

   public boolean registerCacheEntryListener(CacheEntryListener<? super K, ? super V> listener, NotificationScope scope, boolean synchronous) {
      boolean registered = listeners.add(listener);

      if (registered) {
         if (listener instanceof CacheEntryCreatedListener) {
            cache.getInfinispanCache().addListener(new CacheEntryCreatedListenerAdapter(cache, scope, (CacheEntryCreatedListener<K, V>) listener));
         }
      }

      return registered;
   }

   public boolean unregisterCacheEntryListener(CacheEntryListener<?, ?> cacheEntryListener) {
      // un-register listener in Infinispan cache
      //TODO use a map??
      return false;
   }
}
