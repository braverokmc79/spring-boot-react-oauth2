import {API_BASE_URL} from './api-config';

//1.데이터 목록 불러오기 , 2.추가, 3.삭제, 4.수정
export async function call(url, mehtod, request){
    console.log("call ", url, mehtod, request);

    let headers=new Headers({'Content-Type': 'application/json'});
 
    //로컬 스토리지에서 ACCESS TOKEN 가져오기
    const accessToken =localStorage.getItem('ACCESS_TOKEN');
    if(accessToken && accessToken!=null){
        headers.append('Authorization', 'Bearer '+ accessToken);
    }

    let options={        
        headers: headers,
        url:API_BASE_URL + url,
        method: mehtod,
    };

    if(request){
        //GET
        options.body=JSON.stringify(request);
    }


    return fetch(options.url, options)
    .then(async (res)=>{
        console.log(res.status, res);

        if(res.status===200){
            return res.json();

        }else if(res.status===400 ||res.status===403 || res.status===403){                           
                const resData=await getData(res.json());                
               
                console.log("resData ", resData);
               
                alert(resData.message);
               
                if(resData.errorCode==="TOKEN_EXPIRED"){
                     //접근 토큰 유효기간 만료 
                     const result=await reissue();
                     if(result){
                        //call 함수를 재호출한다.
                        return await call(url, mehtod, request);     
                     }                    
                }else if(resData.errorCode==="ACCESS_DENIED"){
                    
                }

                //오류 등 유효하지 않으면  로그인화면으로
                window.location.href="/login";                                           
        }else{
            Promise.reject(res);
            throw Error(res);   
        }         
    }).catch((error) => {
        console.log("에러 ", error);
        window.location.href="/login";
    });

}


//https://squirmm.tistory.com/entry/ReactJavaScript-Promise-값-가져오기
//promiseresult 데이터값 반환
async function  getData(resData){
   return await resData.then((promiseResult)=>{
             return promiseResult;
    });
}

//접근 토큰 및 갱신토큰 재발급 처리
async function reissue(){
    let headers=new Headers({'Content-Type': 'application/json'});   
    //로컬 스토리지에서 갱신토큰 가져오기
    const refreshToken =localStorage.getItem('REFRESH_TOKEN');
    if(refreshToken && refreshToken!=null){
        headers.append('RefreshToken',refreshToken);
    }

    let options={        
        headers: headers,        
        method: "POST",
    };

    return await fetch(API_BASE_URL+"/api/auth/reissue",options)
     .then((res)=>res.json())
     .then(res=>{       
        console.log("reissue (접근 토큰 및 갱신토큰 재발급 처리)  :", res );

         if(res.token){
                //로컬스토리지에 저장;   
                console.log("로컬스토리지에 저장 : ", res.token.accessToken, res.token.refreshToken);
                localStorage.setItem("ACCESS_TOKEN", res.token.accessToken);
                localStorage.setItem("REFRESH_TOKEN", res.token.refreshToken);
                alert("갱신 처리 완료");
                return  true;    
                
        }else if(res.errorCode==="INVALID_REFRESH_TOKEN") {
                alert("갱신 토큰이 유요하지 않습니다.");
                return false;                     
        }else {
                alert("기타 오류");
                return false;  
        }             
     }).catch(err=>{
        console.log("접근 토큰 및 갱신토큰 재발급 처리 오류  :", err);
     })
     
}




//로그인 처리
export function signin(userDTO){
    return call("/api/auth/signin", "POST", userDTO)
     .then((res)=>{
        if(res){
            if(res==="invalid"){
                alert("아이디 또는 비밀번호가 일치하지 않습니다.");
                return;
            }                
            //로컬 스토리지에 토큰 저장
            localStorage.setItem("ACCESS_TOKEN", res.token.accessToken);
            localStorage.setItem("REFRESH_TOKEN", res.token.refreshToken);
            window.location.href="/";
        }
        
     })
}


//로그 아웃 처리
export function signout(){
    let headers=new Headers({'Content-Type': 'application/json'});   
    const refreshToken =localStorage.getItem('REFRESH_TOKEN');
    if(refreshToken && refreshToken!=null){
        headers.append('RefreshToken',refreshToken);
    }
    let options={        
        headers: headers,        
        method: "POST",
    };

    fetch(API_BASE_URL+"/api/auth/logout",options)
     .then(res=>res.json())
     .then(res=>{
        if(res.code===1){
            localStorage.removeItem("ACCESS_TOKEN");
            localStorage.removeItem("REFRESH_TOKEN");            
        }else if(res.code===-1){
            console.log("로그아웃 오류 : " ,res.error);
            alert(res.message);
        }
        window.location.href="/login";
     }).catch(err=>{
        console.log("에러 : ",err);
        window.location.href="/login";
    });
}



//계정 생성 처리
export function signup(userDTO){
    return call("/api/auth/signup", "POST", userDTO)
}



//oauth2 로그인 
export function socialLogin(provider){
    let frontedUrl="http://localhost:3000";
    const hostname =window && window.location && window.location.hostname;
    if(hostname==="localhost"){
        frontedUrl="http://localhost:3000";
    }else{
        frontedUrl="https://ma7front.p-e.kr";
    }
    window.location.href=API_BASE_URL+"/api/auth/authorize/"+provider +"?redirect_url="+frontedUrl;
}
