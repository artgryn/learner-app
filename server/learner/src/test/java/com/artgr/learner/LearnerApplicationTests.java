package com.artgr.learner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

// @AutoConfigureMockMvc here isn't for MockMvc use - it aligns this test's
// Spring context signature with CatalogControllerTest/ProgressControllerTest
// so all three share ONE cached context. Without it, Spring boots a second,
// separate context that also runs spring.sql.init (mode: always) against the
// same live Postgres DB, racing the other context's DROP TABLE/reseed and
// intermittently wiping data mid-suite.
@SpringBootTest
@AutoConfigureMockMvc
class LearnerApplicationTests {

	@Test
	void contextLoads() {
	}

}
