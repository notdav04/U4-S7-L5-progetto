package com.example.U4_S7_L5_progetto.service;

import com.example.U4_S7_L5_progetto.model.Utente;
import com.example.U4_S7_L5_progetto.payload.UtenteDTO;
import com.example.U4_S7_L5_progetto.repository.UtenteDAORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UtenteService {

    @Autowired
    UtenteDAORepository utenteDAO;



    //salva nuovo utente (registrazione)
    public String insertUtente(UtenteDTO user){
        Utente utente = dto_entity(user);
        long id  = utenteDAO.save(utente).getId();
        return "utente aggiunto correttamente con id: " + id;
    }



    //travasi
    public Utente dto_entity(UtenteDTO dto){
        Utente user = new Utente();
        user.setEmail(dto.getEmail());
        user.setCognome(dto.getCognome());
        user.setNome(dto.getNome());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    public UtenteDTO entity_dto(Utente utente){
        UtenteDTO dto = new UtenteDTO();
        dto.setEmail(utente.getEmail());
        dto.setCognome(utente.getCognome());
        dto.setNome(utente.getNome());
        dto.setUsername(utente.getUsername());

        return dto;
    }
}
