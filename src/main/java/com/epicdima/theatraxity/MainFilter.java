package com.epicdima.theatraxity;

import com.epicdima.theatraxity.helpers.ControllerUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author EpicDima
 */
@WebFilter(value = "/*", filterName = "MainFilter", asyncSupported = true)
public final class MainFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if ("/".equals(uri) || uri.startsWith("/api/")) {
            chain.doFilter(req, resp);
            return;
        } else if (uri.startsWith("/js/") || uri.startsWith("/css/") || uri.startsWith("/html/")) {
            request.getRequestDispatcher("/public" + uri).forward(req, resp);
        }
        request.getRequestDispatcher("/").forward(req, resp);
    }

    @Override
    public void destroy() {}
}
