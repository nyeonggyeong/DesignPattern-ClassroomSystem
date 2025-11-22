/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.state;

/**
 *
 * @author suk22
 */
public class ReservationContext {

    private final ReservationState pendingState = new PendingState();
    private final ReservationState approvedState = new ApprovedState();
    private final ReservationState rejectedState = new RejectedState();
    private final ReservationState adminCanceledState = new AdminCanceledState();

    private ReservationState state;

    public ReservationContext(String initialState) {
        switch (initialState) {
            case "승인":
                state = approvedState;
                break;
            case "거절":
                state = rejectedState;
                break;
            case "관리자취소":
                state = adminCanceledState;
                break;
            default:
                state = pendingState;
        }
    }

    public void approve() {
        state.approve(this);
    }

    public void reject() {
        state.reject(this);
    }

    public void setState(ReservationState state) {
        this.state = state;
    }

    public ReservationState getPendingState() {
        return pendingState;
    }

    public ReservationState getApprovedState() {
        return approvedState;
    }

    public ReservationState getRejectedState() {
        return rejectedState;
    }

    public ReservationState getAdminCanceledState() {
        return adminCanceledState;
    }

    public String getStatusName() {
        return state.getStatusName();
    }
}
