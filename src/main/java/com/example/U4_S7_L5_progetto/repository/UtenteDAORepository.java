package com.example.U4_S7_L5_progetto.repository;

import com.example.U4_S7_L5_progetto.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteDAORepository extends JpaRepository<Utente, Long> {

    public Optional<Utente> findByUsername(String username);

    //controlli per login
    public boolean existsByUsernameAndPassword(String username, String password);

    // check duplicate key
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);

}
