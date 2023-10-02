# server-sksitara-rselemon
# Server 

## Rahel Selemon (rselemon) and Siddu Sitaraman (sksitara), pair-programming
## 15 hours to complete
## https://github.com/sks-codes/server-sksitara-rselemon



Design choices

At high level, our program has two essential sets of functionalities. The loadcsv, viewcsv, searchcsv endpoints all use the our parser and searcher to execute queries and other operations on local files. We had these endpoints share the loaded csv data by using a static csv variable in the server class and getter/setter methods for the variable accessible to all handlers in the handler package. Our broadband endpoint queries the ACS database and produces data on certain states and counties internet access. We had our broadband handler utilized a seperate ACS_API class that loads ACS data from the ACS API for a given query.


Tests
We made tests that queried each of our handlers with valid queries and also covered the error functionalities and error messages. We also included integration testing for loadcsv, viewcsv, and searchcsv's combined functionalities as well as the broadband and ACS data retrieving classes since they work in tandem to produce filtered ACS data. Since ACS requires census API, we also created a mock data class so that the tests do not need to repeatedly query the census API. These testing classes can all be found in the testing package in our code.

A Note on code organization and running our code:
All of our code is in the serverCode director, while the README is placed outside for a convenient overview. To run our code and tests in IntelliJ, please select serverCode as a project since it is the directory containing the code.
