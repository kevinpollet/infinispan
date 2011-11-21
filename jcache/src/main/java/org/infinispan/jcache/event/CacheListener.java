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

import javax.cache.event.CacheEntryListener;
import javax.cache.event.NotificationScope;

/**
 * Contains all information associated to a cache listener.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
public class CacheListener<K, V> {

   private final boolean synchronous;
   private final NotificationScope notificationScope;
   private final CacheEntryListener<? super K, ? super V> cacheEntryListener;

   public CacheListener(CacheEntryListener<? super K, ? super V> cacheEntryListener, NotificationScope notificationScope, boolean synchronous) {
      this.cacheEntryListener = cacheEntryListener;
      this.notificationScope = notificationScope;
      this.synchronous = synchronous;
   }

   public boolean isSynchronous() {
      return synchronous;
   }

   public NotificationScope getNotificationScope() {
      return notificationScope;
   }

   public CacheEntryListener<? super K, ? super V> getCacheEntryListener() {
      return cacheEntryListener;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CacheListener that = (CacheListener) o;

      if (cacheEntryListener != null ? !cacheEntryListener.equals(that.cacheEntryListener) : that.cacheEntryListener != null) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      return cacheEntryListener != null ? cacheEntryListener.hashCode() : 0;
   }
}
