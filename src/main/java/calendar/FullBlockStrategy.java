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
public class FullBlockStrategy implements BlockStrategy {

    private final ReservationServiceModel service;
    private final CalendarView view;

    public FullBlockStrategy(ReservationServiceModel service, CalendarView view) {
        this.service = service;
        this.view = view;
    }

    @Override
    public void handle(LocalDate date) {
        try {
            if (service.isFullyBlocked(date.toString())) {
                JOptionPane.showMessageDialog(
                        null,
                        "이미 전체 차단된 날짜입니다.",
                        "알림",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return; 
            }

            service.blockAll(date.toString());
            view.refresh();

            JOptionPane.showMessageDialog(
                    null,
                    date + " 날짜를 전체 차단했습니다.",
                    "완료",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "전체 차단 중 오류가 발생했습니다.\n" + e.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
