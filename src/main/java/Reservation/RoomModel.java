/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reservation;

/**
 *
 * @author scq37
 */
public class RoomModel {

    private String type;            //강의실 실습실
    private String building; // 건물
    private String floor; // 층
    private String roomNumber; // 호수
    private String fullName;

    /*
    public Room() {
        this.name = "강의실";
        this.type
        this()
    }
     */
//    public RoomModel(String name, String type, String[] availableTimes) {
//        this.name = name;
//        this.type = type;
//        this.availableTimes = availableTimes;
//    }
//    
    public RoomModel(String roomNumber, String type, String[] timeSlots) {
        this("정보관", "9", roomNumber, type);
    }
    
    public RoomModel(String building, String floor, String roomNumber, String type) {
        this.building = building;
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.type = type;
        this.fullName = String.format("%s-%s층-%s호", building, floor, roomNumber);
    }
    
    public String getBuilding() { return building; }
    
    public String getFloor() { return floor;}
    
    public String getRoomNumber() { return roomNumber; }
    
    public String getFullName() {return fullName; }
    
    public String getName() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }
    
    @Override
    public String toString() { return fullName; }
    
}
