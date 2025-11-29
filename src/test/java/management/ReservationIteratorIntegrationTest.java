/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import management.iterator.Reservation;
import management.iterator.ReservationCollection;
import management.iterator.Iterator;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author suk22
 */
@DisplayName("Iterator 통합 테스트")
public class ReservationIteratorIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("ReservationCollection이 파일에서 ReservationIterator를 통해 모든 예약 정보를 순회한다")
    void testIntegrationWithReservationCollectionAndIterator() throws IOException {
        // given
        String reservationLine1 = "Kim,Student,2023001,AI,IT,Classroom,101,2023-11-01,Mon,10:00,12:00,Study,Approved";
        String reservationLine2 = "Lee,Student,2023002,DS,Science,Lab,202,2023-11-02,Tue,13:00,15:00,Meeting,Pending";
        Path filePath = tempDir.resolve("test_reservations.txt");
        Files.write(filePath, List.of(reservationLine1, reservationLine2));

        // when
        ReservationCollection collection = new ReservationCollection(filePath.toString());
        Iterator<Reservation> iterator = collection.createIterator();

        // then
        assertTrue(iterator.hasNext(), "첫 번째 예약 존재");
        Reservation r1 = iterator.next();
        assertEquals("Kim", r1.getName());
        assertEquals("2023001", r1.getStudentId());
        assertEquals("Approved", r1.getStatus());

        assertTrue(iterator.hasNext(), "두 번째 예약 존재");
        Reservation r2 = iterator.next();
        assertEquals("Lee", r2.getName());
        assertEquals("2023002", r2.getStudentId());
        assertEquals("Pending", r2.getStatus());

        assertFalse(iterator.hasNext(), "예약 끝");
    }

    @Test
    @DisplayName("Controller 없이도 ReservationCollection과 Reservation이 정상 동작하는 통합 흐름 검증")
    void testFullIntegration_NoController() throws IOException {
        // 예약 1건 생성
        String line = "Choi,Student,2023555,Game,Media,Room,303,2023-11-28,Wed,11:00,13:00,Project,Rejected";
        Path file = tempDir.resolve("single_reservation.txt");
        Files.write(file, List.of(line));

        // Collection 및 Iterator 생성
        ReservationCollection collection = new ReservationCollection(file.toString());
        Iterator<Reservation> iterator = collection.createIterator();

        // Iterator → Reservation 객체 → 필드 검증
        assertTrue(iterator.hasNext());
        Reservation r = iterator.next();

        assertAll("예약 필드 통합 확인",
                () -> assertEquals("Choi", r.getName()),
                () -> assertEquals("2023555", r.getStudentId()),
                () -> assertEquals("Rejected", r.getStatus()),
                () -> assertEquals("Room", r.getRoomType()),
                () -> assertEquals("303", r.getRoomNumber())
        );
    }
}
