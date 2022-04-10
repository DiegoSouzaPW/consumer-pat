package br.com.alelo.consumer.consumerpat.respository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.alelo.consumer.consumerpat.entity.Establishment;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, UUID> {
	
}
