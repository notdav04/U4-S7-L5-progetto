package com.example.U4_S7_L5_progetto.payload;

import com.example.U4_S7_L5_progetto.model.Evento;
import com.example.U4_S7_L5_progetto.model.Utente;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PrenotazioneDTO {
    @NotBlank(message = "il campo id utente è obbligatorio")
    private long idUtente;
    @NotBlank(message = "il campo evento è obbligatorio")
    private Evento evento;

}
