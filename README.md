# Todo List API

A simple RESTful API for managing a todo list, built with FastAPI.

## Features

- Create, read, update, and delete todo items
- In-memory storage (resets when server restarts)
- Full API documentation via Swagger UI

## Installation

1. Clone this repository
2. Install dependencies:
   ```
   pip install -r requirements.txt
   ```

## Running the API

Start the server with:

```
python main.py
```

The API will be available at http://localhost:8000

API documentation is available at http://localhost:8000/docs

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | /        | Welcome message |
| GET    | /todos/  | List all todo items |
| POST   | /todos/  | Create a new todo item |
| GET    | /todos/{todo_id} | Get a specific todo item |
| PUT    | /todos/{todo_id} | Update a todo item |
| DELETE | /todos/{todo_id} | Delete a todo item |

## Running Tests

Run the tests with:

```
python -m unittest test_api.py
```

## Todo Item Structure

```json
{
  "id": "string",
  "title": "string",
  "description": "string (optional)",
  "completed": boolean
}
```