import { Navigate } from "react-router-dom";

const Sociallogin = (props) => {

  const getUrlParameters=(name)=>{
            let search=window.location.search;
            let params=new URLSearchParams(search);
            return params.get(name);
  };

  const accessToken=getUrlParameters('accessToken');
  const refreshToken=getUrlParameters('refreshToken');

  if(accessToken && refreshToken){
        console.log("로컬스토리지에 저장 accessToken:", accessToken);
        console.log("로컬스토리지에 저장 refreshToken:", refreshToken);
        localStorage.setItem("ACCESS_TOKEN", accessToken);
        localStorage.setItem("REFRESH_TOKEN",refreshToken);
        return (  <Navigate to={{pathname:'/',  state:{from: props.location}}} /> )     

  }else{
        return (<Navigate to={{pathname:'/login',  state:{from: props.location}}} />)
  }

}

export default Sociallogin