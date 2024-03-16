package com.shop.repository.api.todo;

import com.shop.dto.api.todo.TodoDTO;
import com.shop.entity.api.todo.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {



    @Query("select new  com.shop.dto.api.todo.TodoDTO(t.id, t.title, m.id,  t.done) from TodoEntity t join t.member m where m.id = :id")
    List<TodoDTO> findByMemberId(@Param("id") long id);


}
