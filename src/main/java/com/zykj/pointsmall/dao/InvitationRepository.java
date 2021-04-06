package com.zykj.pointsmall.dao;

import com.zykj.pointsmall.pojo.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation,Integer> {
    Invitation findByAcceptPhone(String phone);

    List<Invitation> findAllByInviteCustomerId(String token);
}
