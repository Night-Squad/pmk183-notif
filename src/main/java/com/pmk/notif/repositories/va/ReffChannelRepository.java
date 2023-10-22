package com.pmk.notif.repositories.va;

import com.pmk.notif.models.va.ReffChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReffChannelRepository extends JpaRepository<ReffChannel, Short> {

    Optional<ReffChannel> findFirstByChannelCode(String channelCode);

}
