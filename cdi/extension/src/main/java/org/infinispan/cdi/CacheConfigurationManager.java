/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other
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
package org.infinispan.cdi;

import org.infinispan.cdi.event.cachemanager.CacheManagerEventBridge;
import org.infinispan.cdi.util.logging.Log;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.util.logging.LogFactory;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Set;

import static org.infinispan.cdi.InfinispanExtension.ConfigurationHolder;

/**
 * This manager is responsible to register the configured caches in the corresponding cache managers when the CDI
 * container is initialized.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2012 SERLI
 */
final class CacheConfigurationManager {

   private static final Log log = LogFactory.getLog(CacheConfigurationManager.class, Log.class);

   @Inject
   private InfinispanExtension extension;

   @Inject
   private CacheManagerEventBridge eventBridge;

   @Inject
   private BeanManager beanManager;

   @Inject
   @Any
   private Instance<EmbeddedCacheManager> cacheManagers;

   public void observe(@Observes StartupEvent event) {
      final CreationalContext<Configuration> ctx = beanManager.createCreationalContext(null);
      final EmbeddedCacheManager defaultCacheManager = cacheManagers.select(new AnnotationLiteral<Default>() {}).get();

      for (ConfigurationHolder oneConfigurationHolder : extension.getCacheConfigurations()) {
         final String cacheName = oneConfigurationHolder.getName();
         final Configuration cacheConfiguration = oneConfigurationHolder.getProducer().produce(ctx);
         final Set<Annotation> cacheQualifiers = oneConfigurationHolder.getQualifiers();

         // if a specific cache manager is defined for this cache we use it
         final Instance<EmbeddedCacheManager> specificCacheManager = cacheManagers.select(cacheQualifiers.toArray(new Annotation[cacheQualifiers.size()]));
         final EmbeddedCacheManager cacheManager = specificCacheManager.isUnsatisfied() ? defaultCacheManager : specificCacheManager.get();

         // the default configuration is registered by the default cache manager producer
         if (!cacheName.trim().isEmpty()) {
            if (cacheConfiguration != null) {
               cacheManager.defineConfiguration(cacheName, cacheConfiguration);
               log.cacheConfigurationDefined(cacheName, cacheManager);
            } else if (!cacheManager.getCacheNames().contains(cacheName)) {
               cacheManager.defineConfiguration(cacheName, cacheManager.getDefaultCacheConfiguration());
               log.cacheConfigurationDefined(cacheName, cacheManager);
            }
         }

         // register cache manager observers
         eventBridge.registerObservers(cacheQualifiers, cacheName, cacheManager);
      }
   }
}
