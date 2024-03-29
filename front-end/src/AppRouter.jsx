import React from 'react'
import Copyright from './components/Copyright'
import { Route, Routes } from 'react-router-dom'
import { Box } from '@mui/material'

import App from './App'
import SinUp from './pages/SinUp'
import SocialLogin from './pages/SocialLogin'
import LoginPage from './pages/LoginPage'

const AppRouter = () => {


  return (
    <div>
         <Routes>
            <Route path='/' element={<App  />} />
            <Route path='/login' element={<LoginPage  />} />
            <Route path='/signup' element={<SinUp  />} />
            <Route path='/sociallogin' element={<SocialLogin />} />
         </Routes>

         <Box mt={5}>
            <Copyright />
        </Box>
    </div>
  )
}

export default AppRouter