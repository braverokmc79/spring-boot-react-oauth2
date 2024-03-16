import React from 'react'
import { Button, Container, Grid, TextField, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { signup } from "../config/ApiServie";

const SignUp = () => {


    const handleSubmit =(e)=>{
        e.preventDefault();
        const data=new FormData(e.target);

        const username=data.get('username');
        const password=data.get('password');
        const email=data.get("email");

        signup({username, email , password}).then(res=>{
            console.log("  signup res :", res);
            //계정 생성 성공시 login 페이지로 리다이렉트
            window.location.href="/login";
        });

    }


    return (
        <Container component="main" maxWidth="xs" style={{marginTop:"8%"}}  >
           <Grid container spacing={2}   >
              <Grid item xs={12}>
                  <Typography component="h1" variant='h5'>
                      계정 생성
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
                        id="email"
                        label="이메일"
                        name="email"
                        autoComplete="email"
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
                      <Button type='submit' fullWidth variant='contained' color="primary" >계정생성</Button>
                    </Grid>



                    <Grid container justify="flex-end">
                         <Grid item >
                            <Link to="/login" variant="body2">
                                이미 계정이 있습니까? 로그인하세요.
                            </Link> 
                        </Grid> 
                    </Grid>

                </Grid>
           </form>
        </Container>
      )
}

export default SignUp