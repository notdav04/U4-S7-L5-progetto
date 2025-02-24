package com.example.U4_S7_L5_progetto.payload;

import com.example.U4_S7_L5_progetto.model.Utente;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class EventoDTO {


    @NotBlank(message = "il campo titolo è obbligatorio")
    private String titolo;

    @NotBlank(message = "il campo descrizione è obbligatorio")
    private String descrizione;

    @NotNull(message = "la data è obbligatoria")
    private LocalDate data;

    @NotBlank(message = "il campo luogo è obbligatorio")
    private String luogo;

    @NotNull(message = "il campo numero posti disponibili è obbligatorio")
    @JsonProperty("nPostiDisponibili")
    private Integer nPostiDisponibili;


    private Long idOrg;
}
