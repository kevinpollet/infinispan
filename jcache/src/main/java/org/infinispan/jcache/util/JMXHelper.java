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
package org.infinispan.jcache.util;

import org.infinispan.jcache.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import javax.cache.CacheException;
import javax.cache.CacheStatistics;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;

import static org.infinispan.jcache.util.Contracts.assertNotNull;

/**
 * Provides method to register and un-register JMX MBean.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
public final class JMXHelper {

   private static final Log log = LogFactory.getLog(JMXHelper.class, Log.class);

   private static final String JMX_STATISTICS_DOMAIN = "javax.cache";
   private static final String JMX_STATISTICS_TYPE_PROPERTY_KEY = "type";
   private static final String JMX_STATISTICS_TYPE_PROPERTY_VALUE = "CacheStatistics";
   private static final String JMX_STATISTICS_MANAGER_PROPERTY_KEY = "manager";
   private static final String JMX_STATISTICS_CACHE_PROPERTY_KEY = "cache";

   /**
    * Disable instantiation.
    */
   private JMXHelper() {
   }

   /**
    * Register the cache statistics MBean with the following name (javax.cache:type=CacheStatistics,manager=cacheManagerName,cache=cacheName).
    *
    * @param cacheStatistics  the cache statistics MBean instance.
    * @param cacheManagerName the cache manager name.
    * @param cacheName        the cache name.
    * @throws CacheException if an error occurs during the MBean registration.
    */
   public static void registerStatisticsMBean(CacheStatistics cacheStatistics, String cacheManagerName, String cacheName) {
      assertNotNull(cacheStatistics, "cacheStatistics parameter cannot be null");
      assertNotNull(cacheManagerName, "cacheManagerName parameter cannot be null");
      assertNotNull(cacheName, "cacheName parameter cannot be null");

      try {

         final Hashtable<String, String> jmxProperties = new Hashtable<String, String>();
         jmxProperties.put(JMX_STATISTICS_TYPE_PROPERTY_KEY, JMX_STATISTICS_TYPE_PROPERTY_VALUE);
         jmxProperties.put(JMX_STATISTICS_MANAGER_PROPERTY_KEY, cacheManagerName);
         jmxProperties.put(JMX_STATISTICS_CACHE_PROPERTY_KEY, cacheName);

         final ObjectName objectName = new ObjectName(JMX_STATISTICS_DOMAIN, jmxProperties);
         ManagementFactory.getPlatformMBeanServer().registerMBean(cacheStatistics, objectName);

      } catch (Exception ex) {
         throw log.unableToRegisterStatisticsMBean(cacheName, ex);
      }
   }

   /**
    * Un-register the cache statistics MBean with the following name (javax.cache:type=CacheStatistics,manager=cacheManagerName,cache=cacheName).
    *
    * @param cacheManagerName the cache manager name.
    * @param cacheName        the cache name.
    * @throws CacheException if an error occurs during the MBean un-registration.
    */
   public static void unregisterStatisticsMBean(String cacheManagerName, String cacheName) {
      assertNotNull(cacheManagerName, "cacheManagerName parameter cannot be null");
      assertNotNull(cacheName, "cacheName parameter cannot be null");

      try {

         final Hashtable<String, String> jmxProperties = new Hashtable<String, String>();
         jmxProperties.put(JMX_STATISTICS_TYPE_PROPERTY_KEY, JMX_STATISTICS_TYPE_PROPERTY_VALUE);
         jmxProperties.put(JMX_STATISTICS_MANAGER_PROPERTY_KEY, cacheManagerName);
         jmxProperties.put(JMX_STATISTICS_CACHE_PROPERTY_KEY, cacheName);

         final ObjectName objectName = new ObjectName(JMX_STATISTICS_DOMAIN, jmxProperties);
         ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);

      } catch (Exception ex) {
         throw log.unableToUnRegisterStatisticsMBean(cacheName, ex);
      }
   }

}
