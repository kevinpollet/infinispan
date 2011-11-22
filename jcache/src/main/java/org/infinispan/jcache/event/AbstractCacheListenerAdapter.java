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

import javax.cache.Cache;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.NotificationScope;

/**
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
abstract class AbstractCacheListenerAdapter<K, V, T extends CacheEntryListener<? super K, ? super V>> {

   private final Cache<K, V> cache; //TODO KP: Remote cache??
   private final NotificationScope scope;
   private final T listener;

   AbstractCacheListenerAdapter(Cache<K, V> cache, NotificationScope scope, T listener) {
      this.cache = cache;
      this.scope = scope;
      this.listener = listener;
   }

   public Cache<K, V> getCache() {
      return cache;
   }

   public NotificationScope getScope() {
      return scope;
   }

   public T getListener() {
      return listener;
   }
}
