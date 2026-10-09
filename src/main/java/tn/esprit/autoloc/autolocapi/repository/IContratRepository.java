package tn.esprit.autoloc.autolocapi.repository;


import tn.esprit.autoloc.autolocapi.domain.Contrat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
}