import { Checkbox, IconButton, InputBase, ListItem, ListItemSecondaryAction, ListItemText } from "@mui/material";
import { DeleteOutlineOutlined } from "@mui/icons-material";
import { useState } from "react";

const Todo = ({ getItem, editItem,   deleteItem }) => {
  const [item, setItem] = useState(getItem);
  const [readOnly, setReadOnly] = useState(true);
  


  const turnOffReadOnely=()=>{
    setReadOnly(false);
  }

  const editEventHandler =(e)=>{
    setItem({...item, title: e.target.value})
  }


  const checkboxEventHandler =(e)=>{
    const v= e.target.checked;
    item.done = v;
    setReadOnly(v);
    editItem(item);    
  }


  //엔터키 클릭시 저장
  const turnOnReadOnely=(e)=>{
    if(e.key==="Enter" && readOnly===false){
      setReadOnly(true);     
      editItem(item);
    }
    
  }


  return (
    <ListItem className={readOnly ? "input" :''}>
      <Checkbox checked={item.done}  onChange={checkboxEventHandler} />
      <ListItemText>
        <InputBase
          inputProps={{"aria-label" : "naked", 
          readOnly:readOnly}}
          type="text"
          id={item.todoId.toString()}
          name={item.todoId.toString()}
          value={item.title}
          multiline={true}
          fullWidth={true}

          onChange={editEventHandler}
          onClick={turnOffReadOnely}
          onKeyDown={turnOnReadOnely}  
                    
        />
      </ListItemText>
      <ListItemSecondaryAction>
         <IconButton aria-label="Delete Todo"  onClick={()=>deleteItem(item)} >
           <DeleteOutlineOutlined  />
         </IconButton>
      </ListItemSecondaryAction>
    </ListItem>
  );
};



export default Todo;
