/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.state;

/**
 *
 * @author suk22
 */
public class ApprovedState implements ReservationState {

    @Override
    public void approve(ReservationContext ctx) {
        // 이미 승인 상태 → 아무 처리 없음
    }

    @Override
    public void reject(ReservationContext ctx) {
        // 승인 상태에서는 거절로 바꿀 수 없도록 설계할 수도 있지만,
        // 필요하면 허용하도록 커스터마이징 가능
        ctx.setState(ctx.getRejectedState());
    }

    @Override
    public String getStatusName() {
        return "승인";
    }
}
