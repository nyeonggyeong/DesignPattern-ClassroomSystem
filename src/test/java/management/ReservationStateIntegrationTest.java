/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author suk22
 */
public class ReservationStateIntegrationTest {

    private ReservationMgmtModel model;

    @BeforeEach
    void setUp() {
        model = new ReservationMgmtModel(
                "홍길동", "20230001", "컴퓨터공학",
                "공학관 301호", "2025-11-30", "10:00",
                "예약대기" 
        );
    }

    @Test
    @DisplayName("시나리오 1: 예약대기 -> 승인 (정상 흐름)")
    void testPendingToApproved() {
        assertEquals("예약대기", model.getApproved());

        model.approve();

        assertEquals("승인", model.getApproved());
    }

    @Test
    @DisplayName("시나리오 2: 예약대기 -> 거절 (정상 흐름)")
    void testPendingToRejected() {
        model.reject();
        
        assertEquals("거절", model.getApproved());
    }

    @Test
    @DisplayName("시나리오 3: 승인 -> 거절 (승인 취소)")
    void testApprovedToRejected() {
        model.approve();
        assertEquals("승인", model.getApproved());

        model.reject();

        assertEquals("거절", model.getApproved());
    }

    @Test
    @DisplayName("시나리오 4: 거절 -> 승인 (불가)")
    void testRejectedCannotBeApproved() {

        model.reject();
        assertEquals("거절", model.getApproved());

        model.approve();

        assertEquals("거절", model.getApproved());
    }

    @Test
    @DisplayName("시나리오 5: 관리자 취소 (강제 상태 변경)")
    void testAdminCancel() {
        // Given
        model.approve();
        assertEquals("승인", model.getApproved());

        // When
        model.cancelByAdmin();

        // Then
        assertEquals("관리자취소", model.getApproved());
    }

    @Test
    @DisplayName("시나리오 6: 관리자 취소 상태에서는 승인/거절 불가")
    void testAdminCanceledIsFinal() {
        // Given
        model.cancelByAdmin();
        
        // When
        model.approve();
        assertEquals("관리자취소", model.getApproved());

        // When
        model.reject();
        assertEquals("관리자취소", model.getApproved());
    }
    
    @Test
    @DisplayName("초기 상태 로딩 테스트: DB/파일에서 '승인' 상태를 읽어왔을 때")
    void testInitialStateLoading() {
        // When: 초기값을 '승인'으로 생성
        ReservationMgmtModel approvedModel = new ReservationMgmtModel(
                "김철수", "20230002", "경영학",
                "경영관 101호", "2025-12-01", "13:00",
                "승인"
        );

        // Then
        assertEquals("승인", approvedModel.getApproved());
        
        approvedModel.reject();
        assertEquals("거절", approvedModel.getApproved());
    }
}
