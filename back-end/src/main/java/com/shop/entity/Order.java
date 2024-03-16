package com.shop.entity;

import com.shop.constant.OrderStatus;
import com.shop.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;  //주문상태

    @OneToMany(mappedBy = "order",fetch =FetchType.LAZY    , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems=new ArrayList<>();


    //연관 관계의 주인이 아닌 이상 - 항상 두개를 같이 코딩해 줘야 한다.
    //orderItems 에는 주문 상품 정보를 담아 준다.
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }


    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order =new Order();
        //1.상품을 주문한 회원의 정보를 세팅
        order.setMember(member);

        //2.상품 페이지에서는 1개의 상품을 주문하지만, 장바구니 페이지에서는 한 번에 여러 개의 상품을 주문할 수 있습니다.
        //따라서, 여러 개의 주문 상품을 담을 수 있도록 리스트형태로 파라미터 값을 받으며 주문 객체에 orderItem 객체를 추가 합니다.
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        //3.주문 상태를 order 로 세팅
        order.setOrderStatus(OrderStatus.ORDER);

        //4.현재 시간을 주문 시간으로 세팅
        order.setOrderDate(LocalDateTime.now());
        return order;
    }


    public int getTotalPrice(){
        int totalPrice=0;
        for(OrderItem orderItem : orderItems){
            totalPrice+=orderItem.getTotalPrice();
        }
        return totalPrice;
    }


    public void cancelOrder(){
        this.orderStatus =OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancle();
        }
    }





}