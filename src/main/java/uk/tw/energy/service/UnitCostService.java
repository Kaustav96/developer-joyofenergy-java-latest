package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.exception.InvalidPricePlanForSmartMeterIdException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UnitCostService {
    // calling the other services
    private MeterReadingService meterReadingService;
    private AccountService accountService;
    private PricePlanService pricePlanService;

    public UnitCostService(MeterReadingService meterReadingService, AccountService accountService,
                           PricePlanService pricePlanService){
        this.meterReadingService = meterReadingService;
        this.accountService = accountService;
        this.pricePlanService = pricePlanService;
    }

    public BigDecimal calculateLastWeekUsageCost(String smartMeterId, int noOfDays) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        if (pricePlanId == null) {
            throw new InvalidPricePlanForSmartMeterIdException(
                    "No price plan found for smart meter ID: " + smartMeterId);
        }

        Optional<Map<String, BigDecimal>> costsOpt =
                pricePlanService.getConsumptionCostForLastNDays(smartMeterId, noOfDays);

        if (costsOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<String, BigDecimal> costs = costsOpt.get();
        BigDecimal cost = costs.get(pricePlanId);

        if (cost == null) {
            throw new IllegalStateException("Cost not found for price plan: " + pricePlanId);
        }

        return cost.setScale(2, RoundingMode.HALF_UP);
    }

}
