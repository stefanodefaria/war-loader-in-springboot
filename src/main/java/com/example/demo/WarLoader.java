package com.example.demo;

import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class WarLoader {
  private static final String WAR_RESOURCE_PATH = "sample.war";

  @Bean
  public TomcatServletWebServerFactory tomcatFactory() {
    return new TomcatServletWebServerFactory() {
      @Override
      protected TomcatWebServer getTomcatWebServer(org.apache.catalina.startup.Tomcat tomcat) {

        try {
          Context context = tomcat.addWebapp("/war", getResource(WAR_RESOURCE_PATH));
          new File(tomcat.getServer().getCatalinaBase().getAbsolutePath() + "/webapps").mkdirs();
          WebappLoader loader =
            new WebappLoader(Thread.currentThread().getContextClassLoader());
          context.setLoader(loader);
        } catch (IOException e) {
          System.err.println("Unable to load sample WAR file");
          e.printStackTrace();
        }
        return super.getTomcatWebServer(tomcat);
      }
    };
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
