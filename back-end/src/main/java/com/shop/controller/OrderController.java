package com.shop.controller;

import com.shop.config.auth.PrincipalDetails;
import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import com.shop.utils.PageMaker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;


    @PostMapping(value = "/order")
    public ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
                                   @AuthenticationPrincipal PrincipalDetails pricPrincipalDetails) {

        if(bindingResult.hasErrors()){
            StringBuilder sb =new StringBuilder();
            bindingResult.getAllErrors().forEach(e->{
                sb.append(e.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(sb.toString());
        }

        String email=pricPrincipalDetails.getEmail();
        Long orderId;
        try{
            orderId=orderService.order(orderDto, email);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }


    /**
     * 주문 인력 조회
     */
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, PageMaker pageMaker,
                              @AuthenticationPrincipal  PrincipalDetails principal, Model model){
        Integer pageInt = page.orElse(0);
        PageRequest pageable = PageRequest.of(pageInt, 4);

        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getEmail(), pageable);

        String pagination = pageMaker.pageObject(ordersHistDtoList, pageInt, 4, 5, "/orders/", "pathVariable");
        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("pagination", pagination);
        return "order/orderHist";
    }


    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") Long orderId,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails){
        log.info("  ");
        //1.자바스크립트에서 취소할 주문 번호는 조작이 가능하므로 다른 사람의 주문을 취고하지 못하도록 주문 취소 권한 검사를 합니다.
        if(!orderService.validateOrder(orderId, principalDetails.getEmail())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("주문 취소 권한이 없습니다.");
        }

        //2.주문 취고 로직을 호출합니다.
        orderService.cancleOrder(orderId);
        return ResponseEntity.ok().body(orderId);
    }







}
