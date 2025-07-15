from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel
from typing import List, Optional
import uvicorn
from uuid import uuid4, UUID

# Initialize FastAPI app
app = FastAPI(title="Todo List API")

# Pydantic model for Todo items
class TodoCreate(BaseModel):
    title: str
    description: Optional[str] = None
    completed: bool = False

class Todo(TodoCreate):
    id: str

# In-memory storage for todos
todos = {}

@app.get("/")
def read_root():
    """Root endpoint that returns a welcome message."""
    return {"message": "Welcome to the Todo List API"}

@app.post("/todos/", response_model=Todo, status_code=status.HTTP_201_CREATED)
def create_todo(todo: TodoCreate):
    """Create a new todo item."""
    todo_id = str(uuid4())
    todo_dict = todo.dict()
    todo_dict["id"] = todo_id
    todos[todo_id] = todo_dict
    return todo_dict

@app.get("/todos/", response_model=List[Todo])
def read_todos():
    """Get all todo items."""
    return list(todos.values())

@app.get("/todos/{todo_id}", response_model=Todo)
def read_todo(todo_id: str):
    """Get a specific todo item by ID."""
    if todo_id not in todos:
        raise HTTPException(status_code=404, detail="Todo not found")
    return todos[todo_id]

@app.put("/todos/{todo_id}", response_model=Todo)
def update_todo(todo_id: str, todo: TodoCreate):
    """Update a todo item."""
    if todo_id not in todos:
        raise HTTPException(status_code=404, detail="Todo not found")
    
    todo_dict = todo.dict()
    todo_dict["id"] = todo_id
    todos[todo_id] = todo_dict
    return todo_dict

@app.delete("/todos/{todo_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_todo(todo_id: str):
    """Delete a todo item."""
    if todo_id not in todos:
        raise HTTPException(status_code=404, detail="Todo not found")
    
    del todos[todo_id]
    return None

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)