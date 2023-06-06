package com.upc.modelhouse.cucumberglue;

import com.upc.modelhouse.security.domain.model.entity.Project;
import com.upc.modelhouse.security.resource.AuthCredentialsResource;
import com.upc.modelhouse.security.resource.BusinessProfile.BusinessProfileDto;
import com.upc.modelhouse.security.resource.Project.CreateProjectDto;
import com.upc.modelhouse.security.resource.Project.ProjectDto;
import com.upc.modelhouse.security.resource.UserResource;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Log4j2
@RunWith(SpringRunner.class)
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProjectTest {
    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String postUrl = "http://localhost";
    private String businessId = "";
    private String token = "";
    private String accountId = "";
    @Given("I am authenticated in the app")
    public void i_am_authenticated_in_the_app(){
        String url = postUrl + ":" + port + "/api/v1/auth/login";
        AuthCredentialsResource credentials = new AuthCredentialsResource();
        credentials.setEmailAddress("jose@gmail.com");
        credentials.setPassword("@#holita");

        ResponseEntity<UserResource> response = restTemplate.postForEntity(url, credentials, UserResource.class);
        token = Objects.requireNonNull(response.getBody()).getToken();
        accountId  = String.valueOf(response.getBody().getId());
        log.info(token);
        Assertions.assertNotNull(credentials);
    }
    @Given("I have permissions as a business role")
    public void i_have_permissions_as_a_business_role(){
        String url = postUrl + ":" + port + "/api/v1/business_profile/account/" + accountId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<CreateProjectDto> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<BusinessProfileDto> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, BusinessProfileDto.class);
        businessId = String.valueOf(Objects.requireNonNull(response.getBody()).getId());
        Assertions.assertNotNull(response);

    }
    @When("^I sending project to be published with title (.*), description (.*) and image (.*)$")
    public void i_sending_project(String title, String description, String image){
        String url = postUrl + ":" + port + "/api/v1/project/businessProfile/" + businessId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        CreateProjectDto project = new CreateProjectDto();

        project.setTitle(title);
        project.setDescription(description);
        project.setImage(image);

        HttpEntity<CreateProjectDto> requestEntity = new HttpEntity<>(project, headers);
        ResponseEntity<ProjectDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ProjectDto.class);
        log.info(project);
        Assertions.assertNotNull(project);
        Assertions.assertNotNull(response);
    }

    @Then("I should be able to see my newly created project")
    public void i_should_be_able_to_see_my_newly_created_project(){
        String url = postUrl + ":" + port + "/api/v1/project/profile/" + businessId;
        ProjectDto myProject = restTemplate.getForObject(url, ProjectDto.class);
        log.info(myProject);
        Assertions.assertNotNull(myProject);
    }

}

