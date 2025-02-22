package com.example.U4_S7_L5_progetto.repository;

import com.example.U4_S7_L5_progetto.model.Eruolo;
import com.example.U4_S7_L5_progetto.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuoloDAORepository extends JpaRepository<Ruolo, Long> {
    Optional<Ruolo> findByNome(Eruolo nome);
}