package com.example.U4_S7_L5_progetto.model;


import com.example.U4_S7_L5_progetto.payload.PrenotazioneDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "eventi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String titolo;

    @Column(nullable = false)
    private String descrizione;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private String luogo;

    @Column(nullable = false)
    private int nPostiDisponibili;

    @ManyToOne
    @JoinColumn(name= "utente_id")
    @JsonIgnore
    private Utente creatoreEvento;

    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "prenotazione_id")
    @JsonIgnore
    private List<Prenotazione> listaPrenotazioni;


    public void diminuisciPosti(){
        nPostiDisponibili--;
    }

    public void addPrenotazione(Prenotazione p){
        listaPrenotazioni.add(p);
    }
}
