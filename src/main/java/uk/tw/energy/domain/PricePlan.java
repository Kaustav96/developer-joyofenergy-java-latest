package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an energy price plan with base rates and peak time multipliers.
 * <p>
 * Design Patterns:
 * - Immutable Object Pattern: All fields are final and set through constructor
 * - Strategy Pattern: PeakTimeMultiplier encapsulates pricing calculation logic
 */
public class PricePlan {

    /**
     * The energy supplier providing this plan
     */
    private final String energySupplier;
    /**
     * Name of the price plan
     */
    private final String planName;
    /**
     * Base unit rate per kWh
     */
    private final BigDecimal unitRate;
    /**
     * List of peak time multipliers for different days
     */
    private final List<PeakTimeMultiplier> peakTimeMultipliers;

    /**
     * Constructs a new PricePlan with the specified parameters.
     *
     * @param planName            Name of the price plan
     * @param energySupplier      Name of the energy supplier
     * @param unitRate            Base unit rate per kWh
     * @param peakTimeMultipliers List of peak time multipliers
     */
    public PricePlan(
            String planName, String energySupplier, BigDecimal unitRate, List<PeakTimeMultiplier> peakTimeMultipliers) {
        this.planName = planName;
        this.energySupplier = energySupplier;
        this.unitRate = unitRate;
        this.peakTimeMultipliers = peakTimeMultipliers;
    }

    public String getEnergySupplier() {
        return energySupplier;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getUnitRate() {
        return unitRate;
    }

    /**
     * Calculates the price for a given date and time considering peak time multipliers.
     * Follows Single Responsibility Principle by focusing only on price calculation.
     *
     * @param dateTime The date and time for price calculation
     * @return The calculated price considering any applicable multipliers
     */
    public BigDecimal getPrice(LocalDateTime dateTime) {
        return peakTimeMultipliers.stream()
                .filter(multiplier -> multiplier.dayOfWeek.equals(dateTime.getDayOfWeek()))
                .findFirst()
                .map(multiplier -> unitRate.multiply(multiplier.multiplier))
                .orElse(unitRate);
    }

    /**
     * Inner class representing a peak time multiplier for a specific day of week.
     * Follows Interface Segregation Principle by keeping the multiplier logic separate.
     */
    static class PeakTimeMultiplier {

        DayOfWeek dayOfWeek;
        BigDecimal multiplier;

        public PeakTimeMultiplier(DayOfWeek dayOfWeek, BigDecimal multiplier) {
            this.dayOfWeek = dayOfWeek;
            this.multiplier = multiplier;
        }
    }
}
