/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.infinispan.query;

import org.jboss.util.Base64;

import java.io.Serializable;

/**
 * Warning, slow as a dog, uses serialization to get a byte representation of a class.  Implement your own!
 *
 * Repeat. It is HIGHLY RECOMMENDED THAT YOU PROVIDE YOUR OWN IMPLEMENTATION OF {@link org.infinispan.query.Transformer}
 *
 * @author Navin Surtani
 */
public class DefaultTransformer implements Transformer {
   
   @Override
   public Object fromString(String s) {
      return Base64.decodeToObject(s);
   }

   @Override
   public String toString(Object customType) {
      if (customType instanceof Serializable) {
         return Base64.encodeObject((Serializable) customType);
      } else {
         throw new IllegalArgumentException("Expected " + customType.getClass() + " to be Serializable!");
      }
   }
   
}