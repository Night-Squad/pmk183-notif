package com.pmk.notif.repositories.pubsubs;

import com.pmk.notif.models.pubsubs.RefChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefChannelRepository extends JpaRepository<RefChannel, Long> {
}
