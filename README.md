# About

Purs-Android is an android version of the test application which is concidered as a "Take Home Assignment"

# Requirements
## Data Layer

### Connection
- The application must have connection to the internet to retrieve the working hours
- If no connection, the error should be displayed notifying user that he has no connection to the internet
- If connection lost during request, the error should be displayed notifying a user that connection to the endpoint failed
- If there is no connection or the connection is lost while retrieving data from a remote computer, the cached data, if any, should be displayed

### Cache
- When application successfully retrieved the data from remote that data should be persisted on the device for further usage if needed
- Every time user opens application the cache should be updated in order to keep data fresh

### Data format
#### Cloud
The JSON structure contains location structure so the DTO for **location** should be created with the following fields
- Location name – _String_
- List of working days – _Collection_

The **working day** structure should contain the following fields:
- Day of week – _String_
- Start local time – _String_
- End local time  – _String_

#### Cache
The cached structure should be similar to the cloud. There should be 2 entities: **location** and **working hours**. The location structure should contain location id which can be an integer, and the location name which is the String.

`Location`
- LocationId – _Int_
- LocationName – _String_

`Working hours`
- Work day name – _String_
- Working hours – _String_ (all working hours can be persisted in single sting separated with comma)
- LocationId – _Int_ (a foreign key pointing  a location associated with it)

# UI Design

# Test cases

# Contacts
Email: yuriisurzhykov@gmail.com
