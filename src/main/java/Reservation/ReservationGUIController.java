package Reservation;

import ServerClient.LogoutUtil;
import UserFunction.UserMainController;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * ReservationGUIController 강의실 예약 시스템의 GUI Controller 클래스 - 사용자 정보 처리 - 엑셀/텍스트
 * 파일 기반 강의실 및 예약 관리 - View(ReservationView)와 연결하여 사용자와 상호작용 - 서버와의 통신(Socket,
 * BufferedReader/Writer) 연동
 */
public class ReservationGUIController {

    private ReservationView view; // 사용자 인터페이스 뷰 (GUI)
    private static final String ROOMS_JSON_PATH = "src/main/resources/rooms.json";
    private static final String SCHEDULES_JSON_PATH = "src/main/resources/schedules.json";
    private final String reservationPath = "src/main/resources/reservation.txt";

    private List<RoomModel> allRooms = new ArrayList<>(); //로드된 강의실 목록
    private ScheduleRoot scheduleData; // 요일별 목록
    private List<String[]> copyReserved = new ArrayList<>(); // 기존 학생 예약 목록
    private Set<String> cancelReservationUser = new HashSet<>(); // 취소될 사용자 정보

    private String userName;  //사용자 이름
    private String userId;  //사용자id
    private String userDept;    //사용자 학과
    private String userType; // "학생" 또는 "교수"
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private String reserveDate; // 예약 시간
    private List<String> reserveTimes; // 예약 시간(시작, 끝)
    private String reservePurpose; // 예약 목적
//    private String reserveRoomName; // 예약 룸 이름(강의실 or 실습실)
    private RoomModel selectedRoom;
    private String dayOfWeek;
    private String status;
    private String reserveRoomNumber;
    
    private ReservationRepository reservationRepository;

//클라이언트-서버 연결 코드(로그인과 사용자 페이지 연결되면 주석 해제)
    public ReservationGUIController(String userId, String name, String dept, String type,
            Socket socket, BufferedReader in, BufferedWriter out) {
        this.userId = userId;
        this.userName = name;
        this.userDept = dept;
        this.userType = type;
        this.socket = socket;
        this.in = in;
        this.out = out;
        System.out.println("사용자ID:" + this.userId + "사용자 이름: " + userName + "사용자Dept: " + userDept);
        
        reservationRepository = new ReservationRepository();
        view = new ReservationView();

        // 서버에서 사용자 정보 불러오기
        System.out.println("사용자 정보를 불러오겠습니다.");
        initializeUserInfoFromServer();
        System.out.println("최종 유저 정보 - 이름: " + userName + ", 학과: " + userDept);

        view.setUserInfo(userName, userId, userDept);

        LogoutUtil.attach(view, userId, socket, out);

        // json 데이터 불러오기
        loadRoomsJson();
        loadSchedulesJson();

        initializeReservationFeatures();

        view.setVisible(true);
    }

