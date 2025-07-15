import unittest
from fastapi.testclient import TestClient
from main import app

class TestTodoAPI(unittest.TestCase):
    def setUp(self):
        self.client = TestClient(app)
        # Clear todos before each test
        import main
        main.todos = {}

    def test_read_root(self):
        """Test the root endpoint returns a welcome message."""
        response = self.client.get("/")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json(), {"message": "Welcome to the Todo List API"})

    def test_create_todo(self):
        """Test creating a new todo item."""
        todo_data = {"title": "Test Todo", "description": "This is a test", "completed": False}
        response = self.client.post("/todos/", json=todo_data)
        self.assertEqual(response.status_code, 201)
        
        # Check response contains the todo with an ID
        todo = response.json()
        self.assertIn("id", todo)
        self.assertEqual(todo["title"], todo_data["title"])
        self.assertEqual(todo["description"], todo_data["description"])
        self.assertEqual(todo["completed"], todo_data["completed"])

    def test_read_todos(self):
        """Test reading all todo items."""
        # Create a couple of todos
        todo1 = {"title": "Todo 1", "description": "First todo"}
        todo2 = {"title": "Todo 2", "description": "Second todo"}
        
        self.client.post("/todos/", json=todo1)
        self.client.post("/todos/", json=todo2)
        
        # Get all todos
        response = self.client.get("/todos/")
        self.assertEqual(response.status_code, 200)
        
        # Check we got both todos
        todos = response.json()
        self.assertEqual(len(todos), 2)

    def test_read_todo(self):
        """Test reading a specific todo item."""
        # Create a todo
        todo_data = {"title": "Test Todo", "description": "This is a test"}
        create_response = self.client.post("/todos/", json=todo_data)
        todo_id = create_response.json()["id"]
        
        # Get the todo by ID
        response = self.client.get(f"/todos/{todo_id}")
        self.assertEqual(response.status_code, 200)
        
        # Check it's the right todo
        todo = response.json()
        self.assertEqual(todo["id"], todo_id)
        self.assertEqual(todo["title"], todo_data["title"])

    def test_update_todo(self):
        """Test updating a todo item."""
        # Create a todo
        todo_data = {"title": "Original Title", "description": "Original description"}
        create_response = self.client.post("/todos/", json=todo_data)
        todo_id = create_response.json()["id"]
        
        # Update the todo
        updated_data = {"title": "Updated Title", "description": "Updated description", "completed": True}
        response = self.client.put(f"/todos/{todo_id}", json=updated_data)
        self.assertEqual(response.status_code, 200)
        
        # Check the todo was updated
        todo = response.json()
        self.assertEqual(todo["id"], todo_id)
        self.assertEqual(todo["title"], updated_data["title"])
        self.assertEqual(todo["description"], updated_data["description"])
        self.assertEqual(todo["completed"], updated_data["completed"])

    def test_delete_todo(self):
        """Test deleting a todo item."""
        # Create a todo
        todo_data = {"title": "Test Todo"}
        create_response = self.client.post("/todos/", json=todo_data)
        todo_id = create_response.json()["id"]
        
        # Delete the todo
        response = self.client.delete(f"/todos/{todo_id}")
        self.assertEqual(response.status_code, 204)
        
        # Check the todo was deleted
        get_response = self.client.get(f"/todos/{todo_id}")
        self.assertEqual(get_response.status_code, 404)

    def test_read_nonexistent_todo(self):
        """Test reading a todo that doesn't exist."""
        response = self.client.get("/todos/nonexistent-id")
        self.assertEqual(response.status_code, 404)

    def test_update_nonexistent_todo(self):
        """Test updating a todo that doesn't exist."""
        todo_data = {"title": "Updated Title"}
        response = self.client.put("/todos/nonexistent-id", json=todo_data)
        self.assertEqual(response.status_code, 404)

    def test_delete_nonexistent_todo(self):
        """Test deleting a todo that doesn't exist."""
        response = self.client.delete("/todos/nonexistent-id")
        self.assertEqual(response.status_code, 404)

if __name__ == "__main__":
    unittest.main()