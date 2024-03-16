package com.shop.utils;


import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

//MySQL PageMaker
@Data
@ToString
public class PageMaker {

  private int page;
  private int pageSize=10;
  private int pageStart;
  private int totalCount; //전체 개수
  private int startPage; // 시작 페이지
  private int endPage;   // 끝페이지
  private boolean prev;  // 이전 여부
  private boolean next;  // 다음 여부
  private boolean last;  //마지막 페이지 여부
  private int displayPageNum=10;  //하단 페이징  << 1 2 3 4 5 6 7 8 9 10 >>
  private int tempEndPage;
  private String searchQuery;

  Page<?>  pageObject;


  //MyBatis 사용시
  // MyBatis SQL 의 Mapper 에서 인식해서 가져가는 파라미터 값 메소드 #{pageStart}
//  public int getPageStart() {
//    //실질적으로 Mybatis 에서  파라미터로 인식해서  가져오는 것은 get 이다.
//    // 따라서 getPageStart 에서 값을 설정한다.
//    //시작 데이터 번호 = (페이지 번호 -1 ) * 페이지당 보여지는 개수
//    this.pageStart=(this.page -1)*pageSize;
//    return this.pageStart;
//  }


  private void calcData(){
    endPage=(int)(Math.ceil(page / (double)displayPageNum)*displayPageNum);
    startPage=(endPage - displayPageNum) +1;

    if(endPage>=tempEndPage)endPage=tempEndPage;
    prev =startPage ==1 ? false :true;
    next =endPage *pageSize >=totalCount ? false :true;
  }

  /**
   *
   * 
   * @param pageObject  Page<?> 반환된 리스트값
   * @param pageInt 현재 페이지
   * @param pageSize  페이지사이즈
   * @param displayPageNum  하단 페이징 기본 10설정 << 1 2 3 4 5 6 7 8 9 10 >>
   * @param pageUrl  url 주소
   * @param type   ajax, href =  자바스크립트 ,  링크
   * @return
   */
  public String pageObject(Page<?> pageObject,  Integer pageInt ,
                                Integer pageSize, Integer displayPageNum , String pageUrl, String type) {
      this.pageObject = pageObject;
      this.page=pageInt==0? 1:pageInt+1;
      if(pageSize!=null){
         this.pageSize=pageSize;
      }
      this.tempEndPage=pageObject.getTotalPages();
      if(displayPageNum!=null){
        this.displayPageNum=displayPageNum;
      }else this.displayPageNum=10;

      this.totalCount=Math.toIntExact(pageObject.getTotalElements());
      calcData();

      if(StringUtils.hasText(pageUrl)){

        if(type.equalsIgnoreCase("JS")){
          return paginationJs(pageUrl);
        }else if(type.equalsIgnoreCase("HREF")){
          return paginationHref(pageUrl);
        }else if(type.equalsIgnoreCase("PATHVARIABLE")){
          return paginationPathVariable(pageUrl);
        }

      }return null;
  }

  /**
   * javascript page 버튼 클릭  반환
   * @param url
   * @return
   */
  public String paginationJs(String url) {
    StringBuffer sBuffer = new StringBuffer();
    sBuffer.append("<ul class='pagination justify-content-center'>");
    if (prev) {
      sBuffer.append("<li class='page-item' ><a class='page-link' onclick='javascript:page(0)' >처음</a></li>");
    }

    if (prev) {
      sBuffer.append("<li class='page-item'><a  class='page-link' onclick='javascript:page("+ (startPage - 2)+")';  >&laquo;</a></li>");
    }

    String active = "";
    for (int i = startPage; i <= endPage; i++) {

      if (page==i) {
        active = "class='page-item active'";
      } else {
        active = "class='page-item'";
      }

      sBuffer.append("<li " + active + " >");
      sBuffer.append("<a class='page-link'  onclick='javascript:page("+ (i-1) +")'; >" + i + "</a></li>");
      sBuffer.append("</li>");
    }


    if (next && endPage > 0 && endPage <= tempEndPage) {
      sBuffer.append("<li class='page-item'><a  class='page-link' onclick='javascript:page("+ (endPage) +")';  >&raquo;</a></li>");
    }


    if (next && endPage > 0 && !isLast()) {
      sBuffer.append("<li class='page-item'> <a class='page-link' onclick='javascript:page("+ (tempEndPage-1) +")'; >마지막</a></li>");
    }

    sBuffer.append("</ul>");
    return sBuffer.toString();
  }




