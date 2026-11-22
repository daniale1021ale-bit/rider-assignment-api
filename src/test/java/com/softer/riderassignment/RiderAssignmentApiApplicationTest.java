package com.softer.riderassignment;

import com.softer.riderassignment.model.AssignmentStatus;
import com.softer.riderassignment.model.RiderAssignment;
import com.softer.riderassignment.repository.TestH2Repository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class RiderAssignmentApiApplicationTest {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository testH2Repository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/assignments";
    }

    // --------------------------------- Integration Tests ----------------------------------

    @Test
    @DisplayName("should get 0 assignments when table is empty")
    public void test1_testGetAssignmentsWhenTableIsEmpty() {
        List<?> response = restTemplate.getForObject(baseUrl, List.class);
        assertNotNull(response);
        assertEquals(0, response.size());
        assertEquals(0, testH2Repository.findAll().size());
    }


    @Test
    @DisplayName("should get all assignments when table is not empty")
    @Sql(
            statements = "INSERT INTO rider_assignments(delivery_id, rider_id, status, assigned_at) VALUES (7001, 301, 'ASSIGNED', '2026-11-18 10:00:00')",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            statements = "DELETE FROM rider_assignments",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void test2_testGetAssignmentsWhenTableIsNotEmpty() {
        List<?> response = restTemplate.getForObject(baseUrl, List.class);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1, testH2Repository.findAll().size());
    }
}