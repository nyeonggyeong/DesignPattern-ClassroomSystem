/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author suk22
 */
public class PartialBlockStrategy implements BlockStrategy {

    private final ReservationServiceModel service;
    private final CalendarView view;

    public PartialBlockStrategy(ReservationServiceModel service, CalendarView view) {
        this.service = service;
        this.view = view;
    }

    @Override
    public void handle(LocalDate date) {
        showRoomType(date);
    }

    /**
     * 강의실/실습실 선택 다이얼로그
     */
    private void showRoomType(LocalDate date) {
        RoomTypeDialogView roomTypeDialog = new RoomTypeDialogView();
        roomTypeDialog.setHandler(new RoomTypeDialogView.Handler() {
            @Override
            public void onSelectLecture() {
                roomTypeDialog.close();
                showRoomNumber(date, "강의실");
            }

            @Override
            public void onSelectLab() {
                roomTypeDialog.close();
                showRoomNumber(date, "실습실");
            }

            @Override
            public void onCancel() {
                roomTypeDialog.close();
            }
        });

        roomTypeDialog.show();
    }

    /**
     * 호수 선택 다이얼로그
     */
    private void showRoomNumber(LocalDate date, String roomType) {
        RoomNumberDialogView roomNumberDialog = new RoomNumberDialogView();
        roomNumberDialog.setHandler(new RoomNumberDialogView.Handler() {
            @Override
            public void onSelect(String roomNumber) {
                roomNumberDialog.close();
                showTimeSlot(date, roomType, roomNumber);
            }

            @Override
            public void onCancel() {
                roomNumberDialog.close();
            }
        });

        roomNumberDialog.show(roomType);
    }

    /**
     * 시간대 선택 다이얼로그
     */
    private void showTimeSlot(LocalDate date, String roomType, String roomNumber) {
        List<String> blockedSlots
                = service.getBlockedTimeSlots(date.toString(), roomType, roomNumber);

        TimeSlotDialogView timeSlotDialog = new TimeSlotDialogView(blockedSlots);
        timeSlotDialog.setHandler(new TimeSlotDialogView.Handler() {
            @Override
            public void onSelect(String timeSlot) {
                service.blockPartial(date.toString(), roomType, roomNumber, timeSlot);
                view.refresh();
                timeSlotDialog.close();
            }

            @Override
            public void onCancel() {
                timeSlotDialog.close();
            }
        });

        timeSlotDialog.show();
    }
}
