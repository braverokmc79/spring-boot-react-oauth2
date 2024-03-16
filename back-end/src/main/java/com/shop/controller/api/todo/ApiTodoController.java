package com.shop.controller.api.todo;

import com.shop.config.auth.PrincipalDetails;
import com.shop.dto.api.todo.ResDTO;
import com.shop.dto.api.todo.TodoDTO;
import com.shop.entity.Member;
import com.shop.entity.api.todo.TodoEntity;
import com.shop.service.MemberService;
import com.shop.service.api.todo.TodoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
@Log4j2
public class ApiTodoController {


    private final TodoService todoService;

    private final MemberService memberService;


    @PostMapping
    public ResponseEntity<?> createdTodo(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody TodoDTO todoDTO){
        try {

            log.info("===>1.createdTodo principalDetails : {}", principalDetails.getMember().getId());
            log.info("===>2.createdTodo TodoDTO :{}", principalDetails.getMember().getId());
            log.info("===>3.createdTodo getUsername : {}", principalDetails.getUsername());


            //회원정보 불러오기
            Member member = memberService.getMemberUsername(principalDetails.getUsername());
            TodoEntity entity = TodoDTO.toEntity(todoDTO, member);


            //4. 서비스를 이용해 Todo 엔티티를 생성한다.
            List<TodoDTO> todoDTOList=todoService.create(entity);

            //5.자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 반환
            //List<TodoDTO> dtos = entities.stream().map(TodoEntity::new).collect(Collectors.toList());

            ResDTO<Object> response = ResDTO.builder().data(todoDTOList).build();

            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            ResDTO<TodoDTO> response = ResDTO.<TodoDTO>builder().code(-1).errorCode(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }

    }


    /**   //@PreAuthorize("hasAnyAuthority('ADMIN')")
     * 목록 출력  - 검색 기능
     * @return
     */
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal PrincipalDetails principalDetails){
        try{
            log.info("===>1.목록 출력  - 검색 기능 아이디 :  {}", principalDetails.getId());

            List<TodoDTO> todoDTOList=todoService.retrieve(principalDetails.getId());
            ResDTO<Object> response = ResDTO.builder().data(todoDTOList).build();
            log.info("===>2.목록 출력  - 응답처리:  {}", response.getData().toString());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResDTO.builder().code(-1).message("목록 출력 오류").errorCode(e.getMessage()).build());
        }
    }





    /**
     * 수정 하기
     * @param dto
     * @return
     */
    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal PrincipalDetails principalDetails,@RequestBody TodoDTO dto){
        //회원정보 불러오기
        Member member = memberService.getMemberUsername(principalDetails.getUsername());
        TodoEntity entity = TodoDTO.toEntity(dto, member);

        List<TodoDTO> resDto = todoService.update(entity);
        return ResponseEntity.ok(ResDTO.builder().data(resDto).build());
    }


    /**
     * 삭제 하기
     * @param dto
     * @return
     */
    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal PrincipalDetails principalDetails ,@RequestBody TodoDTO dto){
        try {
            //회원정보 불러오기
            Member member = memberService.getMemberUsername(principalDetails.getUsername());
            TodoEntity entity = TodoDTO.toEntity(dto, member);

            List<TodoDTO> resDto = todoService.delete(entity);
            return ResponseEntity.ok(ResDTO.builder().data(resDto).build());
        }catch (Exception e){
            ResDTO<TodoDTO> response = ResDTO.<TodoDTO>builder().code(-1).errorCode(e.getMessage()).build();
            return ResponseEntity.badRequest().body(response);
        }
        
    }
    



}


