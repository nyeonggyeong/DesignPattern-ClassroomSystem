package dashboard;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class DashboardFactoryTest {

    @Test
    void testCreateUserDashboard() throws Exception {
        // 가짜 소켓
        Socket fakeSocket = new Socket();

        // 가짜 Writer/Reader
        BufferedWriter fakeOut = new BufferedWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
        BufferedReader fakeIn = new BufferedReader(new StringReader(""));

        Dashboard dashboard = DashboardFactory.createDashboard(
                "user",
                "student",
                "testUser",
                fakeSocket,
                fakeOut,
                fakeIn
        );

        assertTrue(dashboard instanceof UserDashboard);
    }

    @Test
    void testCreateAdminDashboard() throws Exception {
        // 가짜 Reader/Writer
        BufferedWriter fakeOut = new BufferedWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
        BufferedReader fakeIn = new BufferedReader(new StringReader(""));

        Dashboard dashboard = DashboardFactory.createDashboard(
                "admin",
                null,       // subRole 없음
                "adminUser",
                null,       // socket 필요 없음
                fakeOut,
                fakeIn
        );

        assertTrue(dashboard instanceof AdminDashboard);
    }
}
