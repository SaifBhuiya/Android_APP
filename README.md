Installing Android studio was straight forward. I connected my phone directly to my laptop instead of using an emulator. I had done a similar thing for a university project where I was trying to develop an AR app using unity Game Engine and C#. The app didnt end up as a phone app but the knowledge of how to connect and install from PC to phone using USB stayed. And also I knew how to access sensor data using the phone so I could match data that I was receiving from the code with the phone values. There is a certain number you dial that takes you to a screen that allows you to view the sensor values. That is where I 
found that Accelerometer and Gyroscope have X, Y  and Z values.

Initially the Layout section in Android studio was overwhelming due to there being so many different layouts components to choose from. I started by trying to use the design section to make the UI due to past experience with game Engines but it seemed less intuitive than the game Engines so opted for code instead. Later I realised it was much easier to move stuff in the Blueprint section than the design section. 

Making an App seemed closer in concept to what I have done in Game Development (UI elements and logic to control them around). I also had some experience in Java due to a university Course. 
It was my first time with SQLite, but it was pretty straight forward. I was initially confused about how I was gonna handle my view changes. After looking at some options I picked the one which seemed like the least complex way to do it. 

Every now and then I would have a few bugs which I had to go in and Fix. Some of the bugs gave me more insight about how systems work. Specially about what happens when an app is minimized or when an app is closed and re opened.

Adding chart was as easy as following a video. But then I had to go in and create a bunch of functions so that I could use my SQL data for chart. Faced a few issues trying to do that and had to improvise and implement different solutions depending on the type of sensor.

The most challenging part for me however was the part where I had to keep the app running in the background while the app was closed. A lot of systems had to be moved around and things had to be re implemented. Also there were multiple possible ways to do it, all of which were new to me. 

After some research I opted for WorkManager to handle the background execution (I read that it's supposed to be efficient). After implementing the whole system in WorkManager, I found out that the lowest amount of time for intervals is 15 mins (PeriodicWorkRequest).
So the data would be updated every 15 minutes instead of 5 mins. So I tried finding options which would allow me to keep my progress so far and solve the timing issue.
Then I landed on AlarmManager. AlarmManager is like setting an alarm at certain time intervals.
So I used AlarmManager to send a signal every 5 mins for WorkManager to activate(OnetimeWorkRequest) and update the database . This part was the worst for me. As these systems were completely new to me and I was bombarded with concepts and syntaxes. But slowly I crawled my way out, debugging and fixing systems until everything was working as intended.... or so I thought.  
Just when I thought I was done, I realized the app didnt work when the app was removed from recent apps. I tried multiple permission settings but couldnt get it to work. So finally I used Foreground service to try and fix that. 
Foreground service is basically a continuous execution (until stopped) and can run even when the phone is sleeping/ app is removed from recent apps. I could also do things like reading sensor data directly within the Foreground service. Working with it opened up multiple ways I could make the app interact and transfer data. 
After Implementing Foreground manager I found another bug. Using App inspector helpe me realize that the bug was related to how I implemented AlarmManager and so I went in and fixed that as well.

Working with so many different systems and how they interact was overwhelming at first but I do have a basic understanding of how they work now.


