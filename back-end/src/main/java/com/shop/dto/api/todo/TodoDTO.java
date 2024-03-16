package com.shop.dto.api.todo;

import com.shop.entity.Member;
import com.shop.entity.api.todo.TodoEntity;
import lombok.*;
import org.modelmapper.ModelMapper;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class TodoDTO {

    private Long todoId;
    private String title;
    private long memberId;
    private String message;
    private boolean done;



    public TodoDTO(final TodoEntity entity) {
       // this.id = entity.getId();
        this.title = entity.getTitle();
        this.done = entity.isDone();
    }

    public TodoDTO(Long todoId, String title, long memberId, boolean done) {
        this.todoId = todoId;
        this.title =title;
        this.memberId =memberId;
        this.done =done;
    }

    private static ModelMapper mapper = new ModelMapper();

    public static TodoEntity createEntity(final TodoDTO todoDTO) {
        return mapper.map(todoDTO, TodoEntity.class);
    }


    public static TodoEntity toEntity(final TodoDTO dto, Member member) {
       return TodoEntity.builder()
               .id(dto.getTodoId())
               .title(dto.getTitle())
               .done(dto.isDone())
               .member(member)
               .build();
    }



}
