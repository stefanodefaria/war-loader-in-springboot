package com.example.demo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import static org.hamcrest.CoreMatchers.is;

public class IntegrationTestsWithClientCertTest {

  private static final String BASE_URL = "https://localhost:";
  private static ApplicationContext server;

  @BeforeClass
  public static void startServer() throws GeneralSecurityException, IOException {
    server = SpringApplication.run(DemoApplication.class);
    initSslFactories();
  }

  @AfterClass
  public static void stopServer() {
    SpringApplication.exit(server);
  }

  @Test
  public void testSpringControllerWithCert8081() throws IOException {
    Assert.assertThat(doGet(BASE_URL + "8081/spring/"), is(HttpServletResponse.SC_OK));
  }

  @Test
  public void testWarWithCert8081() throws IOException {
    Assert.assertThat(doGet(BASE_URL + "8081/war/"), is(HttpServletResponse.SC_OK));
    Assert.assertThat(doGet(BASE_URL + "8081/war/hello"), is(HttpServletResponse.SC_OK));
    Assert.assertThat(doGet(BASE_URL + "8081/war/hello.jsp"), is(HttpServletResponse.SC_OK));
  }


  private int doGet(String url) throws IOException {
    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
    httpsURLConnection.setConnectTimeout(1000);
    httpsURLConnection.setReadTimeout(1000);

    return httpsURLConnection.getResponseCode();
  }

  private static void initSslFactories() throws IOException, GeneralSecurityException{
    ClassLoader classLoader = IntegrationTestsWithClientCertTest.class.getClassLoader();

    KeyStore trustStore = KeyStore.getInstance("JKS");
    trustStore.load(classLoader.getResourceAsStream("truststore.jks"), "changeit".toCharArray());
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(trustStore);

    KeyStore keystore = KeyStore.getInstance("PKCS12");
    keystore.load(classLoader.getResourceAsStream("clientkey.p12"), "changeit".toCharArray());
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keystore, "changeit".toCharArray());

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory()); // use it in all HTTPS requests
  }

}
