import requests
import json

# Base URL for the API
BASE_URL = "http://localhost:8000"

def print_response(response):
    """Print the response in a formatted way."""
    print(f"Status Code: {response.status_code}")
    try:
        print(json.dumps(response.json(), indent=2))
    except:
        print(response.text)
    print("-" * 50)

def main():
    print("Welcome to the Todo API Example")
    print("=" * 50)
    
    # Get the welcome message
    print("\n1. Getting welcome message:")
    response = requests.get(f"{BASE_URL}/")
    print_response(response)
    
    # Create a todo item
    print("\n2. Creating a new todo item:")
    todo_data = {
        "title": "Buy groceries",
        "description": "Milk, eggs, bread, and cheese",
        "completed": False
    }
    response = requests.post(f"{BASE_URL}/todos/", json=todo_data)
    print_response(response)
    
    # Save the todo ID for later use
    todo_id = response.json()["id"]
    
    # Create another todo item
    print("\n3. Creating another todo item:")
    todo_data = {
        "title": "Finish project",
        "description": "Complete the API implementation",
        "completed": False
    }
    response = requests.post(f"{BASE_URL}/todos/", json=todo_data)
    print_response(response)
    
    # Get all todos
    print("\n4. Getting all todo items:")
    response = requests.get(f"{BASE_URL}/todos/")
    print_response(response)
    
    # Get a specific todo
    print(f"\n5. Getting todo with ID {todo_id}:")
    response = requests.get(f"{BASE_URL}/todos/{todo_id}")
    print_response(response)
    
    # Update a todo
    print(f"\n6. Updating todo with ID {todo_id}:")
    updated_data = {
        "title": "Buy groceries",
        "description": "Milk, eggs, bread, cheese, and fruit",
        "completed": True
    }
    response = requests.put(f"{BASE_URL}/todos/{todo_id}", json=updated_data)
    print_response(response)
    
    # Delete a todo
    print(f"\n7. Deleting todo with ID {todo_id}:")
    response = requests.delete(f"{BASE_URL}/todos/{todo_id}")
    print(f"Status Code: {response.status_code}")
    print("-" * 50)
    
    # Verify the todo was deleted
    print(f"\n8. Verifying todo with ID {todo_id} was deleted:")
    response = requests.get(f"{BASE_URL}/todos/{todo_id}")
    print_response(response)
    
    # Get all todos again to see the changes
    print("\n9. Getting all todo items after deletion:")
    response = requests.get(f"{BASE_URL}/todos/")
    print_response(response)

if __name__ == "__main__":
    print("Note: Make sure the API server is running before executing this script.")
    print("Run 'python main.py' in a separate terminal to start the server.")
    
    try:
        main()
    except requests.exceptions.ConnectionError:
        print("Error: Could not connect to the API server.")
        print("Make sure the server is running at http://localhost:8000")