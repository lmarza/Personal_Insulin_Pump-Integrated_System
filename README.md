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

| Condition         | Action |
| --------          | -------- |
| Sugar level falling (r2 < r1)  | CompDose = 0 |
| Sugar level stable (r2 = r1) | CompDose = 0 |
| Sugar level increasing and rate of increase decreasing((r2 – r1) < (r1 – r0)) | CompDose = 0 |
| Sugar level increasing and rate of increase stable or increasing ((r2 – r1) ≥ (r1 – r0)) | CompDose = round ((r2 – r1)/4)
| If rounded result = 0 | CompDose = MinimumDose|


4.	Under normal operating conditions, the system is in the "running" state.
5.	The controller shall run a self-test program every 30 seconds. This shall test for the conditions shown in Table 2 below. Otherwise, if the tests fail, the system transitions in the "error" state.

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




The following sections are about the different scenarios regarding the system functionality.

## Scenarios

### Scenario 1: to do
