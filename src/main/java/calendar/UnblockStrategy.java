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
public class UnblockStrategy implements BlockStrategy {

    private final ReservationServiceModel service;
    private final CalendarView view;

    public UnblockStrategy(ReservationServiceModel service, CalendarView view) {
        this.service = service;
        this.view = view;
    }

    @Override
    public void handle(LocalDate date) {
        service.unblock(date.toString());
        view.refresh();
    }
}
