package com.example.U4_S7_L5_progetto.security;

import com.example.U4_S7_L5_progetto.security.jwt.AuthEnrtyPoint;
import com.example.U4_S7_L5_progetto.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl detailsImpl;

    @Autowired
    AuthEnrtyPoint gestoreNOAuthorization;


    // Spring crea in automatico un oggetto Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // FORNISCE L'AUTENTICAZIONE ATTRAVERSO I DETTAGLI USERNAME E PASSWORD
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Gestione di come deve essere creato e inizializzato l'oggetto DaoAuthenticationProvider
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();

        // L'oggetto importa tutti i dettagli utente
        auth.setUserDetailsService(detailsImpl);

        // DaoAuthenticationProvider fornisce un metodo per accettare la password criptata.
        auth.setPasswordEncoder(passwordEncoder());

        return auth;
    }


    // Gestora csrf
    // Andiamo ad impostare il nostro gestore delle autorizzazioni KO
    // Gestione della sessione
    // Gestione autorizzazioni sulle richieste (a livello controller / a livello di singoli servizi controller)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(gestoreNOAuthorization))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/utente/**").permitAll() // Endpoint pubblici (registrazione/login)
                                .requestMatchers("/eventi/**").hasAuthority("ROLE_ORGANIZZATORE") // Solo organizzatori possono gestire eventi
                                .requestMatchers("/prenotazioni/**").hasAuthority("ROLE_USER")// Solo utenti possono prenotare
                                .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }
}
