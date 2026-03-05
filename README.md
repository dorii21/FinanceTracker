# Finance Tracker

A full stack application for managing personal finances.

## Features
- **User authentication:** Login and registration
- **Transaction management:** Add, edit and delete transactions (expense/income)
- **Data filtering:** Filter transactions based on a specific criteria
- **Export functionality:** Export the (filtered) transactions to a CSV file

## Project structure
- **/FinanceTracker:** Spring Boot backend API
- **/JavaFX:** JavaFX-based user interface

## Usage
1. **Start the database:** Ensure you have Docker installed. In the /FinanceTracker directory of the project (where the docker-compose.yml file is located), run the following command:
```
docker compose up -d
```
2. **Run the backend:** Open the /FinanceTracker folder in your IDE and run the FinanceTrackerApplication.java file.
3. **Run the frontend:** Open the /JavaFX folder in your IDE and run the HelloApplication.java file.
