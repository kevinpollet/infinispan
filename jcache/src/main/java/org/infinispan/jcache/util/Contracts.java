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

/**
 * Provides useful methods to validate method contracts.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
public final class Contracts {
   /**
    * Disable instantiation.
    */
   private Contracts() {
   }

   /**
    * Asserts that the given parameter object is not {@code null}.
    *
    * @param object  the object to verify.
    * @param message the exception message.
    * @throws NullPointerException if the given object is {@code null}
    */
   public static void assertNotNull(Object object, String message) {
      if (object == null) {
         throw new NullPointerException(message);
      }
   }

   /**
    * Asserts that the given number is positive.
    *
    * @param number  the number.
    * @param message the exception message.
    * @throws IllegalArgumentException if the given number is negative.
    */
   public static void assertPositive(long number, String message) {
      if (number < 0) {
         throw new IllegalArgumentException(message);
      }
   }
}
