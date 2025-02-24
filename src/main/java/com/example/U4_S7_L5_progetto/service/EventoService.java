package com.example.U4_S7_L5_progetto.service;

import com.example.U4_S7_L5_progetto.model.Evento;
import com.example.U4_S7_L5_progetto.model.Utente;
import com.example.U4_S7_L5_progetto.payload.EventoDTO;
import com.example.U4_S7_L5_progetto.payload.UtenteDTO;
import com.example.U4_S7_L5_progetto.repository.EventoDAORepository;
import com.example.U4_S7_L5_progetto.repository.UtenteDAORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventoService {

    @Autowired
    EventoDAORepository eventoDAO;

    @Autowired
    UtenteDAORepository utenteDAO;


    public String saveEvento(EventoDTO eventoDTO) {

        Utente org = utenteDAO.findById(eventoDTO.getIdOrg()).orElseThrow(() -> new RuntimeException("id organizzatore non trovato!"));

        Evento evento = dto_entity(eventoDTO);
        evento.setCreatoreEvento(org);

        long eventoId = eventoDAO.save(evento).getId();
        return "evento salvato con id " + eventoId;
    }

    public String getEventi(String username){
        Utente utente = utenteDAO.findByUsername(username).orElseThrow(()->new RuntimeException("utente non trovato"));
        return "ok";
    }


    public Evento dto_entity(EventoDTO dto) {
        Evento evento = new Evento();
        evento.setData(dto.getData());
        evento.setLuogo(dto.getLuogo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setTitolo(dto.getTitolo());
        evento.setNPostiDisponibili(dto.getNPostiDisponibili());

        return evento;
    }

    public EventoDTO entity_dto(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setData(evento.getData());
        dto.setDescrizione(dto.getDescrizione());
        dto.setLuogo(evento.getLuogo());
        dto.setTitolo(evento.getTitolo());
        dto.setNPostiDisponibili(evento.getNPostiDisponibili());

        return dto;
    }
}
