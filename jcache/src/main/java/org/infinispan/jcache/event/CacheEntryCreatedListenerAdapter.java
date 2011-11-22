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

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

import javax.cache.Cache;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.NotificationScope;

import static javax.cache.event.NotificationScope.LOCAL;
import static javax.cache.event.NotificationScope.REMOTE;

/**
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
@Listener
public class CacheEntryCreatedListenerAdapter<K, V> extends AbstractCacheListenerAdapter<K, V, CacheEntryCreatedListener<? super K, ? super V>> {

   public CacheEntryCreatedListenerAdapter(Cache<K, V> cache, NotificationScope scope, CacheEntryCreatedListener<? super K, ? super V> listener) {
      super(cache, scope, listener);
   }

   @CacheEntryModified
   public void handle(CacheEntryModifiedEvent<K, V> event) {
      if (!event.isPre()) {
         if ((event.isOriginLocal() && getScope() == LOCAL)
               || (!event.isOriginLocal() && getScope() == REMOTE)) {

            getListener().entryCreated(new CacheEntryEventImpl<K, V>(
                  getCache(),
                  event.getKey(),
                  event.getValue())
            );
         }
      }
   }
}
