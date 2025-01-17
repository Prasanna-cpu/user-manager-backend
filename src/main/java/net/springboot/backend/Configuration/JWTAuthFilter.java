package net.springboot.backend.Configuration;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.springboot.backend.Service.JWTUtils;
import net.springboot.backend.Service.OurUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    private final OurUserDetailsService ourUserDetailsService;


    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader=request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if(authHeader==null || authHeader.isBlank()){
            filterChain.doFilter(request,response);
            return;
        }

        jwtToken=authHeader.substring(7);
        userEmail=jwtUtils.extractUsername(jwtToken);

        if(userEmail!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=ourUserDetailsService.loadUserByUsername(userEmail);
            if(jwtUtils.ValidateToken(jwtToken,userDetails)){
                SecurityContext securityContext=SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);


            }
        }
        filterChain.doFilter(request,response);
    }
}
