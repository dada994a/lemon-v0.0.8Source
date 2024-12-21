package com.lemonclient.api.util.log4j;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public enum EmptyJndiContext implements Context, DirContext {
   INSTANCE;

   public Object lookup(Name name) {
      return null;
   }

   public Object lookup(String name) {
      return null;
   }

   public void bind(Name name, Object obj) {
   }

   public void bind(String name, Object obj) {
   }

   public void rebind(Name name, Object obj) {
   }

   public void rebind(String name, Object obj) {
   }

   public void unbind(Name name) {
   }

   public void unbind(String name) {
   }

   public void rename(Name oldName, Name newName) {
   }

   public void rename(String oldName, String newName) {
   }

   public NamingEnumeration<NameClassPair> list(Name name) {
      return null;
   }

   public NamingEnumeration<NameClassPair> list(String name) {
      return null;
   }

   public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public void destroySubcontext(Name name) {
   }

   public void destroySubcontext(String name) {
   }

   public Context createSubcontext(Name name) throws NamingException {
      return (Context)panic();
   }

   public Context createSubcontext(String name) throws NamingException {
      return (Context)panic();
   }

   public Object lookupLink(Name name) {
      return null;
   }

   public Object lookupLink(String name) {
      return null;
   }

   public NameParser getNameParser(Name name) throws NamingException {
      return (NameParser)panic();
   }

   public NameParser getNameParser(String name) throws NamingException {
      return (NameParser)panic();
   }

   public Name composeName(Name name, Name prefix) throws NamingException {
      return (Name)panic();
   }

   public String composeName(String name, String prefix) throws NamingException {
      return (String)panic();
   }

   public Object addToEnvironment(String propName, Object propVal) {
      return null;
   }

   public Object removeFromEnvironment(String propName) {
      return null;
   }

   public Hashtable<?, ?> getEnvironment() {
      return new Hashtable();
   }

   public void close() {
   }

   public String getNameInNamespace() {
      return "";
   }

   public Attributes getAttributes(Name name) throws NamingException {
      return (Attributes)panic();
   }

   public Attributes getAttributes(String name) throws NamingException {
      return (Attributes)panic();
   }

   public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
      return (Attributes)panic();
   }

   public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
      return (Attributes)panic();
   }

   public void modifyAttributes(Name name, int mod_op, Attributes attrs) {
   }

   public void modifyAttributes(String name, int mod_op, Attributes attrs) {
   }

   public void modifyAttributes(Name name, ModificationItem[] mods) {
   }

   public void modifyAttributes(String name, ModificationItem[] mods) {
   }

   public void bind(Name name, Object obj, Attributes attrs) {
   }

   public void bind(String name, Object obj, Attributes attrs) {
   }

   public void rebind(Name name, Object obj, Attributes attrs) {
   }

   public void rebind(String name, Object obj, Attributes attrs) {
   }

   public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
      return (DirContext)panic();
   }

   public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
      return (DirContext)panic();
   }

   public DirContext getSchema(Name name) throws NamingException {
      return (DirContext)panic();
   }

   public DirContext getSchema(String name) throws NamingException {
      return (DirContext)panic();
   }

   public DirContext getSchemaClassDefinition(Name name) throws NamingException {
      return (DirContext)panic();
   }

   public DirContext getSchemaClassDefinition(String name) throws NamingException {
      return (DirContext)panic();
   }

   public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
      return (NamingEnumeration)panic();
   }

   public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
      return (NamingEnumeration)panic();
   }

   private static <T> T panic() throws NamingException {
      throw new NamingException("JNDI has been removed");
   }
}
