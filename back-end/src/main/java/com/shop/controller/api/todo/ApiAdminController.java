package com.shop.controller.api.todo;


import com.shop.config.auth.PrincipalDetails;
import com.shop.dto.api.todo.ResDTO;
import com.shop.dto.api.todo.TodoDTO;
import com.shop.service.api.todo.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * 관리자만 접근가능
 *
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Log4j2
public class ApiAdminController {

    
    private final TodoService todoService;

    @GetMapping
   // @PreAuthorize("hasAnyRole('ADMIN')")
    //@PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal PrincipalDetails principalDetails){

        try{
            log.info("===>1.관리자 접근 가능 : role {} ", principalDetails.getId());
            Collection<? extends GrantedAuthority> authorities = principalDetails.getAuthorities();
            List<String> collect = authorities.stream().map(GrantedAuthority::getAuthority).toList();
            log.info("===>2.관리자 접근 가능 : authority {}", collect.toString());


            List<TodoDTO> todoDTOList=todoService.retrieve(principalDetails.getId());
            ResDTO<Object> response = ResDTO.builder().data(todoDTOList).build();
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResDTO.builder().code(-1).message("목록 출력 오류").errorCode(e.getMessage()).build());
        }
    }

}
