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

import java.io.*;

@Configuration
public class WarLoader {

  @Bean
  public TomcatServletWebServerFactory tomcatFactory() {
    return new TomcatServletWebServerFactory() {
      @Override
      protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {

        addConnectorToTomcat(tomcat, 8081);
        try {
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
    Context context = tomcat.addWebapp(endpointPath, getResource(resourcePath));
    new File(tomcat.getServer().getCatalinaBase().getAbsolutePath() + "/webapps").mkdirs();
    context.setLoader(new WebappLoader(Thread.currentThread().getContextClassLoader()));
    addFilterToWarContext(context, WarFilter.class);
  }

  private void addConnectorToTomcat(Tomcat tomcat, int connectorPort) {
    Connector c = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    c.setPort(connectorPort);
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

  private String getResource(String resourcePath) throws IOException {

    String targetFilePath = resourcePath;
    InputStream is;
    if ((is = getClass().getClassLoader().getResourceAsStream(resourcePath)) != null) {

      File targetFile = File.createTempFile("tomcat-spring.", ".war");
      targetFile.deleteOnExit();
      OutputStream outStream = new FileOutputStream(targetFile);

      byte[] bytes = new byte[1024];
      int read;
      while ((read = is.read(bytes)) != -1) {
        outStream.write(bytes, 0, read);
      }
      outStream.flush();
      outStream.close();
      targetFilePath = targetFile.getAbsolutePath();

    }
    return targetFilePath;
  }
}
