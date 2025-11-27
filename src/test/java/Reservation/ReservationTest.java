/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reservation;

/**
 *
 * @author user
 */
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    @DisplayName("빌더 패턴: 필수 값과 선택 값이 모두 정상적으로 입력되었을 때 객체 생성 성공")
    void testBuilderSuccess() {
        // given
        String name = "홍길동";
        String userType = "학생";
        String userId = "20230001";
        String userDept = "컴퓨터공학";
        String building = "공학관";
        String roomNumber = "301";
        String date = "2025-11-27";
        String startTime = "10:00";
        String endTime = "12:00";

        // when (빌더 체이닝 사용)
        Reservation reservation = new Reservation.Builder(
                name, userType, userId, userDept, building, roomNumber, date, startTime, endTime)
                .roomType("강의실")
                .dayOfWeek("목")
                .purpose("팀 프로젝트")
                .status("예약확정")
                .build();

        // then
        assertNotNull(reservation);
        assertEquals(name, reservation.getName());
        assertEquals("팀 프로젝트", reservation.getPurpose());
        assertEquals("예약확정", reservation.getStatus());
        assertEquals("목", reservation.getDayOfWeek());
    }

    @Test
    @DisplayName("빌더 패턴: 선택 값(Optional)을 입력하지 않아도 기본값으로 생성 성공")
    void testBuilderWithDefaultValues() {
        // given
        Reservation reservation = new Reservation.Builder(
                "김철수", "교수", "prof1", "전자공학", "정보관", "202", 
                "2025-12-01", "13:00", "14:00")
                .build();

        // then
        assertNotNull(reservation);
        assertEquals("", reservation.getPurpose()); // 기본값 확인
        assertEquals("예약대기", reservation.getStatus()); // Builder 클래스 내 기본값 확인
    }

    @Test
    @DisplayName("유효성 검사: 필수 데이터(이름)가 누락되면 예외 발생")
    void testValidationRequiredFieldMissing() {
        // given & when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Reservation.Builder(
                    null, // 이름 누락 (null)
                    "학생", "id1", "dept", "B1", "101", 
                    "2025-11-27", "10:00", "11:00")
                    .build();
        });

        assertEquals("이름은 필수입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("유효성 검사: 종료 시간이 시작 시간보다 빠르면 예외 발생")
    void testValidationTimeOrder() {
        // given (종료 시간이 시작 시간보다 빠름)
        String startTime = "12:00";
        String endTime = "11:00";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Reservation.Builder(
                    "TestUser", "Student", "id", "dept", "Build", "101",
                    "2025-11-27", startTime, endTime)
                    .build();
        });

        assertEquals("종료 시간은 시작 시간보다 이후여야 합니다", exception.getMessage());
    }

    @Test
    @DisplayName("유효성 검사: 날짜 포맷이 yyyy-MM-dd가 아니면 예외 발생")
    void testValidationDateFormat() {
        // given (잘못된 날짜 포맷)
        String invalidDate = "2025/11/27"; 

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Reservation.Builder(
                    "TestUser", "Student", "id", "dept", "Build", "101",
                    invalidDate, "10:00", "11:00")
                    .build();
        });

        assertTrue(exception.getMessage().contains("날짜 형식이 올바르지 않습니다"));
    }

    @Test
    @DisplayName("유효성 검사: 시간 포맷이 HH:mm이 아니면 예외 발생")
    void testValidationTimeFormat() {
        // given (잘못된 시간 포맷)
        String invalidTime = "10시 30분";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Reservation.Builder(
                    "TestUser", "Student", "id", "dept", "Build", "101",
                    "2025-11-27", invalidTime, "12:00")
                    .build();
        });

        assertTrue(exception.getMessage().contains("시간 형식이 올바르지 않습니다"));
    }

    @Test
    @DisplayName("Helper 메서드: CSV 라인 파싱(fromTxtLine) 테스트")
    void testFromTxtLine() {
        // given
        // name, userType, userId, userDept, building, roomType, roomNumber, date, dayOfWeek, startTime, endTime, purpose, status
        String csvLine = "박지성,학생,park123,체육학과,체육관,실습실,101,2025-11-30,일,09:00,10:00,동아리,예약확정";

        // when
        Reservation r = Reservation.fromTxtLine(csvLine);

        // then
        assertNotNull(r);
        assertEquals("박지성", r.getName());
        assertEquals("체육학과", r.getUserDept());
        assertEquals("체육관", r.getBuilding());
        assertEquals("실습실", r.getRoomType()); // 5번째 인덱스
        assertEquals("101", r.getRoomNumber()); // 6번째 인덱스
        assertEquals("예약확정", r.getStatus());
    }

    @Test
    @DisplayName("Helper 메서드: CSV 변환(toResult) 테스트")
    void testToResult() {
        // given
        Reservation r = new Reservation.Builder(
                "손흥민", "학생", "son7", "체육", "본관", "303", 
                "2025-12-25", "10:00", "12:00")
                .roomType("강의실")
                .dayOfWeek("목")
                .purpose("스터디")
                .status("예약대기")
                .build();

        // when
        String result = r.toResult();

        // then (CSV 형식 확인)
        // name, userType, userId, userDept, building, roomType, roomNumber, date, dayOfWeek, startTime, endTime, purpose, status
        String expected = "손흥민,학생,son7,체육,본관,강의실,303,2025-12-25,목,10:00,12:00,스터디,예약대기";
        assertEquals(expected, result);
    }
}
