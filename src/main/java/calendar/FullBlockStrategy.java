/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.time.LocalDate;

/**
 *
 * @author suk22
 */
public class FullBlockStrategy implements BlockStrategy {

    private final ReservationServiceModel service;
    private final CalendarView view;

    public FullBlockStrategy(ReservationServiceModel service, CalendarView view) {
        this.service = service;
        this.view = view;
    }

    @Override
    public void handle(LocalDate date) {
        service.blockAll(date.toString());
        view.refresh();
    }
}
