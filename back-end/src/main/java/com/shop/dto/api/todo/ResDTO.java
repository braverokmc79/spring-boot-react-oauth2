package com.shop.dto.api.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResDTO<T> {

    private int code; //1(성공), -1(실패)
    private String message;
    private String errorCode;
    private T data;

}
