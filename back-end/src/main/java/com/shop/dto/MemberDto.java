package com.shop.dto;

import com.shop.constant.Role;
import com.shop.dto.api.jwt.TokenDto;
import com.shop.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private String id;
    private TokenDto token;
    private String username;
    private String name;
    private String email;
    private Role role;
    private String authProvider; //이후 OAuth 에서 사용할 유저 정보 제공자 : github
    private String authProviderId;

    private static ModelMapper mapper = new ModelMapper();

    public static MemberDto of(Member member){
        return mapper.map(member, MemberDto.class);
    }

    public static Member oauth2CreateMember(MemberDto memberDto){
        return mapper.map(memberDto, Member.class);
    }

}
