package com.shop.entity.api.todo;

import com.shop.entity.Member;
import com.shop.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="todo")
@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class TodoEntity  extends BaseEntity {

    @Id
    @Column(name="todo_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //이 오브젝트의 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title; // Todo 타이츨 예) 운동 하기

    @Column(name="done")
    private boolean done;  //true - todo 를 완료한 경우 (checked)



}
