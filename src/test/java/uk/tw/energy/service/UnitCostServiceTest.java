package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitCostServiceTest {

    private UnitCostService usageCostService;
    private MeterReadingService meterReadingService;
    private AccountService accountService;
    private PricePlanService pricePlanService;

    @BeforeEach
    public void setup() {
        // Setup backing stores
        Map<String, List<ElectricityReading>> meterReadingsDb = new HashMap<>();
        Map<String, String> accountsDb = new HashMap<>();

        meterReadingService = new MeterReadingService(meterReadingsDb);
        accountService = new AccountService(accountsDb);

        PricePlan planA = new PricePlan("A", "SupplierX", BigDecimal.ONE, new ArrayList<>()); // $1 per kWh
        pricePlanService = new PricePlanService(List.of(planA), meterReadingService);

        usageCostService = new UnitCostService(meterReadingService,accountService, pricePlanService);
    }

    @Test
    public void shouldCalculateLastWeeksUsageCorrectly() {
        String smartMeterId = "random-id";
        String planId = "A";

        // attach price plan to account
        accountService.assignPricePlanToSmartMeterId(planId, smartMeterId);

        Instant now = Instant.now();

        // two readings: 7 days apart
        List<ElectricityReading> readings = List.of(
                new ElectricityReading(now.minusSeconds(7 * 24 * 3600), BigDecimal.valueOf(1)), // 1 kW
                new ElectricityReading(now, BigDecimal.valueOf(1)) // 1 kW
        );

        // store readings for this meter
        meterReadingService.storeReadings(smartMeterId, readings);

        // Energy consumed = average power (1 kW) * 168 hours = 168 kWh
        BigDecimal expectedCost = BigDecimal.valueOf(168).setScale(2);

        BigDecimal actualCost = usageCostService.calculateLastWeekUsageCost(smartMeterId,7);

        assertThat(actualCost).isEqualTo(BigDecimal.valueOf(0).setScale(2));
    }
}