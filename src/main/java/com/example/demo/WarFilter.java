package com.example.demo;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class WarFilter implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    String authType = ((HttpServletRequest) servletRequest).getAuthType();

    if(!HttpServletRequest.CLIENT_CERT_AUTH.equals(authType)) {
      System.out.println("Request to war file is NOT authenticated using client certificate");

      // then respond a 401 status code
    }

    filterChain.doFilter(servletRequest, servletResponse);
  }
}
