/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reservation;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author user
 */
public class Reservation {

    private final String name;
    private final String userType;
    private final String userId;
    private final String building;
    private final String roomNumber;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String userDept;
    private final String roomType;
    private final String dayOfWeek;
    private final String purpose;
    private final String status;

    private Reservation(Builder builder) {
        this.name = builder.name;
        this.userType = builder.userType;
        this.userId = builder.userId;
        this.building = builder.building;
        this.roomNumber = builder.roomNumber;
        this.date = builder.date;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.userDept = builder.userDept;
        this.roomType = builder.roomType;
        this.dayOfWeek = builder.dayOfWeek;
        this.purpose = builder.purpose;
        this.status = builder.status;
    }

    public String getName() {
        return name;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getUserDept() {
        return userDept;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }

    public String toResult() {
        return String.join(",",
                name, userType, userId, userDept,
                building, roomType, roomNumber, date,
                dayOfWeek, startTime, endTime, purpose, status);
    }

    public static Reservation fromTxtLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 13) {
            return null;
        }
        // 테스트,교수,dhkdrjs,소프트웨어,산학협력관,강의실,401,2025-11-21,금,18:00,18:50,강의 전용,예약확정
        return new Builder(
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

    public static class Builder {

        private final String name;
        private final String userType;
        private final String userId;
        private final String building;
        private final String roomNumber;
        private final String date;
        private final String startTime;
        private final String endTime;
        private final String userDept;

        private String roomType = "";
        private String dayOfWeek = "";
        private String purpose = "";
        private String status = "예약대기";

        public Builder(String name, String userType, String userId, String userDept, String building,
                String roomNumber, String date, String startTime, String endTime) {
            this.name = name;
            this.userType = userType;
            this.userId = userId;
            this.building = building;
            this.roomNumber = roomNumber;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.userDept = userDept;
        }

        public Builder roomType(String roomType) {
            this.roomType = roomType != null ? roomType : "";
            return this;
        }

        public Builder dayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek != null ? dayOfWeek : "";
            return this;
        }

        public Builder purpose(String purpose) {
            this.purpose = purpose != null ? purpose : "";
            return this;
        }

        public Builder status(String status) {
            this.status = status != null ? status : "예약 대기";
            return this;
        }

        private void validateRequired() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("이름은 필수입니다.");
            }
            if (userType == null || userType.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자 역할은 필수입니다.");
            }
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("아이디는 필수입니다.");
            }
            if (userDept == null || userDept.trim().isEmpty()) {
                throw new IllegalArgumentException("학과는 필수입니다.");
            }
            if (building == null || building.trim().isEmpty()) {
                throw new IllegalArgumentException("건물명은 필수입니다.");
            }
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("강의실 호수는 필수입니다.");
            }
            if (date == null || date.trim().isEmpty()) {
                throw new IllegalArgumentException("날짜는 필수입니다.");
            }
            if (startTime == null || startTime.trim().isEmpty()) {
                throw new IllegalArgumentException("시작 시간은 필수입니다.");
            }
            if (endTime == null || endTime.trim().isEmpty()) {
                throw new IllegalArgumentException("종료 시간은 필수입니다.");
            }
        }

        private void validateTimeFormat() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            try {
                Date start = sdf.parse(startTime);
                Date end = sdf.parse(endTime);

                if (!end.after(start)) {
                    throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다");
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("시간 형식이 올바르지 않습니다 (HH:mm 형식 필요): " + e.getMessage());
            }
        }

        private void validateDateFormat() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            try {
                sdf.parse(date);
            } catch (ParseException e) {
                throw new IllegalArgumentException("날짜 형식이 올바르지 않습니다 (yyyy-MM-dd 형식 필요): " + e.getMessage());
            }
        }

        public Reservation build() {
            validateRequired();
            validateTimeFormat();
            validateDateFormat();
            return new Reservation(this);
        }
    }
}
