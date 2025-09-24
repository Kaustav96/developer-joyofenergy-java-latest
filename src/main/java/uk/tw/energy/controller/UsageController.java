package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.UnitCostService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/usage")
public class UsageController {

    private final UnitCostService unitCostService;

    public UsageController(UnitCostService unitCostService){
        this.unitCostService = unitCostService;
    }

    @GetMapping("{smartMeterId}/{noOfDays}")
    public ResponseEntity getLastWeekCost(@PathVariable String smartMeterId, @PathVariable int noOfDays){
        try{
            BigDecimal cost = unitCostService.calculateLastWeekUsageCost(smartMeterId, noOfDays);
            return ResponseEntity.ok().body(smartMeterId+" -> "+cost);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("No Plan for the smart meter id");
        }
    }
}
