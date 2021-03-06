/*
  * JBoss, Home of Professional Open Source
  * Copyright 2007, JBoss Inc., and individual contributors as indicated
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
package org.jboss.security.xacml.factories;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;

import org.jboss.security.xacml.bridge.JBossPolicyFinder;
import org.jboss.security.xacml.core.JBossXACMLPolicy; 
import org.jboss.security.xacml.core.model.policy.ObjectFactory;
import org.jboss.security.xacml.core.model.policy.PolicyType;
import org.jboss.security.xacml.interfaces.XACMLPolicy;


/**
 *  A Policy Factory that creates XACML Policy
 *  or Policy Sets
 *  @author Anil.Saldhana@redhat.com
 *  @since  Jul 5, 2007 
 *  @version $Revision$
 */
public class PolicyFactory
{
   private static Class<?> constructingClass = JBossXACMLPolicy.class;

   /**
    * Set the class that constructs {@link XACMLPolicy}
    * @param clazz
    */
   public static void setConstructingClass(Class<?> clazz)
   {
      if (XACMLPolicy.class.isAssignableFrom(clazz) == false)
         throw new RuntimeException("clazz is not of type XACMLPolicy");
      constructingClass = clazz;
   }

   /**
    * Set the class that constructs {@link XACMLPolicy}
    * @param fqn
    */
   public static void setConstructingClass(String fqn)
   {
      ClassLoader tcl = SecurityActions.getContextClassLoader();
      try
      {
         setConstructingClass(tcl.loadClass(fqn));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Create a {@link XACMLPolicy}
    * @param policySetFile
    * @return
    * @throws Exception
    */
   public static XACMLPolicy createPolicySet(InputStream policySetFile) throws Exception
   {
      return (XACMLPolicy) getCtr().newInstance(new Object[]
      {policySetFile, XACMLPolicy.POLICYSET});
   }

   /**
    * Create {@link XACMLPolicy}
    * @param policySetFile inputstream to a policyset
    * @param theFinder
    * @return
    * @throws Exception
    */
   public static XACMLPolicy createPolicySet(InputStream policySetFile, JBossPolicyFinder theFinder) throws Exception
   {
      return (XACMLPolicy) getCtrWithFinder().newInstance(new Object[]
      {policySetFile, XACMLPolicy.POLICYSET, theFinder});
   }

   /**
    * Create a {@link XACMLPolicy}
    * @param policyFile inputstream to a Policy
    * @return
    * @throws Exception
    */
   public static XACMLPolicy createPolicy(InputStream policyFile) throws Exception
   {
      return (XACMLPolicy) getCtr().newInstance(new Object[]
      {policyFile, XACMLPolicy.POLICY});
   }

   /**
    * Construct {@link XACMLPolicy}
    * @param policyFile a {@link PolicyType}
    * @return
    * @throws Exception
    */
   public static XACMLPolicy createPolicy(PolicyType policyFile) throws Exception
   {
      JAXBElement<PolicyType> jaxbPolicy = new ObjectFactory().createPolicy(policyFile);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      JAXB.marshal(jaxbPolicy, baos);
      ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
      return (XACMLPolicy) getCtr().newInstance(new Object[]
      {bis, XACMLPolicy.POLICY});
   }
   
   /**
    * Create {@link XACMLPolicy}
    * @param is an inputstream to a policy or policyset
    * @param finder a {@link JBossPolicyFinder}
    * @return
    * @throws Exception
    */
   @SuppressWarnings("unchecked")
   public static XACMLPolicy create(InputStream is, JBossPolicyFinder finder) throws Exception
   {
      Constructor<XACMLPolicy> ctr = (Constructor<XACMLPolicy>) constructingClass.getConstructor(new Class[]{
             InputStream.class,JBossPolicyFinder.class});
      return ctr.newInstance(new Object[]{is, finder});
   }

   @SuppressWarnings("unchecked")
   private static Constructor<XACMLPolicy> getCtr() throws Exception
   {
      return (Constructor<XACMLPolicy>) constructingClass.getConstructor(new Class[]
      {InputStream.class, Integer.TYPE});
   }

   @SuppressWarnings("unchecked")
   private static Constructor<XACMLPolicy> getCtrWithFinder() throws Exception
   {
      return (Constructor<XACMLPolicy>) constructingClass.getConstructor(new Class[]
      {InputStream.class, Integer.TYPE, JBossPolicyFinder.class});
   }
}