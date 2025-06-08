
Charity Fundraising Collection Box API
======================================

A Spring Boot REST API for managing collection boxes during fundraising events organized by charity organizations.
This project was built for the Letnia Akademia Talentów 2025 (LAT 2025) recruitment task at Sii Polska.

Features
--------
- Create and manage fundraising events
- Register, assign, and unassign collection boxes
- Add funds in multiple currencies
- Automatically convert and transfer funds to an event’s account
- Display financial reports of all fundraising events
- Simple in-memory database (H2) with no authentication

Tech Stack
----------
- Java 21
- Spring Boot
- Maven
- H2 In-Memory Database
- REST API (JSON)
- Optional: External currency exchange API

Getting Started
---------------
1. Clone the repository:

   git clone https://github.com/your-username/charity-collection-box-api.git

   cd charity-collection-box-api

3. Build the project:

   mvn clean install

4. Run the application:

   mvn spring-boot:run

   The application will start on http://localhost:8080.

REST Endpoints
--------------

Fundraising Events
------------------
1. Create a new event:
   POST /api/events/create
   Parameters:
     - name (String)
     - currencyCode (String) — e.g. USD, EUR

2. Get all events:
   GET /api/events

3. Financial report:
   GET /api/events/financial-report
   Returns a list of all fundraising events with their current balances in assigned currencies.

Collection Boxes
----------------
1. Create a new collection box:
   POST /api/boxes/create

2. Get all collection boxes:
   GET /api/boxes
   Returns a list of all boxes showing:
     - box ID
     - assigned status
     - whether it is empty

3. Assign a box to an event:
   PUT /api/boxes/{boxId}/assign/{eventId}

4. Unassign a box:
   PUT /api/boxes/{boxId}/unassign?reason={reason}

5. Add funds to a box:
   PUT /api/boxes/{boxId}/addFunds/{amount}/{currencyCode}
   Example:
     PUT /api/boxes/1/addFunds/50.00/USD

6. Transfer funds to assigned event:
   PUT /api/boxes/{boxId}/transferToEvent

Currency Handling
-----------------
- The system supports multiple currencies (e.g. USD, EUR, GBP).
- Exchange rates are hardcoded or optionally fetched from an external API.
- Currency conversion is performed automatically when transferring funds from a box to an event’s account.

H2 Database Console
-------------------
Access the in-memory H2 database at:
  http://localhost:8080/h2-console

Credentials:
  JDBC URL: jdbc:h2:mem:fundraising_db
  User: admin
  Password: admin

Sample Request
--------------
Create an event in EUR:

  POST http://localhost:8080/api/events/create?name=Hope2025&currencyCode=EUR

Create a box, assign it, add funds, and transfer:
 - POST http://localhost:8080/api/boxes/create
 - PUT  http://localhost:8080/api/boxes/1/assign/1
 - PUT  http://localhost:8080/api/boxes/1/addFunds/100.00/USD
 - PUT  http://localhost:8080/api/boxes/1/transferToEvent

Notes
-----
- No authentication or authorization.
- Decimal accuracy is preserved (e.g. 0.99 EUR).
- Exchange rates are fetched using an external API - https://exchangeratesapi.io/.

Contact
-------
Created by OG-YODA as part of LAT 2025 by Sii Polska.
Feel free to reach out via GitHub or email for any questions.
