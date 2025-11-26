/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management;

import management.iterator.Iterator;
import management.iterator.Reservation;
import management.iterator.ReservationCollection;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suk22
 */
public class ReservationIteratorTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("파일에 데이터가 있을 때 순차적으로 모든 데이터를 읽어와야 한다")
    void testIteratorWithValidData() throws IOException {
        String line1 = "Kim,Student,2020001,CS,Engineering,Class,101,2023-01-01,Mon,10:00,12:00,Study,Approved";
        String line2 = "Lee,Student,2020002,Biz,Business,Lab,202,2023-01-02,Tue,13:00,15:00,Meeting,Pending";

        Path file = tempDir.resolve("reservations.txt");
        Files.write(file, List.of(line1, line2));

        ReservationCollection collection = new ReservationCollection(file.toString());

        Iterator<Reservation> iterator = collection.createIterator();

        assertTrue(iterator.hasNext(), "첫 번째 데이터가 있어야 합니다.");
        Reservation r1 = iterator.next();
        assertNotNull(r1);
        assertEquals("Kim", r1.getName());
        assertEquals("2020001", r1.getStudentId());
        assertEquals("Approved", r1.getStatus());

        assertTrue(iterator.hasNext(), "두 번째 데이터가 있어야 합니다.");
        Reservation r2 = iterator.next();
        assertNotNull(r2);
        assertEquals("Lee", r2.getName());
        assertEquals("2020002", r2.getStudentId());
        assertEquals("Pending", r2.getStatus());

        assertFalse(iterator.hasNext(), "더 이상 데이터가 없어야 합니다.");
    }

    @Test
    @DisplayName("빈 파일인 경우 hasNext는 즉시 false를 반환해야 한다")
    void testIteratorWithEmptyFile() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.createFile(file);

        ReservationCollection collection = new ReservationCollection(file.toString());
        Iterator<Reservation> iterator = collection.createIterator();

        assertFalse(iterator.hasNext(), "빈 파일이므로 hasNext는 false여야 합니다.");
    }

    @Test
    @DisplayName("파일 경로가 잘못되었을 때 예외가 발생하지 않고 빈 Iterator처럼 동작해야 한다")
    void testIteratorWithInvalidPath() {
        String invalidPath = "invalid/path/to/file.txt";
        ReservationCollection collection = new ReservationCollection(invalidPath);
        Iterator<Reservation> iterator = collection.createIterator();

        assertFalse(iterator.hasNext(), "잘못된 파일 경로는 데이터가 없는 것으로 처리되어야 합니다.");
    }

    @Test
    @DisplayName("Reservation 객체가 CSV 데이터를 올바르게 파싱하는지 검증")
    void testReservationDataParsing() {
        String rawLine = "Park,Student,20231234,AI,Tech,StudyRoom,305,2023-05-05,Fri,09:00,11:00,Exam,Rejected";
        Reservation reservation = new Reservation(rawLine);

        assertAll("Reservation 필드 파싱 검증",
                () -> assertEquals("Park", reservation.getName()),
                () -> assertEquals("20231234", reservation.getStudentId()),
                () -> assertEquals("AI", reservation.getDepartment()),
                () -> assertEquals("305", reservation.getRoomNumber()),
                () -> assertEquals("Rejected", reservation.getStatus()),
                () -> assertEquals(rawLine, reservation.getRawLine())
        );
    }

    @Test
    @DisplayName("While 루프를 사용한 전체 순회 테스트")
    void testFullTraversalLoop() throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            lines.add("User" + i + ",Student,2020" + i + ",Dept,Bldg,Room," + (100 + i) + ",Date,Day,Start,End,Purpose,Status");
        }
        Path file = tempDir.resolve("loop_test.txt");
        Files.write(file, lines);

        ReservationCollection collection = new ReservationCollection(file.toString());
        Iterator<Reservation> iterator = collection.createIterator();

        int count = 0;
        while (iterator.hasNext()) {
            Reservation r = iterator.next();
            assertEquals("User" + count, r.getName());
            count++;
        }

        assertEquals(3, count, "총 3개의 예약 정보를 순회해야 합니다.");
    }
}
