package com.bjbs.haji.business.repositories.haji;

import com.bjbs.haji.business.apis.dtos.ChannelDTO;
import com.bjbs.haji.business.models.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
