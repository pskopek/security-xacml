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
package org.jboss.test.security.xacml.factories.util;

import java.io.InputStream;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.TestCase;

import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;


/**
 *  Utility class for the JBossXACML Tests
 *  @author Anil.Saldhana@redhat.com
 *  @since  Jul 22, 2007 
 *  @version $Revision$
 */
public class XACMLTestUtil
{
   //Enable for request trace
   private static boolean debug = "true".equals(System.getProperty("debug", "false"));
   
   /**
    * Given a request stored in a file, return the xacml request
    * @param requestFileLoc
    * @return
    * @throws Exception
    */
   public static RequestContext getRequest( String requestFileLoc ) throws Exception
   {
      ClassLoader tcl = Thread.currentThread().getContextClassLoader();
      InputStream is = tcl.getResourceAsStream(requestFileLoc);
      RequestContext request = RequestResponseContextFactory.createRequestCtx();
      request.readRequest(is);
      
      return request; 
   }

   /**
    * Get the decision from the PDP
    * @param pdp
    * @param requestFileLoc a file where the xacml request is stored
    * @return
    * @throws Exception
    */
   public static int getDecision(PolicyDecisionPoint pdp, String requestFileLoc) throws Exception
   {
      ResponseContext response = getResponse(pdp,requestFileLoc);
      if (response == null)
         throw new RuntimeException("Response is null");
      if (debug)
         response.marshall(System.out);
      return response.getDecision();
   }
   
   /**
    * Get the Response
    * @param pdp
    * @param requestFileLoc
    * @return
    * @throws Exception
    */
   public static ResponseContext getResponse(PolicyDecisionPoint pdp, 
         String requestFileLoc) throws Exception
   { 
      RequestContext request = getRequest( requestFileLoc ); 
      if (debug)
         request.marshall(System.out);
      return getResponse(pdp,request);
   }
   
   /**
    * Get the response for a request from the pdp
    * @param pdp
    * @param request
    * @return
    * @throws Exception
    */
   public static ResponseContext getResponse(PolicyDecisionPoint pdp
         , RequestContext request) throws Exception
   {
      return pdp.evaluate(request);
   }

   /**
    * Get the decision from the PDP
    * @param pdp
    * @param request RequestContext containing the request
    * @return
    * @throws Exception
    */
   public static int getDecision(PolicyDecisionPoint pdp, RequestContext request) throws Exception
   {
      ResponseContext response = pdp.evaluate(request);
      if (debug)
         response.marshall(System.out);
      TestCase.assertNotNull("Response is not null", response);
      return response.getDecision();
   }

   /**
    * Get a Group with the passed rolename
    * @param roleName rolename which will be placed as a principal
    * @return
    */
   public static Group getRoleGroup(final String roleName)
   {
      return new Group()
      {

         private Vector<Principal> vect = new Vector<Principal>();

         public boolean addMember(final Principal principal)
         {
            return vect.add(principal);
         }

         public boolean isMember(Principal principal)
         {
            return vect.contains(principal);
         }

         public Enumeration<? extends Principal> members()
         {
            vect.add(new Principal()
            {

               public String getName()
               {
                  return roleName;
               }
            });
            return vect.elements();
         }

         public boolean removeMember(Principal principal)
         {
            return vect.remove(principal);
         }

         public String getName()
         {
            return "ROLES";
         }
      };
   } 
}