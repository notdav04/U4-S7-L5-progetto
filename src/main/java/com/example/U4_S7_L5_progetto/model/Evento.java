package com.example.U4_S7_L5_progetto.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private Utente creatoreEvento;
}
