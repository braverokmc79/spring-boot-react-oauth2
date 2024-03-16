package com.shop.entity;


import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import com.shop.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String authProvider; //이후 OAuth 에서 사용할 유저 정보 제공자 : github

    private String authProviderId;


    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setUsername(memberFormDto.getUsername());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);
        //member.setRole(Role.ADMIN);
        return member;
    }


    public static Member createMember(MemberFormDto memberFormDto) {

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setUsername(memberFormDto.getUsername());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        member.setPassword(memberFormDto.getPassword());
        member.setRole(Role.USER);

        return member;
    }


}





