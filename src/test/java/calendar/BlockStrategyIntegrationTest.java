/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

/**
 *
 * @author suk22
 */
public class BlockStrategyIntegrationTest {

    @Test
    @DisplayName("FullBlockStrategy는 blockAll()을 호출해야 한다")
    void testFullBlockStrategy() {
        // given
        ReservationServiceModel service = mock(ReservationServiceModel.class);
        CalendarView view = mock(CalendarView.class);
        FullBlockStrategy strategy = new FullBlockStrategy(service, view);

        LocalDate date = LocalDate.of(2025, 1, 10);

        // when
        strategy.handle(date);

        // then
        verify(service, times(1)).blockAll(date.toString());
        verify(view, times(1)).refresh();
    }

    @Test
    @DisplayName("UnblockStrategy는 unblock()을 호출해야 한다")
    void testUnblockStrategy() {
        // given
        ReservationServiceModel service = mock(ReservationServiceModel.class);
        CalendarView view = mock(CalendarView.class);
        UnblockStrategy strategy = new UnblockStrategy(service, view);

        LocalDate date = LocalDate.of(2025, 1, 11);

        // when
        strategy.handle(date);

        // then
        verify(service, times(1)).unblock(date.toString());
        verify(view, times(1)).refresh();
    }

    @Test
    @DisplayName("PartialBlockStrategy는 UI 흐름 시작(showRoomType)까지는 정상 호출된다")
    void testPartialBlockStrategyStartsUIFlow() {
        ReservationServiceModel service = mock(ReservationServiceModel.class);
        CalendarView view = mock(CalendarView.class);

        PartialBlockStrategy strategy = Mockito.spy(new PartialBlockStrategy(service, view));
        LocalDate date = LocalDate.of(2025, 1, 12);

        doNothing().when(strategy).showRoomType(date);

        strategy.handle(date);

        verify(strategy, times(1)).showRoomType(date);

        verify(service, never()).blockPartial(anyString(), anyString(), anyString(), anyString());
    }
}
