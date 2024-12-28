package com.pmk.notif.repositories.pubsubs;


import com.pmk.notif.models.pubsubs.MasterReversalNotif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterReversalNotifRepository extends JpaRepository<MasterReversalNotif, Long> {



}
