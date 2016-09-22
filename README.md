# PoliticsLive
CSC 4998 Mobile Apps- Final Project

APP BEHAIVOR:
-The launching activity is MainActivity.
-If user has never opened app before, MainActivity redirects to LoginActivity to initialize polls.
-If app is already initiliazed, MainActivity loads the HomePage fragment, so this is the first page the user sees

DATABASES:
-There are 4 active local SQL database: Events, Candidates, Polls, ExitPolls, PoliticalParties, 


TODO ITEMS (in order of priority:
- App Notifications for Events (5 total right now), display the name, time, and channel of the event ON THE DATE OF THE EVENT.
- Allow user to turn on and off event notifications from the AppSettings Activity (Default behaivor = ON)
- Migrate all SQL data onto a hosted solution (TRY FIREBASE)
- Using Firebase, reimplement existing social features including User Profile, Vote Counts for each candidate, Ballot, Login, SignUp, Logout, etc.

SIGNING KEY:
- link: https://drive.google.com/open?id=0B2FELBPNoUJyZXBxUjlUeGVpLUU
- Key alias: PoliticsLiveKey
- Key password: zun3ukit
- key store password: zun3ukit