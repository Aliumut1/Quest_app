package com.example.questapp.security;

import com.example.questapp.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException{
        System.out.println("Request URI: " + request.getRequestURI());
        try {
            String jwtToken = extractJwtFromRequest(request);
            System.out.println("Extracted JWT: " + jwtToken);
                    if(StringUtils.hasText(jwtToken) && jwtTokenProvider.validateToken(jwtToken)){
                        System.out.println("Token is valid.");
                        Long id = jwtTokenProvider.getUserIdFromJwt(jwtToken);
                        UserDetails user = userDetailsService.loadUserById(id);
                        if(user != null){
                            System.out.println("User found: " + user.getUsername());
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
        }catch(Exception e){
            System.err.println("Exception in JwtAuthenticationFilter: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request){//request alsın token dönsün
        String bearer = request.getHeader("Authorization");
        if(StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))
            return bearer.substring("Bearer".length() + 1);//baştaki bearerı atlıyor sadece tokenı dönüyor extract ettik.
        return null;
    };


}
