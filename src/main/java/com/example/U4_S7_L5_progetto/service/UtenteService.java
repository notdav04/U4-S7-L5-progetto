package com.example.U4_S7_L5_progetto.service;

import com.example.U4_S7_L5_progetto.exception.EmailDuplicateException;
import com.example.U4_S7_L5_progetto.exception.UsernameDuplicateException;
import com.example.U4_S7_L5_progetto.model.Eruolo;
import com.example.U4_S7_L5_progetto.model.Ruolo;
import com.example.U4_S7_L5_progetto.model.Utente;
import com.example.U4_S7_L5_progetto.payload.UtenteDTO;
import com.example.U4_S7_L5_progetto.payload.request.RegistrazioneRequest;
import com.example.U4_S7_L5_progetto.repository.RuoloDAORepository;
import com.example.U4_S7_L5_progetto.repository.UtenteDAORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class UtenteService {

    @Autowired
    UtenteDAORepository utenteDAO;
    @Autowired
    RuoloDAORepository ruoloDAO;

    @Autowired
    PasswordEncoder passEncoder;


    //salva nuovo utente (registrazione)
    public String insertUtente(RegistrazioneRequest user) throws UsernameDuplicateException, EmailDuplicateException {
        checkDuplicateKey(user.getUsername(), user.getEmail());
        Utente utente = registrazioneRequest_Utente(user);
        long id = utenteDAO.save(utente).getId();
        return "utente aggiunto correttamente con id: " + id;
    }


    //controllo le chiavi duplicate per username e email
    public void checkDuplicateKey(String username, String email) throws UsernameDuplicateException, EmailDuplicateException {
        if (utenteDAO.existsByUsername(username)) {
            throw new UsernameDuplicateException("username gia presente");
        }
        if (utenteDAO.existsByEmail(email)) {
            throw new EmailDuplicateException("email gia presente");
        }
    }


    //travasi
    public Utente dto_entity(UtenteDTO dto) {
        Utente user = new Utente();
        user.setEmail(dto.getEmail());
        user.setCognome(dto.getCognome());
        user.setNome(dto.getNome());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    public UtenteDTO entity_dto(Utente utente) {
        UtenteDTO dto = new UtenteDTO();
        dto.setEmail(utente.getEmail());
        dto.setCognome(utente.getCognome());
        dto.setNome(utente.getNome());
        dto.setUsername(utente.getUsername());

        return dto;
    }

    public Utente registrazioneRequest_Utente(RegistrazioneRequest request) {

        Utente utente = new Utente();
        utente.setEmail(request.getEmail());
        utente.setNome(request.getNome());
        utente.setUsername(request.getUsername());
        utente.setCognome(request.getCognome());
        String codificaPassword = passEncoder.encode(request.getPassword()); //codifica della password(prima non stavo facendo)
        utente.setPassword(codificaPassword);


        if (request.getRuolo() == null) {
            Ruolo defaultRole = ruoloDAO.findByNome(Eruolo.ROLE_USER).orElseThrow(() -> new RuntimeException("ruolo non trovato!"));
            utente.setRuolo(defaultRole);

        } else if (request.getRuolo().equals(Eruolo.ROLE_ORGANIZZATORE.name())) {
            Optional<Ruolo> orgRole = ruoloDAO.findByNome(Eruolo.ROLE_ORGANIZZATORE);
            orgRole.orElseThrow(() -> new RuntimeException("ruolo non trovato!"));
            utente.setRuolo(orgRole.get());

        } else if (request.getRuolo().equals(Eruolo.ROLE_ADMIN.name())) {
            Optional<Ruolo> adminRole = ruoloDAO.findByNome(Eruolo.ROLE_ADMIN);
            adminRole.orElseThrow(() -> new RuntimeException("ruolo non trovato!"));
            utente.setRuolo(adminRole.get());

        } else {
            throw new RuntimeException("il ruolo specificato non è corretto!");
        }

        return utente;
    }
}

