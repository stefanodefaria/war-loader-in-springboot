package com.example.demo;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

@Configuration
public class WarLoader {

  @Bean
  public TomcatServletWebServerFactory tomcatFactory() {
    return new TomcatServletWebServerFactory() {
      @Override
      protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {

        try {
          addConnectorToTomcat(tomcat, 8081);
          addWarFileToTomcat(tomcat, "sample.war", "/war");
        } catch (IOException e) {
          System.err.println("Unable to load sample WAR file");
          e.printStackTrace();
        }
        return super.getTomcatWebServer(tomcat);
      }
    };
  }

  private void addWarFileToTomcat(Tomcat tomcat, String resourcePath, String endpointPath) throws IOException {
    Context context = tomcat.addWebapp(endpointPath, new ClassPathResource(resourcePath).getURL().getPath());
    new File(tomcat.getServer().getCatalinaBase().getAbsolutePath() + "/webapps").mkdirs();
    context.setLoader(new WebappLoader(Thread.currentThread().getContextClassLoader()));
    addFilterToWarContext(context, WarFilter.class);
  }

  private void addConnectorToTomcat(Tomcat tomcat, int connectorPort) throws IOException {
    Connector c = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);

    c.setAttribute("port", String.valueOf(connectorPort));
    c.setAttribute("SSLEnabled", "true");
    c.setAttribute("scheme", "https");
    c.setAttribute("secure", "true");
    c.setAttribute("keystoreFile", new ClassPathResource("keystore.jks").getURL().getPath());
    c.setAttribute("keystorePass", "changeit");
    c.setAttribute("truststoreFile", new ClassPathResource("truststore.jks").getURL().getPath());
    c.setAttribute("truststorePass", "changeit");
    c.setAttribute("clientAuth", "true");
    c.setAttribute("sslProtocol", "TLS");
    c.setAttribute("sslEnabledProtocols", "TLSv1.2");

    tomcat.getService().addConnector(c);
  }

  private void addFilterToWarContext(Context context, Class clazz) {
    FilterDef filter1definition = new FilterDef();
    filter1definition.setFilterName(clazz.getSimpleName());
    filter1definition.setFilterClass(clazz.getName());
    context.addFilterDef(filter1definition);

    FilterMap filter1mapping = new FilterMap();
    filter1mapping.setFilterName(clazz.getSimpleName());

    filter1mapping.addURLPattern("/*"); // this will only be applied to the .war endpoints
    context.addFilterMap(filter1mapping);
    context.addFilterDef(filter1definition);
  }
}
