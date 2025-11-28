/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.time.LocalDate;
import javax.swing.JOptionPane;

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
    try {
        service.unblock(date.toString());
        view.refresh();

        JOptionPane.showMessageDialog(
            null,
            date + " 날짜에 대한 차단을 취소했습니다.",
            "알림",
            JOptionPane.INFORMATION_MESSAGE
        );
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
            null,
            "차단 취소 중 오류가 발생했습니다.\n" + e.getMessage(),
            "오류",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
}
