/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.state;

/**
 *
 * @author suk22
 */
public class RejectedState implements ReservationState {

    @Override
    public void approve(ReservationContext ctx) {
        // 거절 상태에서 승인 불가 (필요하면 예외 출력)
    }

    @Override
    public void reject(ReservationContext ctx) {
        // 이미 거절 상태
    }

    @Override
    public String getStatusName() {
        return "거절";
    }
}
