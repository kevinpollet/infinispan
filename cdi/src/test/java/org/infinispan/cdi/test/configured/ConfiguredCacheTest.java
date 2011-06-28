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
package org.infinispan.cdi.test.configured;

import org.infinispan.AdvancedCache;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.infinispan.cdi.test.testutil.Deployments.baseDeployment;
import static org.testng.Assert.assertEquals;

/**
 * Tests that the simple form of configuration works
 *
 * @author Pete Muir
 * @see Config
 */
public class ConfiguredCacheTest extends Arquillian {

   @Deployment
   public static Archive<?> deployment() {
      return baseDeployment()
            .addPackage(ConfiguredCacheTest.class.getPackage());
   }

   /**
    * Inject a cache configured by the application
    */
   @Inject
   @Tiny
   private AdvancedCache<String, String> tinyCache;

   /**
    * Inject a cache configured by application
    */
   @Inject
   @Small
   private AdvancedCache<String, String> smallCache;

   @Test(groups = "functional")
   public void testTinyCache() {
      // Check that we have the correctly configured cache
      assertEquals(tinyCache.getConfiguration().getEvictionMaxEntries(), 1);
   }

   @Test(groups = "functional")
   public void testSmallCache() {
      // Check that we have the correctly configured cache
      assertEquals(smallCache.getConfiguration().getEvictionMaxEntries(), 10);
   }

}
