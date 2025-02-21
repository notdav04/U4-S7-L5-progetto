package com.example.U4_S7_L5_progetto.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idruolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Eruolo nome;


}
