![Super linter](https://github.com/yuriisurzhykov/Purs-Android/actions/workflows/android_lint_checker.yaml/badge.svg)
![Unit tests](https://github.com/yuriisurzhykov/Purs-Android/actions/workflows/android_tests_run.yaml/badge.svg)

# About

Purs-Android is an android version of the test application which is concidered as a "Take Home
Assignment"
<details>
  <summary>Requirements</summary>

# Requirements

## Data Layer

### Connection

- The application must have connection to the internet to retrieve the working hours
- If no connection, the error should be displayed notifying user that he has no connection to the
  internet
- If connection lost during request, the error should be displayed notifying a user that connection
  to the endpoint failed
- If there is no connection or the connection is lost while retrieving data from a remote computer,
  the cached data, if any, should be displayed

### Cache

- When application successfully retrieved the data from remote that data should be persisted on the
  device for further usage if needed
- Every time user opens application the cache should be updated in order to keep data fresh

### Data format

#### Cloud

The JSON structure contains location structure so the DTO for **location** should be created with
the following fields

- Location name –_String_
- List of working days –_Collection_

The **working day** structure should contain the following fields:

- Day of week –_String_
- Start local time –_String_
- End local time –_String_

#### Cache

The cached structure should be similar to the cloud. There should be 2 entities: **location** and *
*working hours**. The location structure should contain location id which can be an integer, and the
location name which is the String.

Location

- `LocationId` – _Int_
- `LocationName` – _String_

Working hours

- `WorkDayName` –_String_
- `StartTime` –_String_
- `EndTime` –_String_
- `LocationId` –_Int_ (a foreign key pointing a location associated with it)

## Business requirements

### General statements

- The main use of this application is to show the user the operating hours of a specific business
  point (location).
- The user may open application with or without internet in the local country and the application
  should display information for the working hours for a specific business (location)
- User should be able to see if the location is open now, or closed, or closes within an hour
- Different labels should be displayed for different edge cases:
    - _"Open until {time}"_ – if the location is opened when user is viewing information
    - _"Open until {time}, reopens {next time}"_– if the location is open now, but it may be closed
      within an hour
    - _"Opens again {next time}"_ – if the location is closed but will be opened in less than 24
      hours
    - _"Opens {day} {time}"_ – if the location is closed and will not be opened in 24 hours
- The color indicator should be present on the location info
    - If the location is opened – 🟢
    - If the location closes within an hour – 🟡
    - If the location is closed – 🔴
- The time format should be the local either **24h** or **12h**

### Data format

- All operation hours should be grouped in one day. This means that if JSON file contains same day
  name but different hours of work they have to be displayed in one row.\
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

If the JSON structure contains more than 1 section for the same date the time slots should be
combined and should be displayed in a single section:

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
  Open until early morning means that the location opens one day and continues to operate at night
  the next day. The JSON would looks the following way:

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

> [!NOTE]  
> This case only works if the work time is continuous, that is, there is no break between the end of
> one day's work and the start of another day. In this case 2am time belongs to Monday and it counts
> that Tuesday is not working day and should be displayed as closed the whole day unless no other
> working hours for this particular day.

#### Open 24 hours

The location may be opened 24 hours for the day if `start_local_date` is 00:00 and
the `end_local_date` is 24:00. The "Open 24 hours" has to be displayed.

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

</details>

<details>
  <summary> UI Design </summary>

# UI Design

The primary source of design requirements is
the [Figma document](https://www.figma.com/file/5BXJxCRmeOCeWyW9D0ivc7/Mobile-Take-home?type=design&node-id=0%3A1&mode=design&t=esTJOvM76vo0a121-1)

### Background

The background of application screen is
the [image](https://lh3.googleusercontent.com/p/AF1QipNvaaR6eoBC7I48N_-ROU30qsi_h2Sf5eQRxWtr=s1360-w1360-h1020).
There is no specific requirements, so it can either be used as asset or may be downloaded for user.

#### Decision:

In order to reduce the load on the network and avoid unnecessary calls to the network, it would be
better to use an image as an asset croped for different screen sizes.

### Location selection

In the example JSON structure the only one location is available, but to make things more flexible
and scalable it would be better if we would open selection screen in case of multiple location
available. So the logic should be the following:

- If there is only one location in the structure, then a screen with details by working hours
  immediately opens.
- If there is multiple locations the selection screen should be displayed.
- If no location received the dialog should appear to notify user about the failure

### Location screen

Components:

- Title: The title is the location name. Should have the `Title 1` style. Aligned to the top of
  parent and stretched to the width of screen.
- Working hours dropdown menu.
    - Aligned to the bottom of Title.
    - Displayes "Open ..." label based on
      the [requirements](https://github.com/yuriisurzhykov/Purs-Android/tree/003-create-a-detailed-description-for-ui-layer?tab=readme-ov-file#general-statements)
    - Color bullet indicator to visualise the current status of location
    - "See more hours" label to hint the user that more hours available to see

### Working hours selection

- When user clicks the dropdown menu the previous content should remain unchanged but menu must
  drops down.
- Working days should be aligned the following way:
    - Name of the day aligned to the right
    - Hours aligned to the left. If more than one time slot available for the day, it should appear
      right under the first time occurence.
- It's better to animate dropdown effect to make the UI smooth

## User flow

1. App Launch:
   The app starts, and the user sees a loading screen or the main screen.
2. Location Selection Screen:
   After loading, the user is presented with a screen to select a location from a list of available
   locations.
3. Location Selection:
   The user selects a location from the list.
   Upon selection, the app navigates to the detailed working hours screen for the chosen location.
4. Working Hours Screen:
   On this screen, the user sees the location name and its working hours.
   The user can navigate back to the location selection screen to choose another location.

### Visualization of User Flow

<img src="https://github.com/yuriisurzhykov/Purs-Android/assets/44873047/0359dacb-0c88-4239-b2d3-f2b75f3355ed" alt="drawing" width="350"/>

### Location selection screen

#### UI Elements

- Navigation Bar/App Bar with the title "Select Location".
- List of locations (List in SwiftUI, LazyColumn in Jetpack Compose).
- Loading indicator (ProgressView in SwiftUI, CircularProgressIndicator in Jetpack Compose) while
  data is being loaded.
- Each list item should be styled as a card (CardView) with the location name and an arrow
  indicating navigation to the detail screen.

#### Actions

When a list item is tapped, the app navigates to the detailed working hours screen for the selected
location.
</details>

<details>
  <summary>Architecture design</summary>

# Architecture design

## Multimodule Structure

A multimodule architecture allows splitting the project into independent modules, improving
maintainability, testability, and build speed. The proposed structure:

- **core:** This module contains abstract components such as dispatchers, mapper interfaces and
  everything that can be shared between different modules.
- **app:** The main application module that ties together all other modules.
- **data:** The module for managing data (cloud and cache).
- **domain:** The module for business logic and use cases.
- **presentation:** The module for UI and ViewModel.

## Module Structure

### app Module

The main entry point of the application.
Dependencies on other modules (data, domain, presentation).
Dagger Hilt configurations for dependency injection.

### data Module

Submodules:

- **cloud:** Handling network requests (Ktor or Retrofit).
- **cache:** Handling database operations (Room).
- **Repository:** Combining data from cloud and cache (should not be a separate module, it can be
  located in main source set)

### domain Module

#### Use cases

Business logic and data formatting

- Use case to build proper workdays list
- Use case to format date and time
- Use case to build current working day details (have to be triggered every minute to keep the
  current information up to date for the user)

#### Entities

Business data models.

Business layour have to contain 3 structures:

- `Location`
    - _Location name_
    - _List of workdays_ (always 7 items length)
- `WorkDay`
    - _WorkingHour_ (might be a list of strings or Empty if no working hours fetched from cloud for
      the day)
- `CurrentWorkDay`
    - _Open status_: { OpenUntil(time), ClosesWithinHour(next open time), ClosedOpensNextDay(open
      time), Closed(next open day, next open hours) }

### presentation Module

- ViewModel: Managing UI state.
- UI: User interface components (SwiftUI for iOS and Jetpack Compose for Android).

</details>

<details>
  <summary> Technology stack </summary>

# Technology stack

## Fetching data from cloud

The application must talk to the server to receive location details. The most advanced libraries for
working with the network are:

- [Ktor](https://ktor.io/docs/client-create-multiplatform-application.html)
- [Retrofit](https://square.github.io/retrofit/)

When choosing between these two libraries, preference is given to `Retrofit` due to its simplicity
and ease of configuration in a native android project.

## Data persistence

For data persistence there are a bunch of libraries either SQL or NoSQL. The most popular libaries
for data persistence for native android application are the following:

- [Room](https://developer.android.com/training/data-storage/room) – Library built on top of SQLite
- [Realm](https://www.mongodb.com/docs/atlas/device-sdks/sdk/kotlin/install/#std-label-kotlin-install-android) –
  NoSQL database
- [SqlDelight](https://github.com/cashapp/sqldelight)

Choosing between these libraries the easiest and the fast-to-implement solution would be Android
Room so the **decision** is to take `Android Room` to cache the cloud data

## Concurrency

Taking into account that the Application is an Android app which will be written fully in
Kotlin, [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) will be used for
the concurrency.

## UI Framework

The requirements for the application is to write UI
using [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation) so
the `Compose` will be used for the UI part of application.
</details>

<details>
  <summary>Build application locally</summary>

### Create a keystore file
(if you want to make a debug build, you may skip this section)

First of all you need to generate a keystore file with .jks extension
### Create signing.properties file in project
After you created a keystore file you have to create a `signing.properties` file in root folder of the project. The content of the file should be the following:
```
keystoreFile=purs_android_key.jks
keystorePassword=Purs2024
keyAlias=purs
keyPassword=Purs2024
```
**NOTE:** You can leave all variables empty if you just want to make a debug build
</details>

<details>
  <summary>TODO Next</summary>

## What TO DO next
Several enhancements are needed to finish the task:
- [ ] Update the current location status periodically to keep information up to date for the user
- [ ] Fix expand arrow animation which turns wrong angle if user clicks too fast
- [ ] Blur the background when user expand the location hours section
- [ ] Edge-to-edge stoped working properly, need to fix it
- [ ] Fix working hours alignment on expandable section
</details>

# Contacts

Email: yuriisurzhykov@gmail.com
