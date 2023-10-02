# server-sksitara-rselemon
# Server 

## Rahel Selemon (rselemon) and Siddu Sitaraman (sksitara), pair-programming
## 15 hours to complete
## https://github.com/sks-codes/server-sksitara-rselemon



Design choices -- high level design of your program
Explain the relationships between classes/interfaces.
Discuss any specific data structures you used, why you created it, and other high level explanations.
Runtime/ space optimizations you made (if applicable).

At high level, our program has two essential sets of functionalities. The loadcsv, viewcsv, searchcsv endpoints all use the our parser and searcher to execute queries and other operations on local files. We had these endpoints share the loaded csv data by using a static csv variable in the server class and getter/setter methods for the variable accessible to all handlers in the handler package. Our broadband endpoint queries the ACS database and produces data on certain states and counties internet access. We had our broadband handler utilized a seperate ACS_API class that loads ACS data from the ACS API for a given query.

Tests -- Explain the testing suites that you implemented for your program and how each test ensures that a part of the program works. 
How to…
Run the tests you wrote/were provided
Build and run your program

