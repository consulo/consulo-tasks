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
}