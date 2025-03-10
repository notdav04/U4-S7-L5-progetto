package com.example.U4_S7_L5_progetto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "utenti")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;

    private String nome;

    private String cognome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)

    private String password;

    //per organizzatori
    @OneToMany

    private List<Evento> listaEventi ;

    //per utenti semplici
    @OneToMany(cascade = CascadeType.ALL)
    private List<Prenotazione> listaPrenotazioni;

    @ManyToOne
    private Ruolo ruolo ;

    public void addPrenotazione(Prenotazione p){
        this.listaPrenotazioni.add(p);
    }

    public void addEvento(Evento e){
        this.listaEventi.add(e);
    }


}
