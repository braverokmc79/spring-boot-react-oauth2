import { Button, Container, Grid, TextField, Typography } from '@mui/material'
import React from 'react'
import { Link } from 'react-router-dom'
import { signIn } from '../config/ApiService';
import { API_BASE_URL } from '../api-config';

const LoginPage = () => {

  const handleSubmit=(event)=>{
    event.preventDefault();
    const fd=new FormData(event.target);
    const param=Object.fromEntries(fd.entries());

    
    console.log("param :",param);
    signIn(param);
  }




  const handleSocialLogin =(provider)=>{
    window.location.href=API_BASE_URL+"/api/auth/authorize/"+provider;
  }


  return (
    <Container component="main" maxWidth="xs" style={{marginTop:"8%"}}  >
       <Grid container spacing={2}   >
          <Grid item xs={12}>
              <Typography component="h1" variant='h5'>
                  로그인
              </Typography>
          </Grid>
       </Grid>

       <form noValidate onSubmit={handleSubmit}>
            {""}
            {/* submit 버튼을 누르면 handleSubmit 이 실행됨 */}
            <Grid container spacing={2}>

                <Grid item xs={12}>
                <TextField
                    variant='outlined'
                    required
                    fullWidth
                    id="username"
                    label="아이디"
                    name="username"
                    autoComplete="username"
                />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                      variant='outlined'
                      required
                      fullWidth
                      type='password'
                      id="password"
                      label="패스워드"
                      name="password"
                      autoComplete="current-password"
                  />
                </Grid>

                <Grid item xs={12}>
                  <Button type='submit' fullWidth variant='contained' color="primary" >로그인</Button>
                </Grid>

                 <Grid item xs={12}>
                  <Button  
                    onClick={()=>handleSocialLogin('github')}
                    fullWidth variant='contained' color="primary"   style={{background:'#000', marginBottom:'10px'}}   >깃허브로 로그인하기</Button>
                </Grid> 


                <Grid container justify="flex-end">
                         <Grid item >
                            <Link to="/signup" variant="body2">
                                계정이 없습니까? 여기서 가입하세요.
                            </Link> 
                        </Grid> 
                    </Grid>
            </Grid>
       </form>
    </Container>
  )


}

export default LoginPage