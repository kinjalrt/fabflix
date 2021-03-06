import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();
    private final ArrayList<String> staffURIs = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("api/adlogin");
        allowedURIs.add("admin-login.js");
        allowedURIs.add("admin-login.html");

        staffURIs.add("dashboard.html");
        staffURIs.add("api/dashboard");
        staffURIs.add("dashboard.js");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        System.out.println(httpRequest.getRequestURI());

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());
        // Check if this URL is allowed to access without logging in
        if(this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())){
            // Keep default action: pass along the filter chain
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // Redirect to login page if the "user" attribute doesn't exist in session
        if(httpRequest.getSession().getAttribute("user") == null) {
            if(this.isNotAllowedWithoutStaffLogin(httpRequest.getRequestURI())) {
                httpResponse.sendRedirect("admin-login.html");
            }
            else
                httpResponse.sendRedirect("login.html");
        }
        else if(this.isNotAllowedWithoutStaffLogin(httpRequest.getRequestURI()) &&
                ((User)httpRequest.getSession().getAttribute("user")).getType() != "employee"){
            httpResponse.sendRedirect("admin-login.html");
        }
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isNotAllowedWithoutStaffLogin(String requestURI){
        return staffURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI){
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    @Override
    public void destroy() {
        //ignored
    }
}
