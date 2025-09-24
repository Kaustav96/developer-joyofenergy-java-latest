package uk.tw.energy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.builders.MeterReadingsBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

/**
 * Test class for MeterReadingController that verifies the functionality
 * of storing and retrieving meter readings through the controller.
 */
public class MeterReadingControllerTest {

    private static final String SMART_METER_ID = "10101010";
    private MeterReadingController meterReadingController;
    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        this.meterReadingService = new MeterReadingService(new HashMap<>());
        this.meterReadingController = new MeterReadingController(meterReadingService);
    }

    /**
     * Tests that attempting to store meter readings without a meter ID results in an error response.
     */
    @Test
    public void givenNoMeterIdIsSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(null, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Tests that attempting to store empty meter readings results in an error response.
     */
    @Test
    public void givenEmptyMeterReadingShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, Collections.emptyList());
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Tests that attempting to store null meter readings results in an error response.
     */
    @Test
    public void givenNullReadingsAreSuppliedWhenStoringShouldReturnErrorResponse() {
        MeterReadings meterReadings = new MeterReadings(SMART_METER_ID, null);
        assertThat(meterReadingController.storeReadings(meterReadings).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Tests that multiple batches of meter readings for the same meter ID are correctly stored
     * and can be retrieved as a combined list.
     */
    @Test
    public void givenMultipleBatchesOfMeterReadingsShouldStore() {
        MeterReadings meterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        MeterReadings otherMeterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        meterReadingController.storeReadings(meterReadings);
        meterReadingController.storeReadings(otherMeterReadings);

        List<ElectricityReading> expectedElectricityReadings = new ArrayList<>();
        expectedElectricityReadings.addAll(meterReadings.electricityReadings());
        expectedElectricityReadings.addAll(otherMeterReadings.electricityReadings());

        assertThat(meterReadingService.getReadings(SMART_METER_ID).get()).isEqualTo(expectedElectricityReadings);
    }

    /**
     * Tests that meter readings are correctly associated with their respective meter IDs
     * when storing readings for multiple meters.
     */
    @Test
    public void givenMeterReadingsAssociatedWithTheUserShouldStoreAssociatedWithUser() {
        MeterReadings meterReadings = new MeterReadingsBuilder()
                .setSmartMeterId(SMART_METER_ID)
                .generateElectricityReadings()
                .build();

        MeterReadings otherMeterReadings = new MeterReadingsBuilder()
                .setSmartMeterId("00001")
                .generateElectricityReadings()
                .build();

        meterReadingController.storeReadings(meterReadings);
        meterReadingController.storeReadings(otherMeterReadings);

        assertThat(meterReadingService.getReadings(SMART_METER_ID).get())
                .isEqualTo(meterReadings.electricityReadings());
    }

    /**
     * Tests that attempting to read readings for an unrecognized meter ID returns a not found response.
     */
    @Test
    public void givenMeterIdThatIsNotRecognisedShouldReturnNotFound() {
        assertThat(meterReadingController.readReadings(SMART_METER_ID).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
