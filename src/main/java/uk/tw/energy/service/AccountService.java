package uk.tw.energy.service;

import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Service class that manages the relationship between smart meters and their associated price plans.
 * This class maintains a mapping between smart meter IDs and their corresponding price plan IDs,
 * providing lookup functionality for these relationships.
 * <p>
 * SOLID Principles demonstrated:
 * - Single Responsibility: Class focuses only on account-price plan relationships
 * - Open/Closed: New account management features can be added without modifying existing code
 * - Dependency Inversion: Dependencies are injected through constructor
 * - Interface Segregation: Provides focused methods for specific relationship queries
 * - Liskov Substitution: Can be extended while maintaining contract
 * <p>
 * Design Patterns:
 * - Dependency Injection: Map of relationships is injected through constructor
 * - Service Pattern: Implements business logic layer in Spring architecture
 * - Repository Pattern: Provides abstraction for data access
 */
@Service
public class AccountService {

    private final Map<String, String> smartMeterToPricePlanAccounts;

    /**
     * Constructs a new AccountService with the specified smart meter to price plan mappings.
     * Demonstrates Dependency Injection pattern through constructor parameter, allowing for
     * flexible configuration and easier testing. This follows the Dependency Inversion
     * Principle by depending on abstractions rather than concrete implementations.
     *
     * @param smartMeterToPricePlanAccounts mapping of smart meter IDs to their price plan IDs
     */
    public AccountService(Map<String, String> smartMeterToPricePlanAccounts) {
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    /**
     * Retrieves the price plan ID associated with a given smart meter ID.
     * Follows Single Responsibility Principle by focusing only on relationship lookup.
     * This method provides a clean, focused interface for querying the account-plan
     * relationship, demonstrating both Interface Segregation and Single Responsibility principles.
     *
     * @param smartMeterId the ID of the smart meter to look up
     * @return the associated price plan ID, or null if no association exists
     */
    public String getPricePlanIdForSmartMeterId(String smartMeterId) {
        return smartMeterToPricePlanAccounts.get(smartMeterId);
    }
    public void assignPricePlanToSmartMeterId(String pricePlanId, String smartMeterId) {
        smartMeterToPricePlanAccounts.put(smartMeterId, pricePlanId);
    }
}
