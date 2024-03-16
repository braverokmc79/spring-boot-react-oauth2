package com.shop.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.shop.entity.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    //상품 상태 확인
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus==null?null: item.itemSellStatus.eq(searchSellStatus);
    }


    //상품 등록 - 세팅 후 최근 한들 동안 등록된 상품만 조회하도록 조건값을 반환합니다.
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime=LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType==null){
            return null;

        }else if(StringUtils.equals("1d", searchDateType)) {
            dateTime=dateTime.minusDays(1);

        }else if(StringUtils.equals("1w", searchDateType)) {
            dateTime=dateTime.minusWeeks(1);

        }else if(StringUtils.equals("1m", searchDateType)){
            dateTime=dateTime.minusMonths(1);

        }else if(StringUtils.equals("6m", searchDateType)){
            dateTime=dateTime.minusMonths(6);
        }
        return item.regTime.after(dateTime);
    }


    /**
     * searchBy 값에 따라서 상품명에 검색어를 포함하고 있는 
     * @param searchBy
     * @param searchQuery
     * @return
     */
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm", searchBy)){
            return item.itemNm.like("%"+searchQuery + "%");
        }else if(StringUtils.equals("createdBy", searchBy)){
            return item.createdBy.like("%"+searchQuery+"%");
        }
        
        return  null;         
    }
    


    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        JPAQuery<Long> countQuery = queryFactory.
                select(item.count())
                .from(item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                );

        List<Item> content = queryFactory.selectFrom(item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                ).orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    /**
     *   -------------------------- 메인 페이지
     */


    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery)? null: item.itemNm.like("%"+searchQuery +"%");
    }



    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item=QItem.item;
        QItemImg itemImg=QItemImg.itemImg;

        List<MainItemDto> content = queryFactory.select(
                        new QMainItemDto(
                                item.id, 
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl, 
                                item.price))
                .from(itemImg)
                .join(itemImg.item, item)  //itemImg 와 item 을 내부조인
                .where(itemImg.repImgYn.eq("Y"))  //상품 이미지의 경우 대표 상품 이미지만 불러온다
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(itemImg.count())
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()));

//      List<MainItemDto> content = result.getResults();
//      logn total=reseult.getTotal());
//      return new PageImpl<>(content, pageable, total);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }







}
