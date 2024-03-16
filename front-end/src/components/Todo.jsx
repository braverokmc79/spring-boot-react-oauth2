import { Checkbox,  IconButton, InputBase, ListItem, ListItemSecondaryAction, ListItemText } from "@mui/material";
import React, { useState } from "react";
import { DeleteOutlineOutlined } from "@mui/icons-material";

const Todo = ({ inputItem , editItem , deleteItem}) => {
  const [readOnly, setReadOnly] = useState(true);
  const [item, setItem] =useState(inputItem);

  const turnOffReadOnly =()=>{
    setReadOnly(false);
  }

  //엔터키 누르면 저장
  const turnOnReadOnly =(e)=>{
    if(e.key==="Enter" && readOnly===false){
      setReadOnly(true);
      editItem(item);
    }
  }

  //포커스 bure 일경우 저장
  const editOnBlur =(e)=>{
    setReadOnly(true);
    editItem(item);
  }

  const editEventHandler =(e)=>{
    setItem({...item, title:e.target.value});
  }

  
  const checkBoxHandler=(e)=>{
    item.done = e.target.checked;
    editItem(item);
  }


  return (
    <ListItem  className={readOnly ? 'input' :''}>
      <Checkbox checked={item.done}  onChange={checkBoxHandler} />

      <ListItemText>
        <InputBase
          inputProps={{ "aria-label": "naked" , readOnly: readOnly }}
          onClick={turnOffReadOnly}
          onKeyDown={turnOnReadOnly}
          type="text"
          id={item.id}
          name={item.id}
          value={item.title}
          multiline={true}
          fullWidth={true}
          onChange={editEventHandler}
          onBlur={editOnBlur}
        />

      </ListItemText>
      <ListItemSecondaryAction>
        <IconButton aria-label="Delete Todo" onClick={()=>deleteItem(item)} >
        <DeleteOutlineOutlined />

        </IconButton>
      </ListItemSecondaryAction>
    </ListItem>
  );
};

export default Todo;
