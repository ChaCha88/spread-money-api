package me.chawon.kakaopay.spread.dao;


import me.chawon.kakaopay.spread.domain.SpreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpreadRepository extends JpaRepository<SpreadEntity, Long> {

    Optional<SpreadEntity> findByToken(String token);
}
