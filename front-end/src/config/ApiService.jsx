import { API_BASE_URL } from "../api-config";

//1.공통 처리 noThen : true  일경우 then 처리 생략
export function callApi(url, method, request, noThen){

    let headers=new Headers({'Content-Type': 'application/json'}) ;

    //2.로컬 스토리지에서 ACCESS TOKEN 가져오기
    const accessToken =localStorage.getItem('ACCESS_TOKEN');
    
    if(accessToken && accessToken!=null){        
        headers.append('Authorization', 'Bearer '+ accessToken);
    }

    //3.갱신토큰 재발급 처리 + 로그아웃시 갱신 토큰 확인처리  (갱신)
    if(request&& request.REFRESH){
        const refreshToken =localStorage.getItem('REFRESH_TOKEN');
        headers.append('RefreshToken',refreshToken);    
    }
    

    let options={
        headers:headers,
        url:API_BASE_URL+url,
        method:method
    }

    if(request){
        options.body=JSON.stringify(request);
    }

    async function fethData(){    
        let  resData=[];  
        try {
            if(noThen){
                //1)로그인, 2)회원가입, 3)접근토큰 재발급일 경우 다음을 실행
                return await fetch(options.url, options);  

            }else{

                const res= await fetch(options.url, options);  
                resData= await res.json();
              
                if(res.status !== 200){
                    if(resData&& resData.errorCode==="TOKEN_EXPIRED"){  //접근토큰 만료 오류일경우 갱신처리                      
                        if(await reissue()){
                            //true 이면 call 함수를 재호출한다.
                            return await callApi(url, method, request); 
                        }                  
                    }                    
                   
                    sinOut();                    
                }
            }                
        } catch (error) {
            console.log("fethData 오류 : ", error);
            window.location.href="/login";
        }
        return resData;     
    }
   
    return fethData();
}



//2.로그인 처리
export async function signIn(userDTO){
    return callApi("/api/auth/signin", "POST", userDTO, true)
        .then(async res=>{
            if(await lsSave(res)){
                window.location.href="/";          
            }else{
                alert("아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        });
}



//3.회원 가입 처리
export async function signUp(userData){
   const res= await callApi("/api/auth/signup", "POST", userData, true);
   if(res.status===200){
        const jd=await res.json();   
        if(jd&&jd.code===1){
            alert(jd.message);
            return true;
        }

   }else{      
        const jd=await res.json();
        //console.log("jd " ,jd);
        if(jd.errorCode){
            alert(jd.errorCode);
            return false;
        }
   }    
}


//4.로그 아웃처리
export async function sinOut(){     
  try{
    await callApi("/api/auth/logout", "POST", {REFRESH:true});           
  }catch(error){
    console.log("로그아웃 오류 :", error);
  }
   lsRemove();  
   window.location.href="/login";   
}


//5.토큰 재발급 처리
async function reissue(){
    const res=  await callApi("/api/auth/reissue", "POST", {REFRESH:true}, true);
    if(lsSave(res)){
        alert("*토큰 재발급 처리*");
        return true;
    }else{
        alert("*토큰 재발급 처리 오류 *");
    }
    sinOut();     
}


//6.로컬스토리지 저장
async function lsSave(res){
    try{
        console.log("==lsSave :",res);
        const jd= await res.json();        
        console.log("==jd :",jd);
        if(res.status===200){                           
            const accessToken= jd&& jd.data&& jd.data.token&&jd.data.token.accessToken;
            const refreshToken= jd&& jd.data&& jd.data.token&&jd.data.token.refreshToken;
         
            if(accessToken&&refreshToken) {
              localStorage.setItem("ACCESS_TOKEN", accessToken);
              localStorage.setItem("REFRESH_TOKEN", refreshToken);
              return true;
            }
            

        }else{

            if(jd.errorCode==="INVALID_REFRESH_TOKEN"){
                //갱신토큰 오류 로그 아웃처리
                alert(jd.message);
                sinOut();   
            }

            return false;
        }

    }catch(error){
        console.log("lsSave 오류  :", error);   
        sinOut();   
    }
   
}


//7.로컬스토리지 데이터 삭제
function lsRemove(){
    localStorage.removeItem("ACCESS_TOKEN"); 
    localStorage.removeItem("REFRESH_TOKEN"); 
    return true;    
}





//json 반환 처리 데이터 예
/**
{
    "code": 1,
    "message": "success",
    "errorCode": null,
    "data": {
        "id": "1",
        "token": {
            "grantType": "Bearer ",
            "accessToken": "1234토큰",
            "refreshToken": ""
        },
        "username": "test1",
        "name": "테스터1",
        "email": "test1@gmail.com",
        "role": "USER",
        "authProvider": null,
        "authProviderId": null
    }
}
 */