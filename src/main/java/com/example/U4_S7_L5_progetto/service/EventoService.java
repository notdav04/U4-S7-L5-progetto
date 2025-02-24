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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        org.addEvento(evento);

        long eventoId = eventoDAO.save(evento).getId();
        return "evento salvato con id " + eventoId;
    }



    public List<EventoDTO> getEventi(String username){
        Utente utente = utenteDAO.findByUsername(username).orElseThrow(()->new RuntimeException("utente non trovato"));
        List<Evento> lista = utente.getListaEventi();
        List<EventoDTO> listaDTO = new ArrayList<>();
        lista.forEach(ele->listaDTO.add(entity_dto(ele)));
//        listaDTO.forEach(ele-> System.out.println(ele));
        return listaDTO;
    }

    public String updateEvento(EventoDTO eventoDTO, long idEvento) {
        Evento evento = eventoDAO.findById(idEvento).orElseThrow(()->new RuntimeException("evento non trovato!"));
            evento.setTitolo(eventoDTO.getTitolo());
            evento.setDescrizione(eventoDTO.getDescrizione());
            evento.setData(eventoDTO.getData());
            evento.setLuogo(eventoDTO.getLuogo());
            evento.setNPostiDisponibili(eventoDTO.getNPostiDisponibili());
            eventoDAO.save(evento);
        return "Evento aggiornato correttamente";
    }
    public String deleteEvento(long eventoId, String username) {
        Optional<Evento> eventoTrovato = eventoDAO.findById(eventoId);
        Optional<Utente> organizzTrovato = utenteDAO.findByUsername(username);
        if (eventoTrovato.isPresent() && organizzTrovato.isPresent()) {
            Evento evento = eventoTrovato.get();
            Utente org = organizzTrovato.get();

            if(evento.getCreatoreEvento().equals(org)) {
                eventoDAO.delete(evento);
                return "Evento rimosso con successo !";
            }else {
                throw new RuntimeException("non puoi cancellare un evento altrui!");
            }

        } else {
            throw new RuntimeException("evento non trovato!");
        }
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
