# Mortgage Calculator
 project to implement a mortgage calculator as a native Android app.
 
### Design
1. **Calculation View**: allows user to provide property and loan data, do the calculation, and optionally save it.
2. **Map View**: shows all saved mortgage calculations on the Map.

 The navigation between the two views are implemented using Navigation Drawer.
 
### Features
1. A **New** button is provided for the user to start a new mortgage calculation. 
2. User has an option to **save** a mortgage calculation. (If the address is invalid, the user cannot save the calculation.)
3. User is able to navigate through the map and **browse mortgage markers**.
4. A **dialog with the mortgage information** is shown when user taps on a specific marker.
    *  A **Delete** button allows user to remove this mortgage calculation.
    *  An **Edit** button brings the user to the Calculation View to make changes.

 **Note**:  Saved data is persisted in SQLite Database. Uninstalling the app will result in loss of user data.
 
#### Formula for mortgage calculation can be found in the link below,
 http://www.wikihow.com/Calculate-Mortgage-Payments
 
 
#### Thank you note
 to implement a dialog with a Delete and an Edit button, when user tap on a specific marker, since this is not a default behaviour supported by Google Maps Android v2 API. I have implemented a solution(hack) as posted on the StackOverFlow thread below,

http://stackoverflow.com/questions/14123243/google-maps-android-api-v2-interactive-infowindow-like-in-original-android-go/15040761#15040761
