package uk.tw.energy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    /*
    As an electricity consumer I want to be able to view my usage cost of the last week so that I can monitor my spending
Acceptance Criteria:
Case 1 -> happy Path -> Given I have a smart meter ID with price plan attached to it and usage data stored, when I request the usage cost then I am shown the correct cost of last week's usage
Case 2 -> Error path -> Given I have a smart meter ID without a price plan attached to it and usage data stored, when I request the usage cost then an error message is displayed
How to calculate usage cost
Unit of meter readings : kW (KiloWatt)
Unit of Time : Hour (h)
Unit of Energy Consumed : kW * Hour = kWh
Unit of Tariff : $ per kWh (ex 0.2 $ per kWh) -> unitRate of Price Plan

To calculate the usage cost for a duration (D) in which lets assume we have captured N electricity readings (er1,er2,er3....erN)

Average reading in KW = (er1.reading + er2.reading + ..... erN.Reading)/N
Usage time in hours = Duration(D) in hours
Energy consumed in kWh = average reading * usage time
Cost = tariff unit prices * energy consumed

API ->
GET -> /usageCostPerWeek/{SmartMeterId}
Create a usageCostService ->
1. calculateWeeklyCOst(String smartMeterId) -> return BigDecimal
2. error/exceptions for case 2
unitRate -> pricePlan.getUnitRate();
     */

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
