package com.example.demo;

import java.security.cert.X509Certificate;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WarFilter implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

    X509Certificate[] certs = (X509Certificate[]) httpRequest.getAttribute("javax.servlet.request.X509Certificate");
    if (certs == null || certs.length == 0 || certs[0] == null) {
      System.out.println("Request to war file is NOT authenticated using client certificate: sending 404");
      HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
      httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
    } else {
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }
}
