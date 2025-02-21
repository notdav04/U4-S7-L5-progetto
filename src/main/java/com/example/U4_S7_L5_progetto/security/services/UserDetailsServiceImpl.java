package com.example.U4_S7_L5_progetto.security.services;

import com.example.U4_S7_L5_progetto.model.Utente;
import com.example.U4_S7_L5_progetto.repository.UtenteDAORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UtenteDAORepository utenteDAO;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Utente> utente = utenteDAO.findByUsername(username);
        Utente user = utente.orElseThrow();
        return UserDetailsImpl.costruisciDettagli(user);
    }
}