    private void loadRoomsJson() {
        allRooms.clear();
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(ROOMS_JSON_PATH), StandardCharsets.UTF_8)) {
            RoomStaticRoot root = gson.fromJson(reader, RoomStaticRoot.class);

            if (root != null && root.buildings != null) {
                for (BuildingStatic b : root.buildings) {
                    for (FloorStatic f : b.floors) {
                        for (RoomStatic r : f.rooms) {
                            RoomModel model = new RoomModel(
                                    b.name,
                                    String.valueOf(f.floor),
                                    r.roomNumber,
                                    r.type,
                                    r.capacity
                            );
                            allRooms.add(model);
                        }
                    }
                }
            }
            System.out.println("room.json 로드 완료");
        } catch (IOException e) {
            System.out.println("에러 발생: " + e.getMessage());
        }
    }

    private void loadSchedulesJson() {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(SCHEDULES_JSON_PATH), StandardCharsets.UTF_8)) {
            scheduleData = gson.fromJson(reader, ScheduleRoot.class);
            System.out.println("schedules.json 로드 완료");
        } catch (IOException e) {
            System.out.println("에러 발생" + e.getMessage());
            scheduleData = new ScheduleRoot();
        }
    }

    /**
     * 예약 기능 초기화: - 강의실 목록, 버튼 이벤트, 시간대 표시, UI 구성 등
     */
    private void initializeReservationFeatures() {

        if (userType.equals("교수")) {
            view.enableProfessorMode(); // View 내부에서 교수 전용 UI 구역 활성화
            view.setPurposeOptions(List.of("강의 전용", "세미나", "보강", "기타"));  // 교수 전용
        } else {
            view.setPurposeOptions(List.of("스터디", "동아리 활동", "면담", "팀 회의"));  // 학생용
        }

        // 건물 세팅
        List<String> buildings = allRooms.stream()
                .map(RoomModel::getBuilding)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        view.setBuildingList(buildings);
        // 층 설정
        view.addBuildingListener(e -> updateFloorList());
        // 강의실 설정
        view.addFloorListener(e -> updateRoomList());
        // 강의실 정보, 시간표 설정
        view.addRoomListener(e -> {
            updateRoomInfo();
            updateAvailableTimes();
        });
        // 날짜 변경시 시간표 갱신
        view.addDateListener(e -> updateAvailableTimes());
        // 초기 세팅
        if (!buildings.isEmpty()) {
            view.getBuildingComboBox().setSelectedIndex(0);
        }

        view.addReserveListener(e -> handleReservation());
        view.addBackButtonListener(e -> {
            view.dispose();  // 현재 ReservationView 닫기

            // UserMainController 생성 (기존 로그인 정보 전달)
            new UserMainController(userId, userType, socket, in, out);
        });

    }

    // 층 데이터 세팅
    private void updateFloorList() {
        String selectedBuilding = view.getSelectedBuilding();
        if (selectedBuilding == null) {
            return;
        }

        List<String> floors = allRooms.stream()
                .filter(r -> r.getBuilding().equals(selectedBuilding))
                .map(RoomModel::getFloor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        view.setFloorList(floors);
        updateRoomList();
    }

    private void updateRoomList() {
        String selectedBuilding = view.getSelectedBuilding();
        String selectedFloor = view.getSelectedFloor();

        if (selectedBuilding == null || selectedFloor == null) {
            return;
        }

        List<String> rooms = allRooms.stream()
                .filter(r -> r.getBuilding().equals(selectedBuilding))
                .filter(r -> r.getFloor().equals(selectedFloor))
                .map(RoomModel::getRoomNumber)
                .sorted()
                .collect(Collectors.toList());

        view.setRoomList(rooms);

        if (!rooms.isEmpty()) {
            view.getRoomComboBox().setSelectedIndex(0);
            updateRoomInfo();
            updateAvailableTimes();
        }
        // 방 정보 즉시 갱신
    }

    private void updateRoomInfo() {
        RoomModel room = getCurrentSelectedRoomModel();
        if (room != null) {
            view.setRoomInfoText(String.format("[%s] %s (수용인원: %d명)",
                    room.getType(), room.getFullName(), room.getCapacity()));
        } else {
            view.setRoomInfoText("");
        }
    }

    private RoomModel getCurrentSelectedRoomModel() {
        String building = view.getSelectedBuilding();
        String floor = view.getSelectedFloor();
        String room = view.getSelectedRoom();

        if (building == null || floor == null || room == null) {
            return null;
        }

        return allRooms.stream()
                .filter(m -> m.getBuilding().equals(building) && m.getFloor().equals(floor) && m.getRoomNumber().equals(room))
                .findFirst()
                .orElse(null);

    }

    private void updateAvailableTimes() {
        view.clearTimeSlots();

        String date = view.getSelectedDate();
        RoomModel room = getCurrentSelectedRoomModel();

        if (date == null || date.isEmpty() || room == null) {
            return;
        }

        String dayOfWeek = getDayOfWeek(date);
        String[] defaultTimes = {
            "09:00~09:50", "10:00~10:50", "11:00~11:50", "12:00~12:50",
            "13:00~13:50", "14:00~14:50", "15:00~15:50", "16:00~16:50",
            "17:00~17:50", "18:00~18:50"
        };

        Map<String, String> scheduleMap = getScheduleForRoom(room, dayOfWeek);

        for (String time : defaultTimes) {
            boolean isAvailable = true;
            if (scheduleMap.containsKey(time)) {
                String status = scheduleMap.get(time);
                if (!"비어있음".equals(status)) {
                    isAvailable = false;
                }
            }

            if (isAvailable && isReservedInFile(room, date, time)) {
                isAvailable = false;
            }

            view.addTimeSlot(time, isAvailable, e -> {
                int total = calculateTotalDuration(view.getSelectedTimes());
                view.setTotalDuration(total + "분");
            });
        }
    }

    private Map<String, String> getScheduleForRoom(RoomModel targetRoom, String day) {
        Map<String, String> result = new HashMap<>();
        if (day.isEmpty() || scheduleData == null || scheduleData.schedules == null) {
            return result;
        }

        scheduleData.schedules.stream()
                .filter(s -> s.buildings.equals(targetRoom.getBuilding()))
                .flatMap(s -> s.floors.stream())
                .filter(f -> f.floor.equals(targetRoom.getFloor()))
                .flatMap(f -> f.roomNumbers.stream())
                .filter(r -> r.roomNumber.equals(targetRoom.getRoomNumber()))
                .flatMap(r -> r.timeSlots.stream())
                .forEach(ts -> {
                    if (ts.availability != null && ts.availability.containsKey(day)) {
                        result.put(ts.time, ts.availability.get(day));
                    }
                });
        return result;
    }

    private boolean isReservedInFile(RoomModel room, String date, String newTimeSlot) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(reservationPath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // CSV 필드: 이름,유형,ID,학과,건물,방유형,방번호,날짜,요일,시작시간,끝시간,목적,상태 (총 13개)
                // 기존 코드는 필드 수가 달랐으므로, 현재 13개 기준으로 인덱스 조정 필요:
                // parts[4]: 건물, parts[6]: 방번호, parts[7]: 날짜, parts[9]: 시작시간, parts[10]: 끝시간
                if (parts.length >= 13) {
                    if (parts[4].equals(room.getBuilding()) && parts[6].equals(room.getRoomNumber()) && parts[7].equals(date)) {
                        Date reservedStart = sdf.parse(parts[9]);
                        Date reservedEnd = sdf.parse(parts[10]);

                        String[] newRange = newTimeSlot.split("~");
                        Date newStart = sdf.parse(newRange[0].trim());
                        Date newEnd = sdf.parse(newRange[1].trim());

                        if (newStart.before(reservedEnd) && newEnd.after(reservedStart)) {
                            // 예약확정 상태만 충돌로 간주
                            if (parts[12].equals("예약확정")) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("파일 예약 충돌 확인 오류: " + e.getMessage());
        }
        return false;
    }

    private void handleReservation() {
        RoomModel room = getCurrentSelectedRoomModel();
        String date = view.getSelectedDate();
        List<String> times = view.getSelectedTimes();
        String purpose = view.getSelectedPurpose();
        int max_user = 15;

        if (room == null || date.isEmpty() || times.isEmpty() || purpose.isEmpty()) {
            view.showMessage("모든 정보를 입력해주세요");
            return;
        }

        this.selectedRoom = room;
        this.reserveDate = date;
        this.reserveTimes = times;
        this.reservePurpose = purpose;
        this.reserveRoomNumber = room.getRoomNumber();

        if (new ReservationController().isUserBanned(userId)) {
            view.showMessage("해당 사용자는 예약이 제한되어 있습니다. 관리자에게 문의하세요.");
            return;
        }

        if ("교수".equals(userType)) {
            if ("기타".equals(reservePurpose)) {
                copyReserved = isTimeSlotAlreadyReserved(
                        selectedRoom.getBuilding(),
                        reserveRoomNumber,
                        reserveDate,
                        reserveTimes);
                if (!copyReserved.isEmpty() && copyReserved.size() >= max_user) {
                    view.showMessage("선택한 시간대에 이미 예약 인원수가 가득 찼습니다.");
                    return;
                }
                int totalMinutes = calculateTotalDuration(reserveTimes);
                if (totalMinutes > 180) {
                    view.showMessage("총 예약 시간이 3시간(120분)을 초과할 수 없습니다.");
                    return;
                }
                
                status = "예약확정";
                saveNewReservation();
                view.showMessage("예약이 확정되었습니다.");
            } else if ("세미나".equals(reservePurpose)) {
                
                int totalMinutes = calculateTotalDuration(reserveTimes);
                if (totalMinutes > 180) {
                    view.showMessage("총 예약 시간이 3시간(120분)을 초과할 수 없습니다.");
                    return;
                }
                
                //TODO 동반 학생 설정
                int choice = view.showConfirmDialog("사용자를 지정하시겠습니까?");
                if (choice == 0) {
                    openReservationWithStudentView();                 
                } else {
                  professorReservation();  
                }
            } else {
                professorReservation();
            }
        } else {
            copyReserved = isTimeSlotAlreadyReserved(selectedRoom.getBuilding(), reserveRoomNumber, reserveDate, reserveTimes);
            if (!copyReserved.isEmpty()) {
                boolean professorReservedByClass = copyReserved.stream()
                        .anyMatch(parts -> parts[1].equals("교수") && !"기타".equals(parts[12]));
                if (professorReservedByClass) {
                    view.showMessage("선택한 시간대에 다른 교수님의 수업 예약이 존재합니다.");
                    return;
                }

                if (copyReserved.size() >= max_user) {
                    view.showMessage("선택한 시간대에 이미 예약 인원수가 가득 찼습니다.");
                    return;
                }
                
                int totalMinutes = calculateTotalDuration(reserveTimes);
                if (totalMinutes > 180) {
                    view.showMessage("총 예약 시간이 3시간(120분)을 초과할 수 없습니다.");
                    return;
                }

            }

            // 학생 시간 제한 체크
            int totalMinutes = calculateTotalDuration(reserveTimes);
            if (totalMinutes > 120) {
                view.showMessage("총 예약 시간이 2시간(120분)을 초과할 수 없습니다.");
                return;
            }

            // 학생 하루 1회 제한 체크
            if (isUserAlreadyReserved(userId, reserveDate)) {
                view.showMessage("학생은 하루 1회만 예약할 수 있습니다.");
                return;
            }

            status = "예약대기";
            saveNewReservation();
            view.showMessage("예약이 등록되었습니다. 관리자의 승인을 기다리는 중입니다.");
        }
    }

    public void saveNewReservation() {
        dayOfWeek = getDayOfWeek(reserveDate);
        
        List<Reservation> reservations = new ArrayList<>();
        
        for (String time : reserveTimes) {
            String[] split = time.split("~");
            if (split.length == 2) {
                String start = split[0].trim();
                String end = split[1].trim();
                
                try {
                    // Builder 패턴으로 예약 객체 생성
                    Reservation reservation = new Reservation.Builder(
                                userName, // name
                                userType, // userType
                                userId, // userId
                                userDept, // userDept
                                selectedRoom.getBuilding(), // building
                                selectedRoom.getRoomNumber(), // roomNumber
                                reserveDate, // date
                                start, // startTime
                                end // endTime
                        ).roomType(selectedRoom.getType()).dayOfWeek(dayOfWeek).purpose(reservePurpose).status(status).build();
                    
                    reservations.add(reservation);
                    
                } catch (IllegalArgumentException e) {
                    view.showMessage("예약 정보 검증 실패: " + e.getMessage());
                    return;
                }
            }
        }
        
        // Repository를 통한 일괄 저장
        boolean result = reservationRepository.saveAll(reservations);
        
        if (!result) {
            view.showMessage("파일 저장이 안되었습니다. 관리자에게 연락해주세요.");
        }
        updateAvailableTimes(); // UI 갱신
    }
    
    private void openReservationWithStudentView() {
        ReservationWithStudentView studentView = new ReservationWithStudentView();
        ReservationWithStudentController studentController = new ReservationWithStudentController(
                in, out, this, selectedRoom, reserveDate, reserveTimes, reservePurpose, userId
        );
        studentView.setController(studentController);
        studentController.setView(studentView);
        studentView.setVisible(true);
        
        view.setVisible(false);
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public ReservationView getView() {
        return this.view;
    }
    
    public void professorReservation() {
        // 기존 예약 내역 확인 
        copyReserved = isTimeSlotAlreadyReserved(selectedRoom.getBuilding(), reserveRoomNumber, reserveDate, reserveTimes);

        removeReservation(); // 파일에서 기존 예약 삭제
        setCancelReservationUser(); // 서버로 보낼 취소 알림 사용자 정보 생성

        status = "예약확정";
        saveNewReservation();

        view.showMessage("교수 예약이 확정되었습니다. 기존 예약자에게는 알림이 전송됩니다.");

        // 서버로 취소 알림 전송
        if (!cancelReservationUser.isEmpty()) {
            sendCancelReservationUser();
        }
    }

    public List<String[]> isTimeSlotAlreadyReserved(String building, String roomNumber, String date, List<String> newTimes) {
        List<Reservation> conflicts = reservationRepository.findConflictReservation(building, roomNumber, date, newTimes);
        
        return conflicts.stream()
                .map(r -> new String[]{
                    r.getName(),           // 0
                    r.getUserType(),       // 1
                    r.getUserId(),         // 2
                    r.getUserDept(),       // 3
                    r.getBuilding(),       // 4
                    r.getRoomType(),       // 5
                    r.getRoomNumber(),     // 6
                    r.getDate(),           // 7
                    r.getDayOfWeek(),      // 8
                    r.getStartTime(),      // 9
                    r.getEndTime(),        // 10
                    r.getPurpose(),        // 11
                    r.getStatus()          // 12
                })
                .collect(Collectors.toList());
    }

    public void removeReservation() {
        if (copyReserved.isEmpty()) {
            return;
        }
        // String[] 배열을 Reservation 객체로 변환
        List<Reservation> reservationsToDelete = 
            reservationRepository.convertFromStringArrayList(copyReserved);
        
        // Repository를 통한 삭제
        boolean result = reservationRepository.delete(reservationsToDelete);
        
        if (!result) {
            System.err.println("예약 삭제 실패");
        }
    }

    public void setCancelReservationUser() {
        if (copyReserved.isEmpty()) {
            return;
        }
        cancelReservationUser.clear();
        for (String[] user : copyReserved) {
            String name = user[0];
            String id = user[2];
            String roomFullName = user[4] + " " + user[6]; // 건물 + 방번호
            String date = user[7];
            String startTime = user[9];
            String endTime = user[10];

            // 서버 전송 형식: 이름,ID,건물호수,날짜_시간
            String result = String.format("%s,%s,%s,%s %s~%s", name, id, roomFullName, date, startTime, endTime);
            cancelReservationUser.add(result);
        }
    }

    public void sendCancelReservationUser() {
        StringBuilder sb = new StringBuilder("CANCEL_RESERVATION:");
        for (String user : cancelReservationUser) {
            if (sb.length() > "CANCEL_RESERVATION:".length()) {
                sb.append(";");
            }
            sb.append(user);
        }

        try {
            out.write(sb.toString());
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            System.out.println("취소 알림 서버 전송 실패: " + ex.getMessage());
        }
    }
    //테스트,교수,dhkdrjs,소프트웨어,정보관,강의실,101,2025-11-19,수,10:00,10:50,강의 전용,예약확정
//    public boolean saveReservation(String name, String userType, String userId, String userDept,
//            String building, String room, String roomNumber, String date,
//            String dayOfWeek, String start, String end, String purpose, String status) {
//        // 데이터 형식: name, userType, userId, department, building, roomType, roomNumber, date, dayOfWeek, startTime, endTime, purpose, status
//       
//        String filePath = "src/main/resources/reservation.txt";
//        try (BufferedWriter writer = new BufferedWriter(
//                new OutputStreamWriter(new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {
//            
//            writer.write(String.join(",", name, userType, userId, userDept, building,
//                    room, roomNumber, date, dayOfWeek, start, end, purpose, status));
//            writer.newLine();
//            
//            return true;
//        } catch (IOException e) {
//            System.err.println("예약 저장 실패: " + e.getMessage());
//            return false;
//        }
//    }

    public int calculateTotalDuration(List<String> times) {
        int total = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        for (String time : times) {
            try {
                String[] parts = time.split("~");
                Date start = sdf.parse(parts[0]);
                Date end = sdf.parse(parts[1]);
                long diff = (end.getTime() - start.getTime()) / (1000 * 60);
                total += diff;
            } catch (ParseException e) {
                System.err.println("시간 파싱 오류: " + time);
            }
        }
        return total;
    }

    public boolean isUserAlreadyReserved(String userId, String date) {
        return reservationRepository.exists(userId, date);
    }

    public String getDayOfWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY ->
                    "일";
                case Calendar.MONDAY ->
                    "월";
                case Calendar.TUESDAY ->
                    "화";
                case Calendar.WEDNESDAY ->
                    "수";
                case Calendar.THURSDAY ->
                    "목";
                case Calendar.FRIDAY ->
                    "금";
                case Calendar.SATURDAY ->
                    "토";
                default ->
                    "";
            };
        } catch (Exception e) {
            return "";
        }
    }

    //서버에서 불러오기
    private void initializeUserInfoFromServer() {
        try {
            out.write("INFO_REQUEST:" + userId + "\n");
            out.flush();

            //socket.setSoTimeout(5000);
            String response = in.readLine();
            //socket.setSoTimeout(0);
            System.out.println(" 서버 응답: " + response);

            if (response != null && response.startsWith("INFO_RESPONSE:")) {
                String[] parts = response.substring("INFO_RESPONSE:".length()).split(",");
                System.out.println("분해된 응답: " + Arrays.toString(parts));

                if (parts.length >= 4) {
                    this.userName = parts[1];  //  이름
                    this.userDept = parts[2];  //  학과
                    this.userType = parts[3];  //  역할
                    view.setUserInfo(this.userName, userId, this.userDept);
                } else {
                    System.out.println(" 응답 형식 오류: 5개 요소가 아님");
                }
            } else {
                System.out.println(" 서버 응답 없음 또는 형식 오류");
            }
        } catch (IOException e) {
            System.out.println(" 사용자 정보 요청 실패: " + e.getMessage());
        }
    }
}

// --- rooms.json 매핑 클래스 ---
class RoomStaticRoot {

    List<BuildingStatic> buildings;
}

class BuildingStatic {

    String name;
    List<FloorStatic> floors;
}

class FloorStatic {

    String floor;
    List<String> majors; // 사용하지 않지만 매핑 유지
    List<RoomStatic> rooms;
}

class RoomStatic {

    String roomNumber;
    String type;
    int capacity;
    String more; // 사용하지 않지만 매핑 유지
}

// --- schedules.json 매핑 클래스 ---
class ScheduleRoot {

    List<ScheduleBuilding> schedules;
}

class ScheduleBuilding {

    String buildings;
    List<ScheduleFloor> floors;
}

class ScheduleFloor {

    String floor;
    List<ScheduleRoom> roomNumbers;
}

class ScheduleRoom {

    String roomNumber;
    List<TimeSlot> timeSlots;
}

class TimeSlot {

    String time;
    Map<String, String> availability; // "월": "비어있음"
}
