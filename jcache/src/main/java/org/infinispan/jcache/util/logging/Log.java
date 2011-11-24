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
package org.infinispan.jcache.util.logging;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Cause;
import org.jboss.logging.LogMessage;
import org.jboss.logging.Message;
import org.jboss.logging.MessageLogger;

import javax.cache.CacheException;

import static org.jboss.logging.Logger.Level.FATAL;

/**
 * The JBoss Logging interface.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
@MessageLogger(projectCode = "ISPN")
public interface Log extends BasicLogger {

   @LogMessage(level = FATAL)
   @Message(value = "No CacheLoader defined for read-through cache '%s'")
   void invalidReadThroughConfiguration(String cacheName);

   @LogMessage(level = FATAL)
   @Message(value = "No CacheWriter defined for write-through cache '%s'")
   void invalidWriteThroughConfiguration(String cacheName);

   @Message(value = "The implementation class cannot be unwrapped to '%s'")
   IllegalArgumentException unableToUnwrapImplementation(Class<?> cls);

   @Message(value = "Cache '%s' isn't started")
   IllegalStateException cacheNotStarted(String cacheName);

   @Message(value = "Cache manager '%s' isn't started")
   IllegalStateException cacheManagerNotStarted(String cacheManagerName);

   @Message(value = "Unable to register statistics MBean for cache '%s'")
   CacheException unableToRegisterStatisticsMBean(String cacheName, @Cause Throwable throwable);

   @Message(value = "Unable to un-register statistics MBean for cache '%s'")
   CacheException unableToUnRegisterStatisticsMBean(String cacheName, @Cause Throwable throwable);
}
