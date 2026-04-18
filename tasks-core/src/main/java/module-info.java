/**
 * @author VISTALL
 * @since 02/02/2023
 */
module com.intellij.tasks {
  requires consulo.application.api;
  requires consulo.base.icon.library;
  requires consulo.code.editor.api;
  requires consulo.color.scheme.api;
  requires consulo.component.api;
  requires consulo.document.api;
  requires consulo.execution.api;
  requires consulo.http.api;
  requires consulo.language.api;
  requires consulo.language.editor.api;
  requires consulo.language.editor.ui.api;
  requires consulo.language.impl;
  requires consulo.localize.api;
  requires consulo.logging.api;
  requires consulo.project.api;
  requires consulo.task.api;
  requires consulo.ui.api;
  requires consulo.ui.ex.awt.api;
  requires consulo.util.collection;
  requires consulo.util.dataholder;
  requires consulo.util.io;
  requires consulo.util.jdom;
  requires consulo.util.lang;
  requires consulo.util.xml.serializer;
  requires consulo.version.control.system.api;
  requires consulo.virtual.file.system.api;

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
