/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.state;

/**
 *
 * @author suk22
 */
public class AdminCanceledState implements ReservationState {

    @Override
    public void approve(ReservationContext context) {
        // 관리자 취소 상태에서는 승인 불가
    }

    @Override
    public void reject(ReservationContext context) {
        // 관리자 취소 상태에서는 거절 불가
    }

    @Override
    public String getStatusName() {
        return "관리자취소";
    }
}
