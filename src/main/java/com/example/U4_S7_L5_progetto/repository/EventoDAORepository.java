package com.example.U4_S7_L5_progetto.repository;

import com.example.U4_S7_L5_progetto.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoDAORepository extends JpaRepository<Evento, Long> {
}
