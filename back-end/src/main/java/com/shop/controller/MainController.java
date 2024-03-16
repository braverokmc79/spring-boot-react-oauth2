package com.shop.controller;

import com.shop.config.auth.PrincipalDetails;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.api.todo.ResDTO;
import com.shop.dto.api.todo.TodoDTO;
import com.shop.service.ItemService;
import com.shop.utils.PageMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final ItemService itemService;

    @GetMapping(value = "/")
    public String indexPage(ItemSearchDto itemSearchDto, @PathVariable(value ="page") Optional<Integer> page, PageMaker pageMaker, Model model ) {
        Integer pageInt = page.orElse(0);
        PageRequest pageable = PageRequest.of(pageInt, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        String pagination = pageMaker.pageObject(items, pageInt, 6, 5, "/", "href");

        log.info("==========MainController   {} ", items.getTotalElements());
        List<MainItemDto> content = items.getContent();
        for(MainItemDto  dto :content ){
            log.info("==========MainController   {} ", dto.toString());
        }
        
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("pagination", pagination);
        return "main";
    }



    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value = "/main")
    public String mainPage(ItemSearchDto itemSearchDto, @PathVariable(value ="page") Optional<Integer> page, PageMaker pageMaker, Model model ) {
        Integer pageInt = page.orElse(0);
        PageRequest pageable = PageRequest.of(pageInt, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        String pagination = pageMaker.pageObject(items, pageInt, 6, 5, "/", "href");

        log.info("==========MainController   {} ", items.getTotalElements());
        List<MainItemDto> content = items.getContent();
        for(MainItemDto  dto :content ){
            log.info("==========MainController   {} ", dto.toString());
        }

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("pagination", pagination);
        return "main";
    }




}
