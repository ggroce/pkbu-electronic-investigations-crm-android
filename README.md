## PKBU_AndroidCaseMaintenance

This is the Android implementation of a mobile data entry and communication tool, created to assist case agents and forensic analysts throughout an investigation that involves electronic devices and digital information.  Uses Google Firebase for account access and data storage.  

### Implementation
Developers wishing to utilize the source must currently supply their own Google Firebase account details for accounts and data storage.  This includes Google Auth, Cloud Firestore, (used to store the data fields in JSON format), and a Firestore Storage implementation for storage of visual images.\
<br>
### Summary of functions:  
The application begins by having the user create an account.  After account creation, the user may create a new case, or view existing cases created by other investigators within the group.  Once a case has been entered, the user can begin entering electronic items into the case, filling in their pertinent details, and update them as necessary.  All entered information is populated throughout the group's view of the case in real time, due to Google Firestore Cloud.  This assists groups that as a team, are engaged in a real-time investigations such as a search warrant execution, and are physically separated throughout a location, but working towards a common goal.  

<br>
<i>Case view</i>
<img src="https://user-images.githubusercontent.com/25714007/86613214-df341500-bf76-11ea-97cd-93fd50828ce2.png" width="25%">
<br>
<i>Items entered into case, displayed as separate cards:</i>
<img src="https://user-images.githubusercontent.com/25714007/86613528-58cc0300-bf77-11ea-81da-6d53ca438a0b.png" width="25%">
<br>
<i>Item entry screens</i>
<img src="https://user-images.githubusercontent.com/25714007/86613828-ba8c6d00-bf77-11ea-9840-796789bf3e7c.png" width="25%"><img src="https://user-images.githubusercontent.com/25714007/86613844-bd875d80-bf77-11ea-8c76-af765678f2f0.png" width="25%">
<br>
<i>Sample of item search options</i>
<img src="https://user-images.githubusercontent.com/25714007/86613958-e7408480-bf77-11ea-947e-adf76d542d8e.png" width="25%">
