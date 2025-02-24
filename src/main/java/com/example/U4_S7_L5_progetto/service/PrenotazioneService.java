package com.example.U4_S7_L5_progetto.service;

import com.example.U4_S7_L5_progetto.model.Evento;
import com.example.U4_S7_L5_progetto.model.Prenotazione;
import com.example.U4_S7_L5_progetto.model.Utente;
import com.example.U4_S7_L5_progetto.payload.EventoDTO;
import com.example.U4_S7_L5_progetto.payload.PrenotazioneDTO;
import com.example.U4_S7_L5_progetto.repository.EventoDAORepository;
import com.example.U4_S7_L5_progetto.repository.PrenotazioneDAORepository;
import com.example.U4_S7_L5_progetto.repository.UtenteDAORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PrenotazioneService {

    @Autowired
    PrenotazioneDAORepository prenotazioneDAO;

    @Autowired
    EventoDAORepository eventoDAO;

    @Autowired
    UtenteDAORepository utenteDAO;

    public String savePrenotazione(long idEvento, String username){
        Utente utente = utenteDAO.findByUsername(username).orElseThrow(()->new RuntimeException("utente non trovato"));
        Evento evento = eventoDAO.findById(idEvento).orElseThrow(()->new RuntimeException("evento non trovato"));

        if(evento.getNPostiDisponibili()==0){
            throw new RuntimeException("l evento non ha piu posti disponibili");
        }

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setUtente(utente);
        prenotazione.setEvento(evento);
        evento.diminuisciPosti();
        prenotazioneDAO.save(prenotazione);
        utente.addPrenotazione(prenotazione);
        evento.addPrenotazione(prenotazione);
        return"prenotazione effettuata correttamente";


    }

    public List<PrenotazioneDTO> getListaPrenotazioni(String username){
        Utente utente = utenteDAO.findByUsername(username).orElseThrow(()->new RuntimeException("utente non trovato"));
        List<Prenotazione> lista = utente.getListaPrenotazioni();

        List<PrenotazioneDTO> listaDTO = new ArrayList<>();
        lista.forEach(ele->{
            listaDTO.add(entity_dto(ele));
        });
        return listaDTO;
    }



    public PrenotazioneDTO entity_dto(Prenotazione prenotazione) {
        PrenotazioneDTO dto = new PrenotazioneDTO();
        dto.setEvento(prenotazione.getEvento());
        dto.setIdUtente(prenotazione.getUtente().getId());
        return dto;
    }

    public EventoDTO Eventoentity_dto(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setData(evento.getData());
        dto.setDescrizione(dto.getDescrizione());
        dto.setLuogo(evento.getLuogo());
        dto.setTitolo(evento.getTitolo());
        dto.setNPostiDisponibili(evento.getNPostiDisponibili());

        return dto;
    }

    public String removePrenotazione(long idPrenotazione, String username){
        Utente utente = utenteDAO.findByUsername(username).orElseThrow(()->new RuntimeException("utente non trovato"));

        Prenotazione prenotazione  = prenotazioneDAO.findById(idPrenotazione).orElseThrow(()->new RuntimeException("prenotazione non trovate!"));
        if (prenotazione.getUtente().getId() == utente.getId()){
            prenotazioneDAO.deleteById(idPrenotazione);
            return"prenotazione cancellata con successo";
        }else{
            return"non puoi cancellare le prenotazioni di altri utenti!";
        }


    }
}
