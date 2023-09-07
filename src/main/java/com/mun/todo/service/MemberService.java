package com.mun.todo.service;

import com.mun.todo.controller.dto.MemberResponseDto;
import com.mun.todo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponseDto findMemberInfoById(Long id) {
        return memberRepository.findById(id)
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다.")); // TODO : 커스텀 익셉션으로 변경
    }

    public MemberResponseDto findMemberInfoByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponseDto::of) // MemberResponse.of(Member)를 수행한 결과를 리턴함
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다.")); // TODO : 커스텀 익셉션으로 변경
    }
}
