package com.example.demo;

import java.security.cert.X509Certificate;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WarFilter implements Filter {

  static final String ALLOWED_PORT = "allowedPort";
  private int allowedPort;

  @Override
  public void init(FilterConfig filterConfig) {

    allowedPort = Integer.valueOf(filterConfig.getInitParameter(ALLOWED_PORT));
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    // .war endpoints should only be accessible via specific port and when req is using client certificates
    if(servletRequest.getServerPort() == allowedPort) {
      HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

      X509Certificate[] certs = (X509Certificate[]) httpRequest.getAttribute("javax.servlet.request.X509Certificate");
      if (certs != null && certs.length != 0 && certs[0] != null) {
        filterChain.doFilter(servletRequest, servletResponse);
        return;
      }
    }

    System.out.println("Request to war file is NOT authenticated using client certificate: sending 404");
    HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
    httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
  }
}