  public String makeSearch(int page){
    UriComponents uriComponents=
        UriComponentsBuilder.newInstance()
            .queryParam("searchQuery", searchQuery)
            .queryParam("page", page)
            .build();
    return uriComponents.toUriString();
  }

  /**
   * 링크 파리미터 반환
   * @param url
   * @return
   */
  public String paginationHref(String url){
    StringBuffer sBuffer=new StringBuffer();
    sBuffer.append("<ul class='pagination justify-content-center'>");
    if(prev){
      sBuffer.append("<li class='page-item'><a  class='page-link' href='"+url+makeSearch(1)+"'>처음</a></li>");
    }

    if(prev){
      sBuffer.append("<li class='page-item'><a  class='page-link' href='"+url+makeSearch(startPage-2)+"'>&laquo;</a></li>");
    }

    String active="";
    for(int i=startPage; i <=endPage; i++){

      if (page==i) {
        active = "class='page-item active'";
        sBuffer.append("<li " +active+"  > ");
        sBuffer.append("<a class='page-link' href='javascript:void(0)'>"+i+"</a></li>");
        sBuffer.append("</li>");

      } else {
        active = "class='page-item'";
        sBuffer.append("<li " +active+"  > ");
        sBuffer.append("<a class='page-link' href='"+url+makeSearch(i-1)+"'>"+i+"</a></li>");
        sBuffer.append("</li>");
      }

    }

    if(next && endPage>0  && endPage <= tempEndPage){
      sBuffer.append("<li class='page-item'><a class='page-link' href='"+url+makeSearch(endPage)+"'>&raquo;</a></li>");
    }

    if (next && endPage > 0 && !isLast()) {
      sBuffer.append("<li class='page-item'><a  class='page-link' href='"+url+makeSearch(tempEndPage-1)+"'>마지막</a></li>");
    }

    sBuffer.append("</ul>");
    return sBuffer.toString();
  }




  public String paginationPathVariable(String url){
    StringBuffer sBuffer=new StringBuffer();
    sBuffer.append("<ul class='pagination justify-content-center'>");
    if(prev){
      sBuffer.append("<li class='page-item'><a  class='page-link' href='"+url+(1)+"'>처음</a></li>");
    }

    if(prev){
      sBuffer.append("<li class='page-item'><a  class='page-link' href='"+url+(startPage-2)+"'>&laquo;</a></li>");
    }

    String active="";
    for(int i=startPage; i <=endPage; i++){

      if (page==i) {
        active = "class='page-item active'";
        sBuffer.append("<li " +active+"  > ");
        sBuffer.append("<a class='page-link' href='javascript:void(0)'>"+i+"</a></li>");
        sBuffer.append("</li>");

      } else {
        active = "class='page-item'";
        sBuffer.append("<li " +active+"  > ");
        sBuffer.append("<a class='page-link' href='"+url+(i-1)+"'>"+i+"</a></li>");
        sBuffer.append("</li>");
      }

    }

    if(next && endPage>0  && endPage <= tempEndPage){
      sBuffer.append("<li class='page-item'><a class='page-link' href='"+url+(endPage)+"'>&raquo;</a></li>");
    }

    if (next && endPage > 0 && !isLast()) {
      sBuffer.append("<li class='page-item'><a  class='page-link' href='"+url+(tempEndPage-1)+"'>마지막</a></li>");
    }

    sBuffer.append("</ul>");
    return sBuffer.toString();
  }

}