# 💉🩸 Personal insulin pump (Integrated system)
>An embedded system in an insulin pump used by diabetics to maintain blood glucose control.


## :rocket: Introduction:

This work aims to define the operations of control software for an automated insulin pump which is used by diabetics to administer insulin.
It collects data from a blood sugar sensor and calculates the amount of insulin required to be injected.
The calculation is based on the rate of change of blood sugar levels, then it sends signals to a micro-pump to deliver the correct dose of insulin.
This is a safety-critical system as low blood sugars can lead to brain malfunctioning, coma and death; high-blood sugar levels have long-term consequences such as eye and kidney damage. 

### :computer: Hardware architecture:
![](https://i.imgur.com/l3inUnp.png)

The following parts are the ones developed in this work: 
* **LD_InsulinPump.AppController**
Controls the entire system. This has two possible states: a run state and an error state. The latter state is triggered only when there are hardware issues.
* **Displays**
There are 4 displays. The first display displays system messages/hardware issues; the second one shows the last dose of insulin computed. The third one shows the the last blood sugar measurement and the last one shows the current clock time.
* **Clock**
Provides the controller with the current time.

The **needle assembly**, the **pump**, the **alarm** and the **sensor** are just simulated: they do not provide real information to the system controller. In addiction, in order to provide a prototype, **hardware issues** are simulated. Therefore, they are all developed as mock-ups. 
In particular, the alarm component is simulated only using error messages displayed on the display. 

### :memo: Requirements

1. 	The dose of insulin to be delivered shall be computed by measuring the current level of blood sugar, comparing this to a previous measured level and computing the required dose as described in 3. below. 
2. 	The system shall measure the level of blood sugar and deliver insulin every 10 minutes.
3. The amount of insulin to be delivered shall be computed by the controller according to the current sugar reading as measured by the sensor:
	* If the reading is below the safe minimum, no insulin shall be delivered. See table 1 below.
	* If the reading is within the safe zone, then insulin is only delivered if the level of sugar is rising and the rate of increase of sugar level is increasing. The amount of insulin required is defined in the table 1 below.
	* If the reading is above the recommended level, insulin is delivered unless the level of blood sugar is falling and the rate of decrease of the blood sugar level is increasing. The amount of insulin required in this case is defined in the table 1 below.	
	* The amount of insulin actually delivered may be different from the computed dose as various safety constraints are included in the system. There is a limit on the maximum dose to be delivered in a single injection and a limit on the total cumulative dose in a single day.

**Table 1: conditions/actions for the insulin pump**
The following table is based on the last 3 blood sugar level measurements: in particular r0 indicates the oldest of the three measurements, r1 is the intermediate measurement and finally we have r2 which is the most recent measurement.

| Condition         | Action |
| --------          | -------- |
| a) Sugar level falling (r2 < r1)  | CompDose = 0 |
| b) Sugar level stable (r2 = r1) | CompDose = 0 |
| c) Sugar level increasing and rate of increase decreasing((r2 – r1) < (r1 – r0)) | CompDose = 0 |
| d) Sugar level increasing and rate of increase stable or increasing ((r2 – r1) ≥ (r1 – r0)) | CompDose = round ((r2 – r1)/4)
| e) If rounded result = 0 | CompDose = MinimumDose|


4.	Under normal operating conditions, the system is in the "running" state.
5.	The controller shall run a self-test program every one minute. The result of the test is used to compute the new state:  if the test is 'true' (in case of an error) the new state become 'error', otherwise, if the test is 'false' (the device has not issues) the state remains 'running'. If the new state is 'error' (hardware issues) the system is suspended.
6. There are 4 displays. The first display displays system messages/hardware issues; the second one shows the last dose of insulin computed. The third one shows the the last blood sugar measurement and the last one shows the current clock time.
7.	At the beginning of each 24 hour period, the cumulative dose of insulin delivered is reset to 0.
8.	A blood sugar sensor measures the current blood sugar reading in micrograms/millilitre. This is updated every 10 minutes and it is normally between 1 and 35 micrograms/millilitre.

The following sections are about the different scenarios regarding the system functionality.

## Scenarios

### Scenario 1: 

Sugar level falling (r2 < r1):

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured a blood sugar value, stored in r1.
The insulin pump device has no hardware issues (checked in the last minute).    
**Normal**: Blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1), and states that r2 is less than r1 (r2<r1). Than it computes the dose of insulin to inject (compDose), which is actually 0 U/ml. Since the compDose is 0, the controller does not activate the needle assembly to inject insulin.  
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred. In this case, the controller must block all the system functionality.  
**Other activities**: in the meantime the user are living their normal life and their blood sugar level is changing.  
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and if there is a r0 value, this is discarded, in order to get ready for the next blood sugar measurement.

### Scenario 2: 

