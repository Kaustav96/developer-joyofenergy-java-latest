package uk.tw.energy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

/**
 * Service class responsible for managing electricity meter readings.
 * <p>
 * SOLID Principles:
 * - Single Responsibility Principle: Class handles only meter reading operations
 * - Open/Closed Principle: New storage implementations can be added without modifying existing code
 * <p>
 * Design Patterns:
 * - Dependency Injection: Dependencies are injected through constructor
 * - Repository Pattern: Abstracts data storage operations
 */
@Service
public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    /**
     * Retrieves electricity readings for a specific smart meter.
     *
     * @param smartMeterId the unique identifier of the smart meter
     * @return Optional containing list of readings if meter exists, empty Optional otherwise
     */
    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    /**
     * Stores electricity readings for a specific smart meter.
     * Creates a new list for the meter if it doesn't exist.
     *
     * @param smartMeterId        the unique identifier of the smart meter
     * @param electricityReadings list of electricity readings to store
     */
    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
        }
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
    }
}
