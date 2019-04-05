package com.gowtham.memberservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gowtham.memberservice.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("SELECT a FROM Member a WHERE a.firstName=:firstName ")
	Optional<Member> getMemberByName(String firstName);

}