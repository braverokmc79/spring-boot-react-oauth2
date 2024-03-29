import { Button, Grid, TextField } from "@mui/material";
import React, { useState } from "react";

const AddTodo = ({addItem}) => {
  const [item, setItem] = useState({ title: "" });

  const onButtonClick = () =>{
    addItem(item);
    setItem({title:""});
  }

  //엔터키 처리
  const enterKeyEventHandler = (e)=>{
      if(e.key === "Enter"){
        onButtonClick();
      }
  }
  
  const onInputChange = (e)=>{
    setItem({title:e.target.value});    
  }


  return(
  <Grid container className="mt100">
    <Grid xs={11} md={11} item >
      <TextField placeholder="Add Todo here" fullWidth  onChange={onInputChange}  value={item.title} onKeyDown={enterKeyEventHandler}  />
    </Grid>
    <Grid xs={1} md={1} item >
      <Button    fullWidth={true} style={{ height: "100%" }} color="secondary"  variant="outlined"   onClick={onButtonClick}>
        +
      </Button>
    </Grid>
  </Grid>);
};

export default AddTodo;
