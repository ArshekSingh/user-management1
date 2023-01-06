package com.sts.finncub.usermanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sts.finncub.core.constants.RestMappingConstants;
import com.sts.finncub.core.entity.UserSession;
import com.sts.finncub.core.exception.UnauthorizedException;
import com.sts.finncub.core.response.Response;
import com.sts.finncub.core.util.TokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Import(TokenValidator.class)
public class RequestFilter implements Filter {

    @Autowired
    TokenValidator tokenValidator;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        boolean isValidRequest = true;
        if (servletRequest instanceof HttpServletRequest
                && ((HttpServletRequest) servletRequest).getRequestURI().contains("/api") && !((HttpServletRequest) servletRequest).getRequestURI().contains("/api-docs")) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String token = httpServletRequest.getHeader("Authorization");
            log.info("Headers - {}", httpServletRequest.getHeaderNames());
            if (token != null && !token.isEmpty()) {
                try {
                    String tokenValue = token.split(" ")[1];
                    UserSession userSession = tokenValidator.validateTokenAndReturnUserSession(tokenValue, httpServletRequest.getHeader("user-agent"));
                    if (userSession.getUserId() != null) {
                        setSecurityContext(userSession);
                    } else throw new UnauthorizedException(RestMappingConstants.AUTHENTICATION_FAILED);
                } catch (Exception exception) {
                    log.warn("Request is not valid - exception {}" + exception.getMessage());
                    isValidRequest = false;
                }
            } else {
                log.info("Token not found - value- {}", token);
                log.warn("Request is not valid - ");
                isValidRequest = false;
            }
        }
        if (isValidRequest) {
            log.info("Request is valid");
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            byte[] response = restResponseBytes(
                    new Response(RestMappingConstants.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED));
            servletResponse.getOutputStream().write(response);
            ((HttpServletResponse) servletResponse).setHeader("Content-Type", "application/json");
            ((HttpServletResponse) servletResponse).setStatus(401);
        }
    }

    private void setSecurityContext(UserSession userSession) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userSession, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void destroy() {

    }

    private byte[] restResponseBytes(Response response) throws IOException {
        String serialized = new ObjectMapper().writeValueAsString(response);
        return serialized.getBytes();
    }

}
