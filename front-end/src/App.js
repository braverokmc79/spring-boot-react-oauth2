import "./App.css";
import Todo from "./components/Todo";
import { useEffect, useState } from "react";
import { AppBar, Button, Container, Grid, List, Paper, Toolbar, Typography } from "@mui/material";
import AddTodo from "./components/AddTodo";
import { callApi, sinOut } from "./config/ApiService";

function App() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  //목록 불러오기
  useEffect(() => {
    async function getItemsList(){
      try{
        await callApi("/api/todo", "get" , null).then(res=> {
               console.log("getItemsList  :  ", res);
               if(res.code!==-1){
                 setItems(res.data)      
               }else{
                window.location.href="/login";
               }
                               
            });
          setLoading(false);        
      }catch(error){        
        console.log(error);
        window.location.href="/login";
      }
      
    }
     getItemsList();
  }, [])


  //등록처리
  const addItem=(item)=>{
    callApi("/api/todo", "POST" , item).then(res=> setItems(res.data));
  }


  //삭제처리
  const deleteItem=(item)=>{
    callApi("/api/todo", "DELETE" , item).then(res=> setItems(res.data));  
  }

 //수정하기
  const editItem=(item)=>{
    callApi("/api/todo", "PUT" , item).then(res=> setItems(res.data));  
  }

 //navigationBar 추가
 let navigationBar =(
  <AppBar postion="static"  >
     <Toolbar>
        <Grid justifyContent="space-between" container>
          <Grid item>
              <Typography variant="h6">오늘의 할일</Typography>
          </Grid>
          
          <Grid item>
              <Button color="inherit"  onClick={sinOut}>
                로그아웃
              </Button>
          </Grid>             

        </Grid>
     </Toolbar>
  </AppBar>
);

  let todoItems=items&&items!==null && items.length > 0 && (
      <Paper className="mt50">
        <List>
          {items.map((item)=>(
            <Todo getItem={item}  key={item.todoId}  editItem={editItem}   deleteItem={deleteItem}    />
          ))
          }
        </List>
      </Paper>
  )

  // 로딩중이 아닐 때 랜더링할 부분
  let todoListPage=(
    <>
    {navigationBar}      
    <Container maxWidth="md" className="to70">  
      <AddTodo addItem={addItem} />
      <div className="TodoList">{todoItems}</div>
    </Container>
    </>
  )

  ///로딩중리 때 래더링할 부분
  let loadingPage=<h1>로딩중....</h1>
  let content=loadingPage;
  if(!loading){
    // 로딩중이 아니라면 todoListPage 를 선택
    content=todoListPage;
  }



  return (
    <div className="App">
      {content}
    </div>
  );


}

export default App;