Sugar level stable (r2 = r1):

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured a blood sugar value, stored in r1.
The insulin pump device has no hardware issues (checked in the last minute).  
 **Normal**: Blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1), and states that r2 is equal than r1 (r2=r1). Than it computes the dose of insulin to inject (compDose), which is actually 0 U/ml. Since the compDose is 0, the controller does not activate the needle assembly to inject insulin.  
 **What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred. In this case, the controller must block all the system functionality.  
 **Other activities**: in the meantime the user are living their normal life and their blood sugar level is changing.  
 **System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and if there is a r0 value, this is discarded, in order to get ready for the next blood sugar measurement.  

### Scenario 3: 

Sugar level increasing and rate of increase decreasing((r2 – r1) < (r1 – r0)):

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured multiple blood sugar values, stored in r1 and r0. In particular r0 is the oldest measurement done. The insulin pump device has no hardware issues (checked in the last minute).  
**Normal**: The blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1) and states that r2>r1, i.e the sugar level is increasing.
The controller states the rate of increase starts to decrease: (r2 – r1) < (r1 – r0). So the dose of insulin to inject (compDose) is actually 0 U/ml. Since the compDose is 0, the controller does not activate the needle assembly to inject insulin.  
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred. In this case, the controller must block all the system functionality.  
**Other activities**: in the meantime the user are living their normal life and their blood sugar level is changing.  
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and r0 is discarded, in order to get ready for the next blood sugar measurement.   

### Scenario 4: 

Sugar level increasing and rate of increase stable or increasing ((r2 – r1) ≥ (r1 – r0)) and result of rounded division ≠ 0

r2 = 35  
r1 = 34  
r0 = 33  

35-34= 1 >= 34-33 = 1  
1/4 = rounded(0.25) = 0 --> minDose = 1  

r2 = 35  
r1 = 1  
r0 = 0  

35-1=34 >= 1-0 = 1  

34/4 = rounded(8.5) = 9  

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured multiple blood sugar values, stored in r1 and r0. In particular r0 is the oldest measurement done. The insulin pump device has no hardware issues (checked in the last minute).  
**Normal**: The blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1) and states that r2>r1, i.e the sugar level is increasing.  
Since this level is increasing, the controller checks if the rate of increase is stable or it starts to decrease. In this scenario the rate of increase is stable or increasing ((r2 – r1) ≥ (r1 – r0)), so the dose of insulin to inject (compDose) is actually (r2 – r1)/4 U/ml rounded to the nearest integer and not equal to 0.
Since the compDose is not equal to 0 (result of rounded division ≠ 0), the controller activates the needle assembly to inject insulin.  
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred. In this case, the controller must block all the system functionality.  
**Other activities**: in the meantime the user are living their normal life and their blood sugar level is changing.  
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and r0 is discarded, in order to get ready for the next blood sugar measurement.  

### Scenario 5: 

Sugar level increasing and rate of increase stable or increasing ((r2 – r1) ≥ (r1 – r0)) and result of rounded division = 0

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured multiple blood sugar values, stored in r1 and r0. In particular r0 is the oldest measurement done. The insulin pump device has no hardware issues (checked in the last minute).  
**Normal**: The blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1) and states that r2>r1, i.e the sugar level is increasing.
Since this level is increasing, the controller checks if the rate of increase is stable or it starts to decrease. In this scenario the rate of increase is stable or increasing ((r2 – r1) ≥ (r1 – r0)), so the dose of insulin to inject (compDose) is actually (r2 – r1)/4 U/ml rounded to the nearest integer.
Since the compDose is equal to 0 (result of rounded division = 0), but the controller states an incresing sugar level, it activate the needly assembly to inject a fixed minimum dose (MinimumDose) of insulin.  
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred, i.e. one of the error in the Table 2. In this case, the controller must block all the system functionality.  
**Other activities**: in the meantime the user are living their normal life and their blood sugar level is changing.  
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and r0 is discarded, in order to get ready for the next blood sugar measurement.  

### Scenario 6: 

Hardware issues during the self-test or right before the blood sugar measurement, the insulin gathering, or the injection. 

**Initial assumption**: A user carries the personal insulin pump device. The device could have previously measured multiple blood sugar values. 
The insulin pump device has one or more hardware issues but the controller is not aware of it yet.  
**Normal**: During the self-test or right before the blood sugar measurement, the insulin gathering or the injection, the controller makes a hardware test.  
The controller states that there is a hardware issue, so it must stop all the current operations sending an error message to the user. The hardware test result is returned to the controller (is 'true' when there is an error).  
**Other activities**: in the meantime the user are living their normal life and their blood sugar level is changing.  
**System state on completion**: the controller has sent an error message to the user and the system is suspended.  

## Diagrams

