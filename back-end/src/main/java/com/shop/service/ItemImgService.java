package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            Map<String, Object> resultMap = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgName=resultMap.get("imgName").toString();
            //ex)  /images/item/2024/01/25/26a37f77-81da-4077-bf21-68e0bb597f46.jpg
            imgUrl = "/images/item" + resultMap.get("imgUrl").toString();
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }


    /**
     * 이미지 업데이트 등록
     * @param itemImgId
     * @param itemImgFile
     * @throws Exception
     */
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation+"/"+ savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            Map<String, Object> resultMap= fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgName=resultMap.get("imgName").toString();
            String imgUrl = "/images/item/" + resultMap.get("imgUrl").toString();

            //더티 체킹
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }





}