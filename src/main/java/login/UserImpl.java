/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

public class UserImpl implements IUser {
    private final String id;
    private final String name;
    private final String dept;
    private final String role;

    public UserImpl(String id, String role, String name, String dept) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.dept = dept;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public String getDept() { return dept; }

    @Override
    public String getRole() { return role; }
}
