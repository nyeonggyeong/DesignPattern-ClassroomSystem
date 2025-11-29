package login;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class AdapterIntegrationTest {
    @Test
    public void testSignupAdapter() {
        // 회원가입 시 User 객체 생성
        IUser newUser = new UserImpl("s001", "학생", "홍길동", "컴퓨터공학과");
        IUserAdapter adapter = new UserAdapter(newUser);
        // 공통 메서드 호출
        adapter.showInfo(); // 콘솔 출력 확인용
        assertEquals("학생", adapter.getRole());
        assertEquals("s001", newUser.getId());
        assertEquals("홍길동", newUser.getName());
    }
    @Test
    public void testLoginAdapterStudent() {
        IUser student = new UserImpl("s002", "학생", "김철수", "전자공학과");
        IUserAdapter adapter = new UserAdapter(student);

        adapter.showInfo();
        assertEquals("학생", adapter.getRole());
        assertEquals("s002", student.getId());
    }
    @Test
    public void testLoginAdapterProfessor() {
        IUser professor = new UserImpl("p100", "교수", "이순신", "기계공학과");
        IUserAdapter adapter = new UserAdapter(professor);

        adapter.showInfo();
        assertEquals("교수", adapter.getRole());
    }
    @Test
    public void testLoginAdapterAdmin() {
        IUser admin = new UserImpl("a900", "관리자", "김유신", "관리부");
        IUserAdapter adapter = new UserAdapter(admin);
        adapter.showInfo();
        assertEquals("관리자", adapter.getRole());
    }
}
