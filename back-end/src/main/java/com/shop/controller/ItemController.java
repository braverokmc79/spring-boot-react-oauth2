package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import com.shop.utils.PageMaker;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(@ModelAttribute("itemFormDto") ItemFormDto itemFormDto){
        return "item/itemForm";
    }


    
    //상품 등록 처리
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){

        //1.상품 등록 시 필수 값이 없다면 다시 상품 등록 페이지로 전환합니다.
        if(bindingResult.hasErrors()) return "item/itemForm";

        //2.상품 등록시 첫 번째 이미지가 없다면 에러 메시지와 함께 상품 등록 페이지로 전환합니다.
        //상품의 첫 번째 이미지는 메인 페이지에서 보여줄 상품 이미지로 사용하기 위해서 필수 값으로 지정
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() ==null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try{
            //상품 저장 로직을 호출합니다. 매개 변수로 상품 정보와 상품 이미지 정보를 담고 있는
            //itemImgFileList 를 넘겨줍니다.
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return  "redirect:/";
    }


  //3. 상품 상세보기 처리
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            return "item/itemForm";
        }
        return "item/itemForm";
    }


    /**
     * 상품 등록
     * @param itemFormDto
     * @param bindingResult
     * @param itemImgFileList
     * @param model
     * @return
     */
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){

        if(bindingResult.hasErrors()) return  "item/itemForm";


        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() ==null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }
        

        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }


    /**
     * 상품 관리화면 이동
     * @param itemSearchDto
     * @param page
     * @param model
     * @return
     */
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, PageMaker pageMaker, Model model){
        Integer pageInt = page.orElse(0);
        Pageable pageable = PageRequest.of(pageInt, 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        /// pagination html  처리
        String pagination=pageMaker.pageObject(items, pageInt, 10, 5 ,  "/admin/items/" ,"js" );

        model.addAttribute("items", items);
        model.addAttribute("ite", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("pagination", pagination);
        return "item/itemMng";
    }


    /**
     * 6.5 상품 상세 페이지
     */
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }





}







