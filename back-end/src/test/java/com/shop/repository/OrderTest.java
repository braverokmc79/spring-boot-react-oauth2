package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;


    @Autowired
    OrderItemRepository orderItemRepository;


    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item=new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }


    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){
        Order order=new Order();
        for(int i=0; i<3; i++){
            Item item=this.createItem();
            itemRepository.save(item);
            OrderItem orderItem=new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        orderRepository.saveAndFlush(order);
        em.clear();
        Order saveOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        Assertions.assertEquals(3, saveOrder.getOrderItems().size());
    }




    //고아객체 삭제 확인
    //주문 엔티티에서 주문 상품을 삭제했을 때 orderItem 엔티티가 삭제되는지 테스트



    public Order createOrder(){
        Order order=new Order();

        for(int i=0; i<3; i++){
            Item item=createItem();
            itemRepository.save(item);
            OrderItem orderItem=new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);

        }

        Member member =new Member();
        memberRepository.save(member);

        order.setMember(member);

        orderRepository.save(order);
        return order;
    }

    /**
     Cascade  옵션 중 REMOVE 옵션과 헷갈릴 수 있습니다.
     Cascade REMOVE 옵션은 부모 엔티티가 살제될 때 연관된 자식 엔티티도 함께 삭제 됩니다.
     order 를 삭제하면 order 에 매핑되어 있던 orderItem 이 함께 삭제되는 것입니다.
     */

    @Test
    @DisplayName("고아객체 삭제 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        System.out.println("=============================remove===================================");
        order.getOrderItems().remove(1);
        System.out.println("=================================flush===============================");
        em.flush();
        System.out.println("=================================flush end===============================");
    }




    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        System.out.println("================1.Order class ====================>:");
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(EntityNotFoundException::new);

        System.out.println("================2.Order getOrderDate ====================>:");
        LocalDateTime orderDate = orderItem.getOrder().getOrderDate();

        System.out.println("================Order class ====================>: " + orderItem.getOrder().getClass());
    }






}

