package me.chawon.kakaopay.spread.dao;

import me.chawon.kakaopay.spread.domain.PickupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupRepository extends JpaRepository<PickupEntity, Long> {
}
