package uk.tw.energy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

/**
 * Service responsible for calculating electricity consumption costs across different price plans.
 * <p>
 * SOLID Principles:
 * - Single Responsibility: Focused solely on price plan calculations
 * - Open/Closed: New price plan calculations can be added without modifying existing code
 * <p>
 * Design Patterns:
 * - Strategy Pattern: Different price plans represent different pricing strategies
 * - Dependency Injection: Dependencies injected through constructor
 */
@Service
public class PricePlanService {

    private final List<PricePlan> pricePlans;
    private final MeterReadingService meterReadingService;

    /**
     * Constructs PricePlanService with required dependencies.
     *
     * @param pricePlans          List of available price plans
     * @param meterReadingService Service to retrieve meter readings
     */
    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(
            String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        return electricityReadings.map(readings -> pricePlans.stream()
                .collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(readings, t))));

    }

    /**
     * Calculates the total cost for given electricity readings under a specific price plan.
     *
     * @param electricityReadings List of electricity meter readings
     * @param pricePlan           Price plan to calculate cost against
     * @return Total cost for the consumption period
     */
    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        final BigDecimal averageReadingInKw = calculateAverageReading(electricityReadings);
        final BigDecimal usageTimeInHours = calculateUsageTimeInHours(electricityReadings);
        final BigDecimal energyConsumedInKwH = averageReadingInKw.multiply(usageTimeInHours);
        return energyConsumedInKwH.multiply(pricePlan.getUnitRate());
    }

    /**
     * Calculates the average electricity reading from a list of readings.
     *
     * @param electricityReadings List of electricity readings to average
     * @return Average reading value
     */
    private BigDecimal calculateAverageReading(List<ElectricityReading> electricityReadings) {
        BigDecimal summedReadings = electricityReadings.stream()
                .map(ElectricityReading::reading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator));

        return summedReadings.divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
    }

    /**
     * Calculates the total time period covered by the readings in hours.
     *
     * @param electricityReadings List of electricity readings
     * @return Time duration in hours between first and last reading
     */
    private BigDecimal calculateUsageTimeInHours(List<ElectricityReading> electricityReadings) {
        ElectricityReading first = electricityReadings.stream()
                .min(Comparator.comparing(ElectricityReading::time))
                .get();

        ElectricityReading last = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::time))
                .get();

        return BigDecimal.valueOf(Duration.between(first.time(), last.time()).getSeconds() / 3600.0);
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostForLastNDays(String smartMeterId, int days) {
        Instant now = Instant.now();
        Instant start = now.minus(days, ChronoUnit.DAYS);

        Optional<List<ElectricityReading>> readingsOpt = meterReadingService.getReadings(smartMeterId);

        if (readingsOpt.isEmpty()) {
            return Optional.empty();
        }

        List<ElectricityReading> filtered = readingsOpt.get().stream()
                .filter(r -> !r.time().isBefore(start) && !r.time().isAfter(now))
                .toList();

        if (filtered.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream()
                .collect(Collectors.toMap(
                        PricePlan::getPlanName,
                        plan -> calculateCost(filtered, plan)
                )));
    }
}
