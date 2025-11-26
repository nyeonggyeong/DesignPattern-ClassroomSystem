/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author suk22
 */
public class UnblockStrategyTest {

    private FakeService service;
    private FakeView view;
    private UnblockStrategy strategy;

    @BeforeEach
    void setUp() {
        service = new FakeService();
        view = new FakeView();
        strategy = new UnblockStrategy(service, view);
    }

    @Test
    void testUnblockStrategyHandle() {
        LocalDate date = LocalDate.of(2025, 11, 30);
        strategy.handle(date);

        assertEquals("UNBLOCK:2025-11-30", service.lastAction);
        assertTrue(view.refreshed);
    }

    // ===== Fake Objects =====
    static class FakeService extends ReservationServiceModel {

        String lastAction = "";

        FakeService() {
            super(null);
        }

        @Override
        public void unblock(String date) {
            lastAction = "UNBLOCK:" + date;
        }
    }

    static class FakeView extends CalendarView {

        boolean refreshed = false;

        @Override
        public void refresh() {
            refreshed = true;
        }
    }
}
