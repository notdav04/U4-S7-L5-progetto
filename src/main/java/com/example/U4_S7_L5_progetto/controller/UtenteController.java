package com.example.U4_S7_L5_progetto.controller;

import com.example.U4_S7_L5_progetto.exception.EmailDuplicateException;
import com.example.U4_S7_L5_progetto.exception.UsernameDuplicateException;
import com.example.U4_S7_L5_progetto.model.Utente;
import com.example.U4_S7_L5_progetto.payload.EventoDTO;
import com.example.U4_S7_L5_progetto.payload.PrenotazioneDTO;
import com.example.U4_S7_L5_progetto.payload.UtenteDTO;
import com.example.U4_S7_L5_progetto.payload.request.LoginRequest;
import com.example.U4_S7_L5_progetto.payload.request.RegistrazioneRequest;
import com.example.U4_S7_L5_progetto.payload.response.JwtResponse;
import com.example.U4_S7_L5_progetto.repository.UtenteDAORepository;
import com.example.U4_S7_L5_progetto.security.jwt.JwtUtils;
import com.example.U4_S7_L5_progetto.security.services.UserDetailsImpl;
import com.example.U4_S7_L5_progetto.service.EventoService;
import com.example.U4_S7_L5_progetto.service.PrenotazioneService;
import com.example.U4_S7_L5_progetto.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutput;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/utente")
public class UtenteController {

    @Autowired
    UtenteService utenteService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EventoService eventoService;
    @Autowired
    PrenotazioneService prenotazioneService;

    @Autowired
    UtenteDAORepository utenteDAO;


    @PostMapping("/new")
    public ResponseEntity<String> registrazioneUtente(@Validated @RequestBody RegistrazioneRequest nuovoUtente, BindingResult validazione) {

        try {

            if (validazione.hasErrors()) {
                StringBuilder errori = new StringBuilder("Problemi nella validazione dati :\n");

                for (ObjectError errore : validazione.getAllErrors()) {
                    errori.append(errore.getDefaultMessage()).append("\n");
                }
                return new ResponseEntity<>(errori.toString(), HttpStatus.BAD_REQUEST);
            }

            String messaggio = utenteService.insertUtente(nuovoUtente);
            return new ResponseEntity<>(messaggio, HttpStatus.OK);
        } catch (UsernameDuplicateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EmailDuplicateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginDto, BindingResult validazione) {
        if (validazione.hasErrors()) {
            StringBuilder errori = new StringBuilder("Problemi nella validazione dati :\n");
            for (ObjectError errore : validazione.getAllErrors()) {
                errori.append(errore.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(errori.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication autenticazione = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(autenticazione);
            String token = jwtUtils.creaJwtToken(autenticazione);

            UserDetailsImpl dettagliUtente = (UserDetailsImpl) autenticazione.getPrincipal();
            List<String> ruoliweb = dettagliUtente.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            JwtResponse responseJWT = new JwtResponse(
                    dettagliUtente.getUsername(),
                    dettagliUtente.getId(),
                    dettagliUtente.getEmail(),
                    ruoliweb,
                    token
            );
            System.out.println(responseJWT.toString());

            return new ResponseEntity<>(responseJWT, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Credenziali non valide", HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("evento/new")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZZATORE')")
    public ResponseEntity<?> creaEvento(@RequestBody @Validated EventoDTO eventoDTO, BindingResult validation, Authentication authentication){
        if (validation.hasErrors()) {
            StringBuilder errori = new StringBuilder("Problemi nella validazione dati :\n");

            for (ObjectError errore : validation.getAllErrors()) {
                errori.append(errore.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(errori.toString(), HttpStatus.BAD_REQUEST);
        }
        String username = authentication.getName();
        Utente org = utenteDAO.findByUsername(username).orElseThrow(()->new RuntimeException("id organizzatore nopn trovato!"));
        eventoDTO.setIdOrg(org.getId());

        String messaggio = eventoService.saveEvento(eventoDTO);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }


    @PostMapping("/prenotazione/{idEvento}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> creaPrenotazione(@PathVariable long idEvento, Authentication authentication){
        try {
            String username = authentication.getName();
            String messaggio = prenotazioneService.savePrenotazione(idEvento, username);
            return new ResponseEntity<>( messaggio, HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/prenotazione/all")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> getPrenotazioni(Authentication authentication){
        try {
            String username = authentication.getName();
            List<PrenotazioneDTO> listaDTO = prenotazioneService.getListaPrenotazioni( username);
            return new ResponseEntity<>( listaDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/evento/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZZATORE')")

    public ResponseEntity<?> getEventi(Authentication authentication){
        try{
            String username = authentication.getName();
            List<EventoDTO> listaDTO = eventoService.getEventi(username);
            return new ResponseEntity<>(listaDTO, HttpStatus.OK);
        }catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/prenotazione/{idPrenotazione}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<?> deletePrenotazione(@PathVariable long idPrenotazione, Authentication authentication){
        try{
            String username = authentication.getName();
            String message = prenotazioneService.removePrenotazione(idPrenotazione, username);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch(RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/evento/{idEvento}")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZZATORE')")
    public ResponseEntity<?> updateEvento (@Validated @RequestBody EventoDTO eventoDTO, @PathVariable long idEvento, BindingResult validazione) {
        if(validazione.hasErrors()){
            StringBuilder errori = new StringBuilder("Problemi nella validazione dati :\n");

            for(ObjectError errore : validazione.getAllErrors()){
                errori.append(errore.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(errori.toString(), HttpStatus.BAD_REQUEST);
        }

        String messaggio = eventoService.updateEvento(eventoDTO, idEvento);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

    @DeleteMapping("/evento/{idEvento}")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZZATORE')")
    public ResponseEntity<?> deleteEvento( @PathVariable long idEvento, Authentication authentication) {
        String username = authentication.getName();
        String messaggio = eventoService.deleteEvento(idEvento, username);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

}

