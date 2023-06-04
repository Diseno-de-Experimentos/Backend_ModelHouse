package com.upc.modelhouse;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.security.access.prepost.PreAuthorize;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features/",
        glue = { "com.upc.modelhouse/cucumber.glue"}
)
public class CucumberRunnerTest {
}
