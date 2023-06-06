package com.upc.modelhouse.cucumberglue;

import com.upc.modelhouse.ServiceManagement.resource.Request.CreateRequestDto;
import com.upc.modelhouse.ServiceManagement.resource.Request.RequestDto;
import com.upc.modelhouse.security.resource.AuthCredentialsResource;
import com.upc.modelhouse.security.resource.BusinessProfile.BusinessProfileDto;
import com.upc.modelhouse.security.resource.Project.CreateProjectDto;
import com.upc.modelhouse.security.resource.UserResource;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Log4j2
@RunWith(SpringRunner.class)
public class RequestTest {
    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String postUrl = "http://localhost";
    private String businessId = "";
    private String token = "";
    private String accountId = "";

    @Given("I am the representative who logged in on the app")
    public void i_am_the_representative_who_logged_in_on_the_app(){
        String url = postUrl + ":" + port + "/api/v1/auth/login";
        AuthCredentialsResource credentials = new AuthCredentialsResource();
        credentials.setEmailAddress("rayito@gmail.com");
        credentials.setPassword("#$R12345");

        ResponseEntity<UserResource> res = restTemplate.postForEntity(url, credentials, UserResource.class);
        token = Objects.requireNonNull(res.getBody()).getToken();
        accountId  = String.valueOf(res.getBody().getId());
        log.info(token);
        url = postUrl + ":" + port + "/api/v1/business_profile/account/" + accountId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<CreateProjectDto> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<BusinessProfileDto> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, BusinessProfileDto.class);
        businessId = String.valueOf(Objects.requireNonNull(response.getBody()).getId());

        Assertions.assertNotNull(credentials);
        Assertions.assertNotNull(response);
    }
    @When("^I receive the service request with description (.*)$")
    public void i_receive_the_service_request(String description){
        String url = postUrl + ":" + port + "/api/v1/request/user/2/business/" + businessId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        CreateRequestDto request = new CreateRequestDto();
        request.setStatus("IN_PROCESS");
        request.setDescription(description);
        request.setAccepted(false);
        HttpEntity<CreateRequestDto> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<RequestDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, RequestDto.class);
        log.info(request);
        Assertions.assertNotNull(response);
    }
    @Then("I should be able to see the request detail")
    public void i_should_be_able_to_see_the_request_detail(){
        String url = postUrl + ":" + port + "/api/v1/request/business/" + businessId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<RequestDto> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<List<RequestDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<RequestDto>>() {}
        );

        List<RequestDto> requestReceivedList = response.getBody();

        Assertions.assertNotNull(requestReceivedList);
    }
}
