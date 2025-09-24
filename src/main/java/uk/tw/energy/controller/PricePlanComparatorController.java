package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

/**
 * REST controller that handles price plan comparison operations and recommendations
 * for smart meters. Provides endpoints to compare costs across different price plans
 * and recommend the cheapest options.
 *
 * SOLID Principles demonstrated:
 * - Single Responsibility: Controller focused only on price plan comparison operations
 * - Open/Closed: New price plan comparison features can be added without modifying existing code
 * - Interface Segregation: Uses focused service interfaces
 * - Dependency Inversion: Dependencies are injected through constructor
 *
 * Design Patterns:
 * - Strategy Pattern: Different price plan calculations can be implemented
 * - Dependency Injection: Services are injected through constructor
 * - MVC Pattern: Part of Spring MVC architecture
 */
@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    public static final String PRICE_PLAN_ID_KEY = "pricePlanId";
    public static final String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    /**
     * Constructs a new PricePlanComparatorController.
     * Demonstrates Dependency Injection pattern by receiving required services through constructor.
     *
     * @param pricePlanService service for handling price plan calculations and comparisons
     * @param accountService   service for managing account and smart meter relationships
     */
    public PricePlanComparatorController(PricePlanService pricePlanService, AccountService accountService) {
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    /**
     * Calculates the cost of consumption for a given smart meter ID across all available price plans.
     * Follows Single Responsibility Principle by focusing only on cost calculation and comparison.
     *
     * @param smartMeterId the ID of the smart meter to calculate costs for
     * @return ResponseEntity containing a map with the current price plan ID and consumption costs for all plans,
     * or NOT_FOUND if the smart meter readings are not available
     */
    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (!consumptionsForPricePlans.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

        return consumptionsForPricePlans.isPresent()
                ? ResponseEntity.ok(pricePlanComparisons)
                : ResponseEntity.notFound().build();
    }

    /**
     * Recommends the cheapest price plans for a given smart meter ID, optionally limited to a specified number of recommendations.
     * Demonstrates Open/Closed principle by allowing different recommendation strategies without modification.
     *
     * @param smartMeterId the ID of the smart meter to get recommendations for
     * @param limit        optional parameter to limit the number of recommendations returned
     * @return ResponseEntity containing a sorted list of price plan recommendations with their costs,
     * or NOT_FOUND if the smart meter readings are not available
     */
    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(
            @PathVariable String smartMeterId, @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (!consumptionsForPricePlans.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<Map.Entry<String, BigDecimal>> recommendations =
                new ArrayList<>(consumptionsForPricePlans.get().entrySet());
        recommendations.sort(Comparator.comparing(Map.Entry::getValue));

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return ResponseEntity.ok(recommendations);
    }
}
