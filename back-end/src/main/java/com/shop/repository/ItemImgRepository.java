package com.shop.repository;

import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.custom.ItemRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> , QuerydslPredicateExecutor<Item>{

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);



    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);



}
