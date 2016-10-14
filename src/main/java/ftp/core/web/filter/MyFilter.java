package ftp.core.web.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Kosta_Chuturkov on 10/13/2016.
 */
public class MyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println();
    }

    @Override
    public void destroy() {
        System.out.println();
    }
}
