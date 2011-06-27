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
package org.jboss.seam.infinispan.test.testutil;

import org.jboss.seam.infinispan.Infinispan;
import org.jboss.seam.infinispan.event.cachemanager.CacheManagerEventBridge;
import org.jboss.seam.infinispan.interceptors.CacheResultInterceptor;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.seam.infinispan.event.cache.CacheEventBridge;

public class Deployments {

   public static WebArchive baseDeployment() {
      return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackage(Infinispan.class.getPackage())
            .addPackage(CacheEventBridge.class.getPackage())
            .addPackage(CacheManagerEventBridge.class.getPackage())
            .addPackage(CacheResultInterceptor.class.getPackage())
            .addAsManifestResource(Deployments.class.getResource("/META-INF/beans.xml"), "beans.xml")
            .addAsLibraries(
                  DependencyResolvers.use(MavenDependencyResolver.class)
                  		.loadReposFromPom("pom.xml")
                        .artifact("org.jboss.seam.solder:seam-solder")
                        .resolveAs(GenericArchive.class)
            );
   }
}
