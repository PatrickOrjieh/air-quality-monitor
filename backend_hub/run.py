from app import app
from background_tasks import start_background_task

print("Before starting background task")
start_background_task(app)
print("After starting background task")

if __name__ == "__main__":
    app.run(debug=True)
