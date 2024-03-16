package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;



    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList=new ArrayList<>();

        //1.주문할 상품 엔티니와 주문 수량을 이용하여 주문 상풒 엔티티를 생성합니다.
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        //2.회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성합니다.
        Order order = Order.createOrder(member, orderItemList);
        //3.생성한 주문 엔티티를 저장합니다.
        orderRepository.save(order);

        return order.getId();
    }



    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        //1.유저의 아이디와 페이징 조건을 이용하여 주문 목록을 조회합니다.
        List<Order> orders = orderRepository.findOrders(email, pageable);
        //2.유저의 주문 총 개수를 구합니다
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtoList=new ArrayList<>();

        for(Order order : orders){
            //3.주문 리스트를 순회 하면서 구매 이력 페이지에 전달할 DTO를 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();

            for(OrderItem orderItem : orderItems){
                //4.주문한 상품의 대표 이미지를 조회합니다.
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtoList.add(orderHistDto);
        }
        //5.페이지 구현 객체를 생성하여 반환합니다.
        return new PageImpl<OrderHistDto>(orderHistDtoList, pageable, totalCount);
    }


    /**
     * 1.현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사를 합니다.
     * 같을 때는 true 를 반환하고 같지 않을 경우 false 를 반환합니다.
     */


    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        Member saveMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), saveMember.getEmail()) ){
            return false;
        }

        return true;
    }


    /**
     * 주문 취소 상태로 변경하려면 변경 감지 기능에 의해서 트랜잭션이 끝날 때 update 쿼리가 실해
     */
    public void cancleOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }




    //장바구니 목록에서 주문으로 ==> 주문 목록 일괄 저장
    public Long orders(List<OrderDto> orderDtoList, String email){
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList=new ArrayList<>();

        for(OrderDto orderDto : orderDtoList){
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }


    





}
