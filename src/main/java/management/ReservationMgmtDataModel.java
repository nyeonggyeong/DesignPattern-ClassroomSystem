/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suk22
 */
public class ReservationMgmtDataModel extends ReservationMgmtSubject {

    private List<ReservationMgmtModel> reservations;

    public ReservationMgmtDataModel() {
        reservations = new ArrayList<>();
    }

    public void setReservations(List<ReservationMgmtModel> newList) {
        this.reservations = newList != null ? newList : new ArrayList<>();
        notifyObservers(); // 상태 변경 알림
    }

    public List<ReservationMgmtModel> getReservations() {
        return reservations;
    }

    public List<ReservationMgmtModel> searchReservations(String name, String studentId, String room) {
        List<ReservationMgmtModel> result = new ArrayList<>();

        for (ReservationMgmtModel r : reservations) {
            boolean match = true;

            if (name != null && !name.isEmpty() && !r.getName().contains(name)) {
                match = false;
            }
            if (studentId != null && !studentId.isEmpty() && !r.getStudentId().contains(studentId)) {
                match = false;
            }
            if (room != null && !room.isEmpty() && !r.getRoom().contains(room)) {
                match = false;
            }

            if (match) {
                result.add(r);
            }
        }

        return result;
    }

    // 승인 상태 업데이트
    public void updateApprovalInMemory(String studentId, String newStatus) {
        for (ReservationMgmtModel r : reservations) {
            if (r.getStudentId().equals(studentId)) {

                if ("승인".equals(newStatus)) {
                    r.approve();
                } else if ("거절".equals(newStatus)) {
                    r.reject();
                } else if ("관리자취소".equals(newStatus)) {
                    r.cancelByAdmin();
                } else {
                    r.setPending();
                }
                break;
            }
        }
        notifyObservers();
    }
}
