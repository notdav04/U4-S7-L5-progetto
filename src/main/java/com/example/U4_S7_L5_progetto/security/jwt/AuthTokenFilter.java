package com.example.U4_S7_L5_progetto.security.jwt;

import com.example.U4_S7_L5_progetto.security.services.UserDetailsServiceImpl;
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

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils utils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    private String analizzaJwt(HttpServletRequest request) {
        String headAutenticazione = request.getHeader("Authorization");

        if (StringUtils.hasText(headAutenticazione) && (headAutenticazione.startsWith("Bearer "))) {
            return headAutenticazione.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = analizzaJwt(request);

            if (jwt != null && utils.validazioneJwtToken(jwt)) {
                String username = utils.recuperoUsernameDaToken(jwt);
                UserDetails dettagliUtente = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken autenticazione =
                        new UsernamePasswordAuthenticationToken(
                                dettagliUtente,
                                null,
                                dettagliUtente.getAuthorities());

                autenticazione.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(autenticazione);
            }
        } catch (Exception e) {
            System.out.println("Errore durante l'autenticazione dell'utente: {}"+ e.getMessage());
        }

        filterChain.doFilter(request, response); // Continua la catena di filtri
    }
}
