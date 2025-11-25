/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

public class UserAdapter implements IUserAdapter {
    private final IUser user;

    public UserAdapter(IUser user) {
        this.user = user;
    }

    @Override
    public void showInfo() {
        System.out.println("사용자: " + user.getName() + " | 역할: " + user.getRole() + " | 학과: " + user.getDept());
    }

    @Override
    public String getId() { return user.getId(); }

    @Override
    public String getName() { return user.getName(); }

    @Override
    public String getDept() { return user.getDept(); }

    @Override
    public String getRole() { return user.getRole(); }
}

