To run the application code in Android Studio:
1. Clone the repository (if using version control): Use git clone <repository_url> in Android Studio's terminal.
2. Open or import the project: Open an existing project or import a downloaded one.
3. Wait for Gradle to sync: Gradle will automatically sync, fetching dependencies.
4. Check SDK and dependencies: Ensure necessary components are installed.
5. Configure settings (if required): Set up SDK version, build variants, or API keys.
6. Run the application: Click the play button to build and deploy the app.


The Application features graphs to showcase the real-time ECG Data shared to the device via Bluetooth. The app is designed to be very helpful to people who are away from the doctor and have the proper equipment to measure the ECG data.

Application Feature's -
• The application offers an easy-to-use interface and comprises seven plots in total:- I, II, III, AVR, AVL, and AVF, updated in real-time and 1 Median Complex plot of the ECG data.
• The Main screen of that Application shows the patients profile, Name, DOB, Blood Group, and Bed Number.
• In the bottom of the application, we have eight buttons designed, out of which currently only three are working; from button 1, we can see the real-time plot of Channel I, II and III. From button 2, a real-time plot of AVR, AVL, and AVF can be obtained, and from Button 3, the Median Complex of he complete ECG data can be seen.
• Button 1 and 2 divide each screen into 3 multi- section each for individual plot.

I have used Android Plot library to incorporate Graphs in the Application - https://halfhp.github.io/androidplot/

User Interface -
![image](https://github.com/VaishnavYash/BtechProject23/assets/103493455/6165be62-f476-41e2-ada0-d6157ac8ade2)
