# 💉🩸 Personal insulin pump (Integrated system)
>An embedded system in an insulin pump used by diabetics to maintain blood glucose control.


- Table of Content
[ToC]

## :rocket: Introduction:

This work aims to define the operations of control software for an automated insulin pump which is used by diabetics to administer insulin.
It collects data from a blood sugar sensor and calculates the amount of insulin required to be injected.
The calculation is based on the rate of change of blood sugar levels, then it sends signals to a micro-pump to deliver the correct dose of insulin.
This is a safety-critical system as low blood sugars can lead to brain malfunctioning, coma and death; high-blood sugar levels have long-term consequences such as eye and kidney damage. 

### :computer: Hardware architecture:
![](https://i.imgur.com/l3inUnp.png)

The following parts are the ones developed in this work: 
* **Pump**
Pumps insulin from a reservoir to the needle assembly. It's in charge of deliver the correct amount of insulin computed by the controller to the needle assembly.
* **Controller**
Controls the entire system. This has two possible states: a run state and an error state. The latter state is triggered only when there are hardware issues.
* **Displays**
There are 3 displays. The first display displays system messages, the second one shows the last dose of insulin delivered and the last one shows the current clock time.
* **Clock**
Provides the controller with the current time.

The **needle assembly**, the **alarm** and the **sensor** are just simulated: they do not provide real information to the system controller. Therefore, they are developed as mockups. 
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
5.	The controller shall run a self-test program every one minute. This shall test for the conditions shown in Table 2 below. Otherwise, if the tests fail, the system transitions in the "error" state.

**Table 2: Error conditions for the insulin pump**

| Alarm condition | Explanation |
| -------- | -------- |
| Battery low | The voltage of the battery has fallen to less than 0.5V |
| Sensor failure | The self-test of the sugar sensor has resulted in an error |
| Pump failure | The self-test of the pump has resulted in an error |
| Delivery failure | It has not been possible to deliver the specified amount of insulin |
| Low insulin level | The level of insulin is low (indicating that the reservoir should be changed) |

For each error condition the user should manually solve the issue (eg. charging the battery, or fill the insulin reservoir)

6. There are 3 displays. The first display displays system messages, the second one shows the last dose of insulin delivered and the last one shows the current clock time
7.	The reservoir compartment is such that a reservoir holds a maximum of 100 ml of insulin. 
8.	At the beginning of each 24 hour period (indicated by clock =00:00:00), the cumulative dose of insulin delivered is reset to 0.
9.	The error conditions that should be detected and indicated by the system are shown in Table 1.
10.	A blood sugar sensor measures the current blood sugar reading in micrograms/millilitre. This is updated every 10 minutes and it is normally between 1 and 35 micrograms/millilitre.


The following sections are about the different scenarios regarding the system functionality.

## Scenarios

### Scenario 1: 

Sugar level falling (r2 < r1):

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured a blood sugar value, stored in r1.
The insulin pump device has no hardware issues (checked in the last 30 seconds).
**Normal**: Blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1), and states that r2 is less than r1 (r2<r1). Than it computes the dose of insulin to inject (compDose), which is actually 0 U/ml. Since the compDose is 0, the controller does not activate the needle assembly to inject insulin.
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred, i.e. one of the error in the Table 2.
In this case, the controller must block all the system functionality and the user has to manually solve the issue(s) (see Scenario 5).
**Other activities**: in the meantime, the battery is discharging; the user are living their normal life and their blood sugar level is changing.
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and if there is a r0 value, this is discarded, in order to get ready for the next blood sugar measurement.

### Scenario 2: 

Sugar level stable (r2 = r1):

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured a blood sugar value, stored in r1.
The insulin pump device has no hardware issues (checked in the last 30 seconds).
**Normal**: Blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1), and states that r2 is equal than r1 (r2=r1). Than it computes the dose of insulin to inject (compDose), which is actually 0 U/ml. Since the compDose is 0, the controller does not activate the needle assembly to inject insulin.
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred, i.e. one of the error in the Table 2.
In this case, the controller must block all the system functionality and the user has to manually solve the issue(s) (see Scenario 5).
**Other activities**: in the meantime, the battery is discharging; the user are living their normal life and their blood sugar level is changing.
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and if there is a r0 value, this is discarded, in order to get ready for the next blood sugar measurement.

