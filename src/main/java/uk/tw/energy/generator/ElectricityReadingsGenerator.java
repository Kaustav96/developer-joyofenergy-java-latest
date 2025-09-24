package uk.tw.energy.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import uk.tw.energy.domain.ElectricityReading;

/**
 * Generator class for creating simulated electricity readings.
 * <p>
 * SOLID Principles:
 * - Single Responsibility Principle: Class has single responsibility of generating electricity readings
 * - Open/Closed Principle: Class is open for extension but closed for modification
 * <p>
 * Design Patterns:
 * - Factory Pattern: Creates and returns a collection of ElectricityReading objects
 */
public class ElectricityReadingsGenerator {

    /**
     * Generates a specified number of electricity readings with random values.
     * Readings are created with timestamps in descending order and random gaussian values.
     *
     * @param number The number of readings to generate
     * @return List of ElectricityReading objects sorted by timestamp
     */
    public List<ElectricityReading> generate(int number) {
        List<ElectricityReading> readings = new ArrayList<>();
        Instant now = Instant.now();

        Random readingRandomiser = new Random();
        for (int i = 0; i < number; i++) {
            double positiveRandomValue = Math.abs(readingRandomiser.nextGaussian());
            BigDecimal randomReading = BigDecimal.valueOf(positiveRandomValue).setScale(4, RoundingMode.CEILING);
            ElectricityReading electricityReading = new ElectricityReading(now.minusSeconds(i * 10L), randomReading);
            readings.add(electricityReading);
        }

        readings.sort(Comparator.comparing(ElectricityReading::time));
        return readings;
    }
}
