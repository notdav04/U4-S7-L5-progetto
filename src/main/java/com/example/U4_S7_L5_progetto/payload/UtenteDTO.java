package com.example.U4_S7_L5_progetto.payload;


import com.example.U4_S7_L5_progetto.model.Ruolo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;



@Data
public class UtenteDTO {

    private String nome;

    private String cognome;

    @NotBlank(message = " il campo email è obbligatorio")
    @Email(message = "indirizzo mail non valido")
    private String email;

    @NotBlank(message = " il campo username è obbligatorio")
    private String username;

    @NotBlank(message = " il campo password è obbligatorio")
    private String password;





}
