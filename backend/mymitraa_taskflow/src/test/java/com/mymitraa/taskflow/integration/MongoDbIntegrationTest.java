package com.mymitraa.taskflow.integration;

import com.mymitraa.taskflow.api_domain.Project;
import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.ProjectRepository;
import com.mymitraa.taskflow.api_repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * MongoDB Authoritative Integration Test Suite
 * 
 * Target Database: mymitraa_taskflow_test (dedicated isolated test database)
 * Prohibits connecting to or touching production database.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.database=mymitraa_taskflow_test",
    "spring.data.mongodb.uri=mongodb://${MONGODB_HOST:localhost}:${MONGODB_PORT:27017}/mymitraa_taskflow_test"
})
public class MongoDbIntegrationTest {

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired(required = false)
    private ProjectRepository projectRepository;

    private static final String TEST_EMAIL = "integration.test.user@mymitraa-test.local";
    private static final String TEST_PROJECT_CODE = "PRJ-TEST-INTEG-999";

    @BeforeEach
    void verifyMongoDbAvailable() {
        try {
            assumeTrue(userRepository != null, "UserRepository bean must be loaded");
            // Ping database to confirm connectivity
            userRepository.count();
        } catch (Exception e) {
            System.err.println("Notice: Local MongoDB daemon not reachable on port 27017: " + e.getMessage());
            assumeTrue(false, "MongoDB daemon offline — integration test marked NOT RUN (not failed). Start MongoDB with: mongod");
        }
    }

    @AfterEach
    void cleanUpTestDatabaseRecords() {
        try {
            if (userRepository != null) {
                userRepository.findByEmail(TEST_EMAIL).ifPresent(u -> userRepository.deleteById(u.getId()));
            }
            if (projectRepository != null) {
                projectRepository.findByProjectId(TEST_PROJECT_CODE).ifPresent(p -> projectRepository.deleteById(p.getId()));
            }
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("MONGO-01: User entity persistence, retrieval, and updates in MongoDB test database")
    void testUserPersistenceLifecycle() {
        User user = new User();
        user.setName("Integ User");
        user.setEmail(TEST_EMAIL);
        user.setUsername("integuser");
        user.setRole("Developer");
        user.setStatus("Active");
        user.setEnabled(true);

        User saved = userRepository.save(user);
        assertNotNull(saved.getId(), "MongoDB should generate a BSON ObjectId string");

        Optional<User> retrieved = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(retrieved.isPresent(), "User should be retrieved from mymitraa_taskflow_test");
        assertEquals("Developer", retrieved.get().getRole());

        // Update lifecycle
        saved.setTeam("Mobile Platforms");
        userRepository.save(saved);

        Optional<User> updated = userRepository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("Mobile Platforms", updated.get().getTeam());

        // Deletion lifecycle
        userRepository.deleteById(saved.getId());
        assertFalse(userRepository.findById(saved.getId()).isPresent(), "User must be permanently removed");
    }

    @Test
    @DisplayName("MONGO-02: Project entity lifecycle and query isolation in MongoDB")
    void testProjectPersistenceLifecycle() {
        Project project = new Project();
        project.setProjectId(TEST_PROJECT_CODE);
        project.setProjectName("Automated Testing Pipeline");
        project.setCustomerName("Quality Assurance Lab");
        project.setStatus("In Progress");

        Project saved = projectRepository.save(project);
        assertNotNull(saved.getId());

        Optional<Project> retrieved = projectRepository.findByProjectId(TEST_PROJECT_CODE);
        assertTrue(retrieved.isPresent());
        assertEquals("Automated Testing Pipeline", retrieved.get().getProjectName());

        projectRepository.deleteById(saved.getId());
        assertFalse(projectRepository.findByProjectId(TEST_PROJECT_CODE).isPresent());
    }
}
