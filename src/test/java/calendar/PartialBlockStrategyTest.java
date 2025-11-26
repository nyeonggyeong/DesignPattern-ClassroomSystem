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
public class PartialBlockStrategyTest {

    private FakeService service;
    private FakeView view;
    private TestablePartialStrategy strategy;

    @BeforeEach
    void setUp() {
        service = new FakeService();
        view = new FakeView();
        strategy = new TestablePartialStrategy(service, view);
    }

    @Test
    void testPartialBlockStrategyHandle() {
        LocalDate date = LocalDate.of(2025, 11, 30);

        strategy.handle(date);

        assertEquals(date, strategy.calledWith);
    }

    static class FakeService extends ReservationServiceModel {

        FakeService() {
            super(null);
        }
    }

    static class FakeView extends CalendarView {
    }

    static class TestablePartialStrategy extends PartialBlockStrategy {

        LocalDate calledWith = null;

        TestablePartialStrategy(ReservationServiceModel service, CalendarView view) {
            super(service, view);
        }

        @Override
        protected void showRoomType(LocalDate date) {
            this.calledWith = date;
        }
    }
}