### Scenario 3: 

Sugar level increasing and rate of increase decreasing((r2 – r1) < (r1 – r0)):

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured multiple blood sugar values, stored in r1 and r0. In particular r0 is the oldest measurement done. The insulin pump device has no hardware issues (checked in the last 30 seconds).
**Normal**: The blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1) and states that r2>r1, i.e the sugar level is increasing.
Since this level is increasing, the controller checks if the rate of increase is stable or it starts to decrease like in this scenario (r2 – r1) < (r1 – r0), so the dose of insulin to inject (compDose) is actually 0 U/ml. Since the compDose is 0, the controller does not activate the needle assembly to inject insulin.
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred, i.e. one of the error in the Table 2.
In this case, the controller must block all the system functionality and the user has to manually solve the issue(s) (see Scenario 5).
**Other activities**: in the meantime, the battery is discharging; the user are living their normal life and their blood sugar level is changing.
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

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured multiple blood sugar values, stored in r1 and r0. In particular r0 is the oldest measurement done. The insulin pump device has no hardware issues (checked in the last 30 seconds).
**Normal**: The blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1) and states that r2>r1, i.e the sugar level is increasing.
Since this level is increasing, the controller checks if the rate of increase is stable or it starts to decrease. In this scenario the rate of increase is stable or increasing ((r2 – r1) ≥ (r1 – r0)), so the dose of insulin to inject (compDose) is actually (r2 – r1)/4 U/ml rounded to the nearest integer and not equal to 0.
Since the compDose is not equal to 0 (result of rounded division ≠ 0), the controller activates the needle assembly to inject insulin.
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred, i.e. one of the error in the Table 2.
In this case, the controller must block all the system functionality and the user has to manually solve the issue(s) (see Scenario 5).
**Other activities**: in the meantime, the battery is discharging; the user are living their normal life and their blood sugar level is changing.
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and r0 is discarded, in order to get ready for the next blood sugar measurement.

### Scenario 5: 

 Sugar level increasing and rate of increase stable or increasing ((r2 – r1) ≥ (r1 – r0)) and result of rounded division = 0

**Initial assumption**: A user carries the personal insulin pump device. The device previously measured multiple blood sugar values, stored in r1 and r0. In particular r0 is the oldest measurement done. The insulin pump device has no hardware issues (checked in the last 30 seconds).
**Normal**: The blood sugar level is measured by a blood sugar sensor and stored in a value r2. After the measurement, the controller checks the previous value (r1): the controller makes a comparison between the current value (r2) and the previous one (r1) and states that r2>r1, i.e the sugar level is increasing.
Since this level is increasing, the controller checks if the rate of increase is stable or it starts to decrease. In this scenario the rate of increase is stable or increasing ((r2 – r1) ≥ (r1 – r0)), so the dose of insulin to inject (compDose) is actually (r2 – r1)/4 U/ml rounded to the nearest integer.
Since the compDose is equal to 0 (result of rounded division = 0), but the controller states an incresing sugar level, it activate the needly assembly to inject a fixed minimum dose (MinimumDose) of insulin.
**What can go wrong**: between the last hardware check (initial assumption) and the current blood sugar measurement it could have happened that a hardware issues occurred, i.e. one of the error in the Table 2.
In this case, the controller must block all the system functionality and the user has to manually solve the issue(s) (see Scenario 5).
**Other activities**: in the meantime, the battery is discharging; the user are living their normal life and their blood sugar level is changing.
**System state on completion**: the controller updates the r-values: the current measurement r2 become r1, r1 become r0, and r0 is discarded, in order to get ready for the next blood sugar measurement.
