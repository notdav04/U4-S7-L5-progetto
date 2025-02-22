package com.example.U4_S7_L5_progetto.controller;

import com.example.U4_S7_L5_progetto.exception.EmailDuplicateException;
import com.example.U4_S7_L5_progetto.exception.UsernameDuplicateException;
import com.example.U4_S7_L5_progetto.payload.UtenteDTO;
import com.example.U4_S7_L5_progetto.payload.request.LoginRequest;
import com.example.U4_S7_L5_progetto.payload.request.RegistrazioneRequest;
import com.example.U4_S7_L5_progetto.payload.response.JwtResponse;
import com.example.U4_S7_L5_progetto.security.jwt.JwtUtils;
import com.example.U4_S7_L5_progetto.security.services.UserDetailsImpl;
import com.example.U4_S7_L5_progetto.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/new")
    public ResponseEntity<String> registrazioneUtente(@Validated @RequestBody RegistrazioneRequest nuovoUtente, BindingResult validazione){

        try {

            if(validazione.hasErrors()){
                StringBuilder errori = new StringBuilder("Problemi nella validazione dati :\n");

                for(ObjectError errore : validazione.getAllErrors()){
                    errori.append(errore.getDefaultMessage()).append("\n");
                }
                return new ResponseEntity<>(errori.toString(), HttpStatus.BAD_REQUEST);
            }

            String messaggio =utenteService.insertUtente(nuovoUtente);
            return new ResponseEntity<>(messaggio, HttpStatus.OK);
        } catch (UsernameDuplicateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EmailDuplicateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginDto, BindingResult validazione){

        // VALIDAZIONE
        if(validazione.hasErrors()){
            StringBuilder errori = new StringBuilder("Problemi nella validazione dati :\n");

            for(ObjectError errore : validazione.getAllErrors()){
                errori.append(errore.getDefaultMessage()).append("\n");
            }

            return new ResponseEntity<>(errori.toString(), HttpStatus.BAD_REQUEST);

        }

        //Generiamo un oggetto che occorre per l'autenticazione
        UsernamePasswordAuthenticationToken tokenNoAuth = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // Invocare e a recuperare l'authentication -> autenticazione va a buon fine
        // Utilizziamo il gestore delle autenticazioni che si basa su Useername e Password
        // Recuperiamo l'autenticazione attraverso il metodo authenticate
        Authentication autenticazione = authenticationManager.authenticate(tokenNoAuth);

        // Impostare l'autenticazione nel contesto di sicurezza Spring
        SecurityContextHolder.getContext().setAuthentication(autenticazione);

        // Generiamo il TOKEN FINALE (String)
        String token = jwtUtils.creaJwtToken(autenticazione);

        // Recuperando le info che vogliamo inserire nella risposta al client
        UserDetailsImpl dettagliUtente = (UserDetailsImpl) autenticazione.getPrincipal();
        List<String> ruoliweb = dettagliUtente.getAuthorities().stream()
                .map((item->item.getAuthority()))
                .collect(Collectors.toList());

        // Creare un oggetto JWTresponse
        JwtResponse responseJWT = new JwtResponse(dettagliUtente.getUsername(), dettagliUtente.getId(),dettagliUtente.getEmail() , ruoliweb, token);

        // Gestione della risposta al Client -> ResponseEntity
        return new ResponseEntity<>(responseJWT, HttpStatus.OK);

    }
}
