package com.shop.controller;

import com.shop.config.auth.PrincipalDetails;
import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.bcel.classfile.EnumElementValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 담기
     * @param cartItemDto
     * @param bindingResult
     * @param principalDetails
     * @return
     */
    @PostMapping(value = "/cart")
    @ResponseBody
    public ResponseEntity<?> order(@RequestBody  CartItemDto cartItemDto , BindingResult bindingResult,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {

        log.info("장바구니 담기 : {} ", cartItemDto.toString());
        if(bindingResult.hasErrors()){
            StringBuilder sb=new StringBuilder();
            List<FieldError> filedErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : filedErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return  new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }


        String email=principalDetails.getEmail();
        Long cartItemId=null;
        try{
            cartItemId=cartService.addCart(cartItemDto, email);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getCause());
        }
        return ResponseEntity.ok().body(cartItemId);
    }


    /**
     * 장바구니 목록 보기
     * @param principalDetails
     * @param model
     * @return
     */
    @GetMapping(value = "/cart")
    public String orderHist( @AuthenticationPrincipal PrincipalDetails principalDetails, Model model){
        List<CartDetailDto> cartList = cartService.getCartList(principalDetails.getEmail());
        model.addAttribute("cartItems", cartList);
        return "cart/cartList";
    }


    /**
     * 장바구니 수량 업데이트
     * @param cartItemId
     * @param count
     * @param principalDetails
     * @return
     */
    @PatchMapping(value = "/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(
        @PathVariable("cartItemId") Long cartItemId, @RequestParam(value = "count") int count,
        @AuthenticationPrincipal  PrincipalDetails principalDetails){

        log.info("============ 장바구니 담기   {} ", count);

        if(count <=0){
            return ResponseEntity.ok("최소 1개 이상 담아주세요.");
        }else if(!cartService.validateCartItem(cartItemId, principalDetails.getEmail())){
            return ResponseEntity.badRequest().body("수정 권한이 없습니다.");
        }

        cartService.updateCartItemCount(cartItemId, count);

        return ResponseEntity.ok(cartItemId);
    }


    /**
     * 장바구니 삭제
     */
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                            @AuthenticationPrincipal PrincipalDetails principalDetails){
        if(!cartService.validateCartItem(cartItemId, principalDetails.getEmail())){
            return ResponseEntity.badRequest().body("수정 권한이 없습니다.");
        }

        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(cartItemId);
    }


    /**
     * 장바구니에서  -->주문하기
     * @param cartOrderDto
     * @param principalDetails
     * @return
     */
    @PostMapping(value = "/cart/orders")
    public ResponseEntity<?> orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.isEmpty()){
            return ResponseEntity.badRequest().body("주문한 상품을 선택해 주세요.");
        }

        for(CartOrderDto cartOrder : cartOrderDtoList){
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principalDetails.getEmail()) ){
                 return ResponseEntity.badRequest().body("주문 권한이 없습니다.");
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principalDetails.getEmail());
        return ResponseEntity.ok(orderId);
    }


    


}






