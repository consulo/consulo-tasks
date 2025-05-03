/**
 * @author VISTALL
 * @since 02/02/2023
 */
module com.intellij.tasks {
  requires consulo.ide.api;

  requires com.google.gson;
  requires xmlrpc.common;
  requires xmlrpc.client;
  requires axis;
  requires jakarta.xml.rpc.api;
  requires commons.httpclient;
  requires json.path;

  requires java.rmi;
  requires java.naming;

  // TODO remove
  requires java.desktop;
  requires forms.rt;

  opens com.intellij.tasks.jira to consulo.util.xml.serializer;
  opens com.intellij.tasks.impl to consulo.util.xml.serializer;
  opens com.intellij.tasks.jira.model to consulo.util.xml.serializer, com.google.gson;
  opens com.intellij.tasks.jira.model.api2 to com.google.gson;
}