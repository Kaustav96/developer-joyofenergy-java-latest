package uk.tw.energy.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents an immutable snapshot of electricity consumption at a specific moment in time.
 *
 * This class follows:
 * - Single Responsibility Principle (SRP): Focused solely on representing electricity reading data
 * - Immutability Pattern: Using Java Record feature for thread-safe, side-effect free data structure
 * - Value Object Pattern: Represents a measurement value that is equal based on all its properties
 *
 * @param time The instant when the electricity reading was taken
 * @param reading The electricity consumption value in kilowatts (kW)
 */
public record ElectricityReading(Instant time, BigDecimal reading) {}
