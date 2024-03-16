package com.shop.service.api.todo;

import com.shop.dto.api.todo.TodoDTO;
import com.shop.entity.api.todo.TodoEntity;
import com.shop.repository.api.todo.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class TodoService {

    private final TodoRepository repository;


    public  List<TodoDTO>  testService(TodoEntity todo){
        repository.save(todo);
        return repository.findByMemberId(todo.getMember().getId());
    }

    /**
     * 생성
     * @param entity
     * @return
     */
    public List<TodoDTO> create(final TodoEntity entity){
        validate(entity);
        repository.save(entity);

        log.info("=========================> Entity id : {} is saved ", entity.getId());
        return repository.findByMemberId(entity.getMember().getId());
    }


    /**
     * 유효성 검사
     * @param entity
     */
    private void validate(final TodoEntity entity){
        if(entity ==null){
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if(entity.getMember()==null){
            log.warn("Unknown user..");
            throw new RuntimeException("Unknown user.");
        }
    }


    /**
     * 검색 목록 출력
     * @param id
     * @return
     */
    public List<TodoDTO> retrieve(final long id){
        return repository.findByMemberId(id);
    }


    /**
     * 업데이트 구현
     * @param entity
     * @return
     */
    public List<TodoDTO> update(final TodoEntity entity){
        //1.유효성 검사
        validate(entity);

        //2.기존 값을 가져온다.
        Optional<TodoEntity> original = repository.findById(entity.getId());

        //3.더티 체킹 처리한다.
        original.ifPresent(todo->{
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
            //repository.save(todo);
        });


        return retrieve(entity.getMember().getId());
    }


    /**
     * 삭제
     * @param entity
     * @return
     */
    public List<TodoDTO> delete(final TodoEntity entity){
        validate(entity);

        try{
            repository.delete(entity);
        }catch(Exception e){
            throw new RuntimeException("===== 삭제 오류  "+e.getMessage());
        }
        return retrieve(entity.getMember().getId());
    }
    
    
    


}

