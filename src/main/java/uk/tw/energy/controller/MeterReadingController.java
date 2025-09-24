package uk.tw.energy.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

/**
 * REST controller that handles meter reading operations for smart meters.
 * Provides endpoints to store and retrieve electricity readings.
 * <p>
 * SOLID Principles demonstrated:
 * - Single Responsibility: Controller focused only on meter reading operations
 * - Open/Closed: New reading operations can be added without modifying existing code
 * - Liskov Substitution: Uses abstract ResponseEntity consistently
 * - Interface Segregation: Uses focused service interfaces
 * - Dependency Inversion: MeterReadingService is injected through constructor
 * <p>
 * Design Patterns:
 * - Dependency Injection: MeterReadingService is injected through constructor
 * - MVC Pattern: Part of Spring MVC architecture
 * - Repository Pattern: Delegates data operations to service layer
 */
@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    /**
     * Constructs a new MeterReadingController.
     * Demonstrates Dependency Injection pattern by receiving MeterReadingService through constructor.
     *
     * @param meterReadingService service for handling meter reading operations
     */
    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    /**
     * Stores electricity readings for a smart meter.
     * Validates input before storage to ensure data integrity.
     *
     * @param meterReadings the readings data to store, including smart meter ID and electricity readings
     * @return ResponseEntity with OK status if storage successful, or error status if validation fails
     */
    @PostMapping("/store")
    public ResponseEntity storeReadings(@RequestBody MeterReadings meterReadings) {
        if (!isMeterReadingsValid(meterReadings)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        return ResponseEntity.ok().build();
    }

    /**
     * Validates meter readings data ensuring both smart meter ID and readings list are present and non-empty.
     * Follows Single Responsibility Principle by focusing only on validation logic.
     *
     * @param meterReadings the readings data to validate
     * @return true if the readings data is valid, false otherwise
     */
    private boolean isMeterReadingsValid(MeterReadings meterReadings) {
        String smartMeterId = meterReadings.smartMeterId();
        List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
        return smartMeterId != null
                && !smartMeterId.isEmpty()
                && electricityReadings != null
                && !electricityReadings.isEmpty();
    }

    /**
     * Retrieves electricity readings for a given smart meter ID.
     * Demonstrates Repository pattern by delegating data retrieval to service layer.
     *
     * @param smartMeterId the ID of the smart meter to retrieve readings for
     * @return ResponseEntity containing the list of readings if found, or NOT_FOUND if no readings exist
     */
    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity readReadings(@PathVariable String smartMeterId) {
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        return readings.isPresent()
                ? ResponseEntity.ok(readings.get())
                : ResponseEntity.notFound().build();
    }
}
