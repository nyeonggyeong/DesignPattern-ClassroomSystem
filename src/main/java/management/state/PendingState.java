/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.state;

/**
 *
 * @author suk22
 */
public class PendingState implements ReservationState {

    @Override
    public void approve(ReservationContext ctx) {
        ctx.setState(ctx.getApprovedState());
    }

    @Override
    public void reject(ReservationContext ctx) {
        ctx.setState(ctx.getRejectedState());
    }

    @Override
    public String getStatusName() {
        return "예약대기";
    }
}

