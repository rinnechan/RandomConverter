# Simple converter using API from http://api.currencylayer.com/live

### Setup Instructions

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd CurrencyConverter
    ```
2.  **Configure environment variables:**
    Create a `.env` file in the root of the project.
    
    ```
    API_KEY=YourAPIKey
    API_URL=http://api.currencylayer.com/live
    ```
    Replace yourAPIKey with the real one you get from the URL
3.  Run with
    ```
    mvn install
    mvn exec:java
    ```
