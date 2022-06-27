package com.groupehillstone.security.token;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtAuthFilter extends GenericFilter {

    private static final Log logger = LogFactory.getLog(JwtAuthFilter.class);
    private TokenProcessor tokenProcessor;

    public JwtAuthFilter(TokenProcessor tokenProcessor) {
        this.tokenProcessor = tokenProcessor;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication;

        try {
            authentication = this.tokenProcessor.authenticate((HttpServletRequest) request);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception var6) {
            logger.error("Token processing error", var6);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
