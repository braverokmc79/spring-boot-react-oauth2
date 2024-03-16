import { Button } from "@mui/base";
import { Grid, TextField } from "@mui/material";
import React, { useState } from "react";

const AddTodo = ({addItem}) => {
  //사용자의 입력을 저장할 오브젝트  
  const [item, setItem] = useState({ title: "" });
 
 
  const onButtonClick =(e) =>{
    addItem(item);
    setItem({title : ""});
  }

  const onInputChange =(e) =>{
    setItem({title : e.target.value});
    //console.log(item);
  }

 const enterKeyEventHandler = (e) =>{
    if(e.key ==='Enter'){
      onButtonClick();
    }
 }



  return (
    <Grid container style={{ marginTop: 20 }}  >
      <Grid xs={11} item style={{ paddingRight: 16 }}>
        <TextField placeholder="Add Todo here" fullWidth    onKeyDown={enterKeyEventHandler}    onChange={onInputChange} value={item.title} />
      </Grid>
      <Grid xs={1} md={1} item>
        <Button  style={{ height: "100%" }} color="secondary" variant="outlined" 
        
          onClick={onButtonClick}>
          +
        </Button>
      </Grid>
    </Grid>
  );
};

export default AddTodo;