### Use case diagram: 
![](https://i.imgur.com/RMIAatn.png)

### Activity diagram:
![](https://i.imgur.com/Ak502mU.jpg)

### Class diagram:
![](https://i.imgur.com/rwrib1t.png)

### Sequence diagrams:
General:
![](https://i.imgur.com/Qjk0LV4.png)

AppController.computeInsulinToInject:
![](https://i.imgur.com/u0w9IkX.png)

## Test cases

Regarding the requirements presented above, these components were developed:
* Controller: responsible for the computation of insulin to inject based on blood sugar level measurements.
* Displays: developed using Spring. The displays are rendered in a webpage, where each section shows a display. In particular, as mentioned before, there are four different displays: the first display shows system messages/hardware issues; the second one shows the last dose of insulin computed. The third one shows the the last blood sugar measurement and the last one shows the current clock time.
* Sensor, pump and needle are mock-ups: they provide random values.

The aim of this project is to provide a demo of the insulin pump device. 
In order to see all the software functionalities, the minutes reported in the requirements for hardware test and blood sugar level measurement have been transformed into seconds. The hardware test runs every second, and the blood sugar level test every 10 seconds.

Since a part of the view is developed using JavaScript, Selenium was used to perform acceptance tests. 
It was chosen because of Spring conflicts during configuration and JavaScript issues. Also, the suggested JWebUnit framework is no longer updated and no support is provided for newer JavaScript features, such as classes and object declaration.

### Unit tests:

By default IntelliJ IDEA uses Gradle to run JUnit tests. In order to run the JUnit tests, it's required to change IDE configuration as reported below:
* Go to "File->Settings->Build, execution, deployment->Build tools->Gradle"
* Search for "Run test using:"
* Select "IntelliJ IDEA"
* Click "OK"

To run JUnit tests, right click on `"LD_insulinPump"` package (note the lowercase 'i') located in `"src/test/java"` and select `"Run Tests in 'LD_insulinPump'"`. 

These tests will require at least 20 seconds, because `Thread.sleep()` was used to simulate waiting.

To view test coverage, follow the below configuration:
* Go to "Run->Edit Configurations..."
* Choose the last runned configuration described before. It should be already selected and named `"LD_insulinPump in InsulinPump.test"` (note different 'i's)
* Go to "Code Coverage" tab
* In "Packages and classes to inclune in coverage data" make sure to have two entries: `"LD_InsulinPump.\*"` and `"LD_InsulinPump_Mock.\*"` (note the capital 'I's)
* Also make sure that "Enable coverage in test folders" is selected
* Click "Ok"

To run coverage on tests, right click on `"LD_insulinPump"` package (note the lowercase 'i') located in `"src/test/java"` and select `"Run Tests in 'LD_insulinPump' with Coverage"`.

The results are presented below.

![](https://i.imgur.com/7vA72RL.png)

![](https://i.imgur.com/qPBKVaP.png)

![](https://i.imgur.com/z3o5FOj.png)

The overall test coverage is 97% on lines and 83% on classes: they are not 100% because there are some classes or methods that could not be tested, e.g. 'main' method (`ServingWebContentApplication`), `WebSocketConfig` or configuration files. 
Apart from these cases, all the system was tested.

To view an accurate report of test coverage, see `"ReportCoverage"` folder following the path: `"Implementation + JUnit Tests/ReportCoverage"` 
and open `index.html` file.

### Acceptance tests:

![](https://i.imgur.com/CqDfUY2.png)

To run acceptance tests, the correct driver is required and it changes based on the used browser.

Google Chrome browser is recommended to run acceptance tests, therefore the correct chromeDriver driver is required. 
To download the chromeDriver driver based on the installed Chrome version, visit:  https://chromedriver.chromium.org/downloads. Choose the correct Chrome installed version and OS.

Place the downloaded driver in the following folder: `"Acceptance Test (Selenium)/src/test/resources" `(replacing existing file if asked).
It may be necessary to correct the file name "chromedriver" in the first instruction of `setupWebdriverChromeDriver()` replacing the existing filename (chromedriver) with the one downloaded:

>System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/**chromedriver**"); 


First, open in a project or in a command line the main application located in `"Implementation + JUnit"`, either by directly running through IntelliJ IDEA
```
test/src/main/java/LD_InsulinPump/ServingWebContentApplication.java
```
or using 
```
./gradlew bootRun
```
While the main application is running, but not opened in a browser yet, open the Acceptance test `"Acceptance Test (Selenium)"` in a separate project using IntelliJ IDEA. Running a test located in 
```
/src/test/java/TestingImplementation.java
```
will open the Chrome browser with an annotation indicating that it's under testing, like in the picture depicted above.

The tests check if the page behaves correctly: 
* initial booting;
* blood sugar measurement and computed dose of insulin change every ten seconds;
* in case of hardware issues, the view is updated
* reboot button in case of hardware issues
