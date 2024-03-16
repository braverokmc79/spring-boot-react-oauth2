package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Member findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Member findByUsernameAndPassword(String username, String password);

    Member findByEmailAndPassword(String email, String password);

    Boolean existsByAuthProviderId(String authProviderId);

    Member findMemberByAuthProviderId(String authProviderId);




}



