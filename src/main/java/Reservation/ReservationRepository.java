/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reservation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */
public class ReservationRepository {

    private static final String RESERVATION_FILE_PATH = "src/main/resources/reservation.txt";

    public boolean save(Reservation reservation) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(RESERVATION_FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.write(reservation.toResult());
            writer.newLine();
            System.out.println("예약 저장 완료");
            return true;
        } catch (IOException e) {
            System.out.println("예약 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean saveAll(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return true;
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(RESERVATION_FILE_PATH, true), StandardCharsets.UTF_8))) {
            for (Reservation reservation : reservations) {
                writer.write(reservation.toResult());
                writer.newLine();
            }
            System.out.println("예약 저장 완료");
            return true;
        } catch (IOException e) {
            System.out.println("예약 실패: " + e.getMessage());
            return false;
        }
    }

    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(RESERVATION_FILE_PATH), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Reservation reservation = Reservation.fromTxtLine(line);
                    
                    if (reservation != null) {
                        reservations.add(reservation);
                    } else {
                        System.out.println("라인 파싱 실패");
                    }
                        
                } catch (IllegalArgumentException e) {
                    System.out.println("파싱 오류: " + e.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println("파일 오류: " + ex.getMessage());
        }
        return reservations;
    }

    public List<Reservation> findByUserIdAndDate(String userId, String date) {
        return findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public boolean exists(String userId, String date) {
        return !findByUserIdAndDate(userId, date).isEmpty();
    }

    public List<Reservation> findConflictReservation(String building, String roomNumber,
            String date, List<String> newTimes) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        List<Reservation> conflicts = new ArrayList<>();

        List<Reservation> allReservations = findAll();

        for (Reservation reservation : allReservations) {
            if (!reservation.getBuilding().equals(building)
                    || !reservation.getRoomNumber().equals(roomNumber)
                    || !reservation.getDate().equals(date)) {
                continue;
            }

            try {
                Date reservedStart = sdf.parse(reservation.getStartTime());
                Date reservedEnd = sdf.parse(reservation.getEndTime());

                for (String timePart : newTimes) {
                    String[] parts = timePart.split("~");
                    if (parts.length == 2) {
                        Date newStart = sdf.parse(parts[0].trim());
                        Date newEnd = sdf.parse(parts[1].trim());

                        if (newStart.before(reservedEnd) && newEnd.after(reservedStart)) {
                            conflicts.add(reservation);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("에러 발생: " + e.getMessage());
            }
        }
        return conflicts;
    }

    public boolean delete(List<Reservation> reservationsToDelete) {
        if (reservationsToDelete == null || reservationsToDelete.isEmpty()) {
            return true;
        }

        Set<String> linesToRemove = reservationsToDelete.stream()
                .map(Reservation::toResult)
                .collect(Collectors.toSet());

        Path tempPath = null;
        try {
            tempPath = Files.createTempFile("tempReservations", ".txt");
            Path targetPath = Paths.get(RESERVATION_FILE_PATH);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(targetPath.toFile()),
                            StandardCharsets.UTF_8)); BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(tempPath.toFile()),
                            StandardCharsets.UTF_8))) {

                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    if (!linesToRemove.contains(currentLine.trim())) {
                        writer.write(currentLine);
                        writer.newLine();
                    }
                }
            }

            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(reservationsToDelete.size() + "개의 예약 삭제 완료");
            return true;

        } catch (IOException ex) {
            System.err.println("예약 삭제 실패: " + ex.getMessage());
            if (tempPath != null) {
                try {
                    Files.deleteIfExists(tempPath);
                } catch (Exception e) {
                    // ignore
                }
            }
            return false;
        }
    }

    public List<Reservation> convertFromStringArrayList(List<String[]> stringArrayList) {
        return stringArrayList.stream()
                .map(parts -> {
                    if (parts.length >= 13) {
                        return new Reservation.Builder(
                                parts[0], // name
                                parts[1], // userType
                                parts[2], // userId
                                parts[3], // userDept
                                parts[4], // building
                                parts[6], // roomNumber
                                parts[7], // date
                                parts[9], // startTime
                                parts[10] // endTime
                        ).roomType(parts[5]).dayOfWeek(parts[8]).purpose(parts[11]).status(parts[12]).build(); 
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
