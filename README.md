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

Location
- `LocationId` – _Int_
- `LocationName` – _String_

Working hours
- `WorkDayName` – _String_
- `StartTime` – _String_
- `EndTime` – _String_
- `LocationId` – _Int_ (a foreign key pointing  a location associated with it)

## Business requirements
### General statements
- The main use of this application is to show the user the operating hours of a specific business point (location).
- The user may open application with or without internet in the local country and the application should display information for the working hours for a specific business (location)
- User should be able to see if the location is open now, or closed, or closes within an hour
- Different labels should be displayed for different edge cases:
  - _"Open until {time}"_ – if the location is opened when user is viewing information
  - _"Open until {time}, reopens {next time}"_ – if the location is open now, but it may be closed within an hour
  - _"Opens again {next time}"_ – if the location is closed but will be opened in less than 24 hours
  - _"Opens {day} {time}"_ – if the location is closed and will not be opened in 24 hours
- The color indicator should be present on the location info
  - If the location is opened             – 🟢
  - If the location closes within an hour – 🟡
  - If the location is closed             – 🔴
- The time format should be the local either **24h** or **12h**

### Data format
- All operation hours should be grouped in one day. This means that if JSON file contains same day name but different hours of work they have to be displayed in one row.\
__For example__, instead of displaying:\
_Monday: 10am-12pm_\
_Monday: 5pm-9pm_\
The application should display:
_Monday: 10am-12pm, 5pm-9pm_
 
### Edge Cases
#### Single time per day
If there is only one time slot for the day the only one time segment should be displayed:
```json
{
  "day_of_week": "MON",
  "start_local_time": "09:00:00",
  "end_local_time": "17:00:00"
}
```
 
#### Multiple Time Segments per Day
If the JSON structure contains more than 1 section for the same date the time slots should be combined and should be displayed in a single section:
```json
{
  "day_of_week": "MON",
  "start_local_time": "09:00:00",
  "end_local_time": "12:00:00"
},
{
  "day_of_week": "MON",
  "start_local_time": "13:00:00",
  "end_local_time": "17:00:00"
}
```
For the example above the:
_Monday: 9am-12pm, 1pm-5pm_ should be displayed
 
#### Late-Night Hours
- Open Until Midnight
```json
{
  "day_of_week": "MON",
  "start_local_time": "09:00:00",
  "end_local_time": "24:00:00"
}
```
This JSON should be converted to "Open until midnight"
- Open Until Early Morning
Open until early morning means that the location opens one day and continues to operate at night the next day. The JSON would looks the following way:
```json
{
  "day_of_week": "MON",
  "start_local_time": "09:00:00",
  "end_local_time": "24:00:00"
},
{
  "day_of_week": "TUE",
  "start_local_time": "00:00:00",
  "end_local_time": "02:00:00"
}
```
 
#### Open 24 hours
The location may be opened 24 hours for the day if `start_local_date` is 00:00 and the `end_local_date` is 24:00. The "Open 24 hours" has to be displayed.
```json
{
  "day_of_week": "MON",
  "start_local_time": "00:00:00",
  "end_local_time": "24:00:00"
}
```
 
#### Closed Entire Day
```json
{
  "day_of_week": "MON",
  "start_local_time": "00:00:00",
  "end_local_time": "00:00:00"
}
```

# UI Design

# Test cases

# Contacts
Email: yuriisurzhykov@gmail.com
