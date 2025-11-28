/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import management.state.ReservationContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author suk22
 */
public class ReservationContextTest {

    @Test
    void testInitialStatePending() {
        ReservationContext ctx = new ReservationContext("예약대기");
        assertEquals("예약대기", ctx.getStatusName());
    }

    @Test
    void testInitialStateApproved() {
        ReservationContext ctx = new ReservationContext("승인");
        assertEquals("승인", ctx.getStatusName());
    }

    @Test
    void testInitialStateRejected() {
        ReservationContext ctx = new ReservationContext("거절");
        assertEquals("거절", ctx.getStatusName());
    }

    @Test
    void testInitialStateAdminCanceled() {
        ReservationContext ctx = new ReservationContext("관리자취소");
        assertEquals("관리자취소", ctx.getStatusName());
    }

    @Test
    void testPendingToApproved() {
        ReservationContext ctx = new ReservationContext("예약대기");
        ctx.approve();
        assertEquals("승인", ctx.getStatusName());
    }

    @Test
    void testPendingToRejected() {
        ReservationContext ctx = new ReservationContext("예약대기");
        ctx.reject();
        assertEquals("거절", ctx.getStatusName());
    }

    @Test
    void testApprovedRejectChangesToRejected() {
        ReservationContext ctx = new ReservationContext("승인");
        ctx.reject();
        assertEquals("거절", ctx.getStatusName());
    }

    @Test
    void testApprovedApproveDoesNothing() {
        ReservationContext ctx = new ReservationContext("승인");
        ctx.approve();
        assertEquals("승인", ctx.getStatusName());
    }

    @Test
    void testRejectedRejectDoesNothing() {
        ReservationContext ctx = new ReservationContext("거절");
        ctx.reject();
        assertEquals("거절", ctx.getStatusName());
    }

    @Test
    void testRejectedApproveDoesNothing() {
        ReservationContext ctx = new ReservationContext("거절");
        ctx.approve();  // 승인은 불가
        assertEquals("거절", ctx.getStatusName());
    }

    @Test
    void testAdminCanceledNoStateChange() {
        ReservationContext ctx = new ReservationContext("관리자취소");

        ctx.approve();
        assertEquals("관리자취소", ctx.getStatusName());

        ctx.reject();
        assertEquals("관리자취소", ctx.getStatusName());
    }
}
