package com.shop.repository;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 카트 아이디와 상품 아이디를 이용해서 상품이 장바구니에 들어있는지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);



    @Query(" select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl)  " +
            " from CartItem  ci, ItemImg im " +
            " join ci.item i " +
            " where ci.cart.id = :cartId " +
            " and im.item.id =ci.item.id " +
            " and im.repImgYn = 'Y' " +
            " order by  ci.regTime desc "
    )
    List<CartDetailDto> findCartDetailDtolist(@Param("cartId") Long cartId);



}
