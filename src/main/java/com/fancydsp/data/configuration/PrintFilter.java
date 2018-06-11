package com.fancydsp.data.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//开启annotation自动加入filter
@WebFilter
public class PrintFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    long visitCount = 0;
    public PrintFilter() {
        super();
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        visitCount ++;
        logger.info("from : {} ,request count : {}, source : {} " ,request.getRemoteUser(), visitCount,request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
