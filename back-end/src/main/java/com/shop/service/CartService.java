package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email) {
        //1.장바구니에 담을 상품 엔티티를 조회합니다.
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityExistsException::new);
        //2.현재 로그인한 회원 엔티티를 조회합니다.
        Member member = memberRepository.findByEmail(email);


        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){ //상품을 처음으로 장바구니에 담을 경우 해당 회원의 엔티티를 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        //장바구니 아이템 확인
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if(savedCartItem!=null){
            //장바구니에 이미 있던 상품일 경우 기존 수량에 현재 장바구니에 담을 수량 만큼을 더해 준다.
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else{
            //장바구니 엔티티, 상품 엔티티, 장바구니에 담을 수량을 이용하여 CartItem 엔티티를 생성
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }

    }


    /**
     * 장바구니 목록
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList =new ArrayList<>();
        
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());


        if(cart==null){
            return cartDetailDtoList;
        }

        log.info("===== 장바구니 번호  {}", cart.getId());

        cartDetailDtoList = cartItemRepository.findCartDetailDtolist(cart.getId());
        
        return cartDetailDtoList;
    }


    /**
     * 장바구니 넣은 아이디와 동일한지 확인
     * @param cartItemId
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityExistsException::new);

        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }


    /**
     * 장바구니 상품의 수량을 업데이트하는 메소드
     * @param cartItemId
     * @param count
     */
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityExistsException::new);
        cartItem.updateCount(count);
    }


    /**
     * 장바구니 삭제
     */
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityExistsException::new);
        cartItemRepository.delete(cartItem);
    }





    //장바구니 목록에서 --> 주문하기
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<OrderDto>();

        for(CartOrderDto cartOrderDto :cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityExistsException::new);

            OrderDto orderDto=new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        //2.장바구니에 담은 상품을 주문하도록 주문 로직 구현
        Long orderId = orderService.orders(orderDtoList, email);


        //3.주문한 상품들을 장바구니에 제거
        for(CartOrderDto cartOrderDto :cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityExistsException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }





    

}





