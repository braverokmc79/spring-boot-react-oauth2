package com.shop.service;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    public Map<String,Object> uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
        //현재 날짜 얻어서 파일명에 붙이기
        String datePath=calcPath(uploadPath);
        uploadPath=uploadPath+"/"+datePath;


        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;


        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        //return fileDate+"/"+savedFileName;
        //ex) /images/item//2024/01/25/26a37f77-81da-4077-bf21-68e0bb597f46.jpg

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("imgName", savedFileName);
        map.put("imgUrl", datePath+"/"+savedFileName);
        return map;
    }



    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);
        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }



    //파일이 저장될  '년/월/일' 정보 생성
    private static String calcPath(String uploadPath){
        Calendar cal =Calendar.getInstance();
        // 역슬래시 + 2017
        String yearPath= File.separator + cal.get(Calendar.YEAR);

        //  /2024 +/+ 10  한자리 월 일경우 01, 02 형식으로 포멧
        String monthPath=yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);

        // /2024/10 +/ + 22
        String datePath = monthPath+ File.separator+ new DecimalFormat("00").format(cal.get(Calendar.DATE));

        //년월일 폴더 생성하기
        makeDir(uploadPath, yearPath, monthPath, datePath);

        log.info(" datePath - {}", datePath);

        return datePath.replace(File.separator, "/");
    }

    // 실질 적인 날짜 폴더 생성
    private static void makeDir(String uploadPath, String... paths){
        if(new File(paths[paths.length-1]).exists()){
            //년 월 일 에서 일 배열 paths 에서 paths -1 은 일  즉 해당일의 폴더가 존재하면 return
            return ;
        }

        for(String path :paths){
            File dirPath =new File(uploadPath+path);
            if(!dirPath.exists()){
                //년 월일에 대한 해당 폴더가 존재하지 않으면 폴더 생성
                dirPath.mkdirs();
            }
        }

    }


}