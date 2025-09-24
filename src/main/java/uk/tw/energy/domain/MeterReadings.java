package uk.tw.energy.domain;

import java.util.List;

/**
 * Represents a collection of electricity readings for a specific smart meter.
 * <p>
 * This class follows:
 * - Single Responsibility Principle (SRP): Focused solely on storing meter reading data
 * - Immutability Pattern: Using Java Record feature for thread-safe, side-effect free data structure
 * - Value Object Pattern: Represents a collection that is equal based on all its properties
 * - Data Transfer Object (DTO) Pattern: Used to transfer meter reading data between system components
 *
 * @param smartMeterId        Unique identifier for the smart meter
 * @param electricityReadings List of electricity consumption readings for this meter
 */
public record MeterReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {}
