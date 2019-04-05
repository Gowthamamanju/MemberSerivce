package com.gowtham.memberservice.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gowtham.memberservice.entity.Member;
import com.gowtham.memberservice.exception.MemberBaseException;
import com.gowtham.memberservice.exception.MemberNotFoundException;
import com.gowtham.memberservice.repository.MemberRepository;

@RestController
public class MemberController extends CommonExceptionHandler {

	@Autowired
	private MemberRepository memberRepository;

	@GetMapping("/members")
	public List<Member> retrieveAllMembers() {
		return memberRepository.findAll();
	}

	@GetMapping("/member/id/{id}")
	public Member retrieveMemberById(@PathVariable Long id) throws MemberBaseException {
		Optional<Member> member = memberRepository.findById(id);
		if (!member.isPresent())
			throw new MemberNotFoundException("Member Id : " + id + " Does not exists");
		return member.get();
	}

	@GetMapping("/member/firstname/{firstName}")
	public Member retrieveMemberByFirstName(@PathVariable String firstName) throws MemberBaseException {
		Optional<Member> member = memberRepository.getMemberByName(firstName);
		if (!member.isPresent())
			throw new MemberNotFoundException("Member first name : " + firstName + " Does not exists");
		return member.get();
	}

	@DeleteMapping("/member/id/{id}")
	public void deleteMember(@PathVariable long id) {
		memberRepository.deleteById(id);
	}

	@PostMapping("/members")
	public ResponseEntity<Object> createMember(@RequestBody Member member) {
		Member addedMember = memberRepository.save(member);
		URI memberUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(addedMember.getId()).toUri();
		return ResponseEntity.created(memberUri).build();

	}

	@PutMapping("/members/id/{id}")
	public ResponseEntity<Object> updateMember(@RequestBody Member member, @PathVariable long id) {

		Optional<Member> studentOptional = memberRepository.findById(id);

		if (!studentOptional.isPresent())
			return ResponseEntity.notFound().build();

		member.setId(id);
		memberRepository.save(member);
		return ResponseEntity.noContent().build();
	}

}