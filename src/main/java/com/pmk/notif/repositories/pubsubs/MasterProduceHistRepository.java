package com.pmk.notif.repositories.pubsubs;

import com.pmk.notif.models.pubsubs.MasterProduceHist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterProduceHistRepository extends JpaRepository<MasterProduceHist, Long> {
}
