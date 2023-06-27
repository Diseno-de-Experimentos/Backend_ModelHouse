package com.upc.modelhouse;

import com.upc.modelhouse.ServiceManagement.domain.service.ProjectActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import javax.validation.Validator;

import com.upc.modelhouse.ServiceManagement.domain.model.entity.ProjectActivity;
import com.upc.modelhouse.ServiceManagement.domain.model.entity.ProjectResource;
import com.upc.modelhouse.ServiceManagement.domain.model.entity.Proposal;
import com.upc.modelhouse.ServiceManagement.domain.model.entity.Request;
import com.upc.modelhouse.ServiceManagement.domain.persistence.ProjectActivityRepository;
import com.upc.modelhouse.ServiceManagement.domain.persistence.ProposalRepository;
import com.upc.modelhouse.ServiceManagement.service.ProjectActivityServiceImpl;
import com.upc.modelhouse.shared.exception.ResourceNotFoundException;

public class ProjectActivityTests {

    @Mock
    private ProposalRepository proposalRepository;
    @Mock
    private ProjectActivityRepository projectActivityRepository;
    @Mock
    private Validator validator;

    @InjectMocks
    private ProjectActivityServiceImpl projectActivityService;

    private static final Long PROPOSAL_ID = 1L;
    private static final Long ACTIVITY_ID = 1L;
    @Mock
    private ProjectActivityService mockProjectActivityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void countTotalActivitiesTest() {
        // Crear una lista de actividades de prueba
        List<ProjectActivity> projectActivities = new ArrayList<>();
        projectActivities.add(new ProjectActivity());
        projectActivities.add(new ProjectActivity());
        projectActivities.add(new ProjectActivity());

        // Configurar el comportamiento del servicio mock
        when(mockProjectActivityService.findAllProposalId(1L)).thenReturn(projectActivities);

        // Obtener el total de actividades y verificar el resultado
        long totalActivities = projectActivities.size();
        Assertions.assertEquals(3, totalActivities);
    }

    @Test
    public void createProjectActivityTest() {
        // Crear una actividad
        ProjectActivity activity = new ProjectActivity();
        activity.setId(1L);
        activity.setName("Actividad 1");
        activity.setDescription("Descripción de la actividad 1");
        activity.setStatus("IN_PROCESS");
        activity.setStartedAt(new Date());
        activity.setCompletedAt(null);
        activity.setCompletionPercent(0.0f);

        // Verificar los valores de los atributos
        Assertions.assertEquals(1L, activity.getId());
        Assertions.assertEquals("Actividad 1", activity.getName());
        Assertions.assertEquals("Descripción de la actividad 1", activity.getDescription());
        Assertions.assertEquals("IN_PROCESS", activity.getStatus());
        Assertions.assertNotNull(activity.getStartedAt());
        Assertions.assertNull(activity.getCompletedAt());
        Assertions.assertEquals(0.0f, activity.getCompletionPercent(), 0.01);
    }

    @Test
    public void setAndGetProposalTest() {
        // Crear una propuesta
        Proposal proposal = new Proposal();
        proposal.setId(1L);
        proposal.setDescription("Propuesta 1");

        // Crear una actividad
        ProjectActivity activity = new ProjectActivity();
        activity.setProposal(proposal);

        // Verificar la propuesta asociada
        Assertions.assertEquals(proposal, activity.getProposal());
        Assertions.assertEquals(1L, activity.getProposal().getId());
        Assertions.assertEquals("Propuesta 1", activity.getProposal().getDescription());
    }

    @Test
    void testFindAllProposalId() {
        List<ProjectActivity> expectedActivities = Collections.singletonList(createDummyActivity());

        when(projectActivityRepository.findAllByProposalId(eq(PROPOSAL_ID))).thenReturn(expectedActivities);

        List<ProjectActivity> result = projectActivityService.findAllProposalId(PROPOSAL_ID);

        assertEquals(expectedActivities, result);
        verify(projectActivityRepository, times(1)).findAllByProposalId(eq(PROPOSAL_ID));
    }

    @Test
    void testCreate() {
        ProjectActivity projectActivity = createDummyActivity();

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(proposalRepository.findById(eq(PROPOSAL_ID))).thenReturn(Optional.of(new Proposal()));
        when(projectActivityRepository.save(any())).thenReturn(projectActivity);

        ProjectActivity result = projectActivityService.create(PROPOSAL_ID, projectActivity);

        assertEquals(projectActivity, result);
        verify(validator, times(1)).validate(any());
        verify(proposalRepository, times(1)).findById(eq(PROPOSAL_ID));
        verify(projectActivityRepository, times(1)).save(eq(projectActivity));
    }

    @Test
    void testDelete() {
        when(projectActivityRepository.findById(eq(ACTIVITY_ID))).thenReturn(Optional.of(createDummyActivity()));

        ResponseEntity<?> result = projectActivityService.delete(ACTIVITY_ID);

        assertEquals(ResponseEntity.ok().build(), result);
        verify(projectActivityRepository, times(1)).findById(eq(ACTIVITY_ID));
        verify(projectActivityRepository, times(1)).delete(any());
    }

    @Test
    void testUpdate_NonExistentActivity_ThrowsResourceNotFoundException() {
        ProjectActivity updatedActivity = createDummyActivity();
        updatedActivity.setName("Updated Name");

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(projectActivityRepository.findById(eq(ACTIVITY_ID))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectActivityService.update(ACTIVITY_ID, updatedActivity);
        });

        verify(validator, times(1)).validate(any());
        verify(projectActivityRepository, times(1)).findById(eq(ACTIVITY_ID));
        verify(projectActivityRepository, never()).save(any());
    }

    // Helper method to create a dummy ProjectActivity object for testing
    private ProjectActivity createDummyActivity() {
        ProjectActivity activity = new ProjectActivity();
        activity.setId(ACTIVITY_ID);
        activity.setName("Activity 1");
        activity.setDescription("Description of Activity 1");
        activity.setStatus("Pending");
        activity.setStartedAt(new Date());
        activity.setCompletedAt(new Date());
        activity.setCompletionPercent(50.0f);
        activity.setProposal(new Proposal());
        return activity;
    }

    ///// REQUEST AND PROPOSAL /////
    @Test
    public void testCreateRequestWithProposal() {
        // Crear una solicitud
        Request request = new Request();
        request.setStatus("Pendiente");
        request.setDescription("Descripción de la solicitud");

        // Crear una propuesta
        Proposal proposal = new Proposal();
        proposal.setDescription("Descripción de la propuesta");
        proposal.setPrice(100.0f);
        proposal.setStatus("Enviada");

        // Establecer la relación entre la solicitud y la propuesta
        proposal.setRequest(request);
        request.setProposal(proposal);

        // Verificar la asociación
        assertEquals(request, proposal.getRequest());
        assertEquals(proposal, request.getProposal());
    }

    @Test
    public void testUpdateRequestWithProposal() {
        // Crear una solicitud y una propuesta
        Request request = new Request();
        Proposal proposal = new Proposal();

        // Establecer la relación inicial entre la solicitud y la propuesta
        proposal.setRequest(request);
        request.setProposal(proposal);

        // Actualizar la solicitud y la propuesta
        request.setStatus("Aceptada");
        proposal.setStatus("Aprobada");

        // Verificar la asociación actualizada
        assertEquals(request, proposal.getRequest());
        assertEquals(proposal, request.getProposal());
        assertEquals("Aceptada", request.getStatus());
        assertEquals("Aprobada", proposal.getStatus());
    }

    @Test
    public void testDeleteRequestWithProposal() {
        // Crear una solicitud y una propuesta
        Request request = new Request();
        Proposal proposal = new Proposal();

        // Establecer la relación entre la solicitud y la propuesta
        proposal.setRequest(request);
        request.setProposal(proposal);

        // Eliminar la solicitud y la propuesta
        request.setProposal(null);
        proposal.setRequest(null);

        // Verificar que la asociación se haya eliminado
        assertNull(request.getProposal());
        assertNull(proposal.getRequest());
    }

    @Test
    public void testGetRequestWithProposal() {
        // Crear una solicitud y una propuesta
        Request request = new Request();
        Proposal proposal = new Proposal();

        // Establecer la relación entre la solicitud y la propuesta
        proposal.setRequest(request);
        request.setProposal(proposal);

        // Obtener la solicitud desde la propuesta
        Request retrievedRequest = proposal.getRequest();

        // Verificar que la solicitud obtenida es la misma que la original
        assertEquals(request, retrievedRequest);
    }

    @Test
    public void testAcceptRequestAndGenerateProposal() {
        // Crear una solicitud
        Request request = new Request();
        request.setStatus("Pendiente");
        request.setDescription("Descripción de la solicitud");

        // Aceptar la solicitud
        request.setStatus("Aceptada");
        request.setAccepted(true);
        request.setAcceptedAt(new Date());

        // Generar una propuesta basada en la solicitud aceptada
        Proposal proposal = new Proposal();
        proposal.setDescription("Descripción de la propuesta");
        proposal.setPrice(100.0f);
        proposal.setStatus("Generada");
        proposal.setRequest(request);

        // Verificar que la solicitud ha sido aceptada y que la propuesta se generó
        // correctamente
        assertTrue(request.getAccepted());
        assertNotNull(request.getAcceptedAt());
        assertEquals(request, proposal.getRequest());
        assertEquals("Generada", proposal.getStatus());
    }

    @Test
    public void testUpdateProposalResponseDate() {
        // Crear una propuesta
        Proposal proposal = new Proposal();
        proposal.setDescription("Descripción de la propuesta");
        proposal.setPrice(100.0f);
        proposal.setStatus("Enviada");

        // Actualizar la fecha de respuesta de la propuesta
        Date newResponseDate = new Date();
        proposal.setResponseDate(newResponseDate);

        // Verificar que la fecha de respuesta se ha actualizado correctamente
        assertEquals(newResponseDate, proposal.getResponseDate());
    }

    @Test
    public void testBidirectionalRelationship() {
        // Crear una solicitud y una propuesta
        Request request = new Request();
        Proposal proposal = new Proposal();

        // Establecer la relación entre la solicitud y la propuesta
        proposal.setRequest(request);
        request.setProposal(proposal);

        // Verificar la relación inversa
        assertTrue(request.getProposal() == proposal);
        assertTrue(proposal.getRequest() == request);
    }

    // PROJECT RESOURCE AND PROJECT ACTIVITY
    @Test
    public void testAssignResourceToProposal() {
        // Crear un recurso
        ProjectResource resource = new ProjectResource();
        resource.setDescription("Descripción del recurso");
        resource.setQuantity(10);
        resource.setState("Disponible");

        // Crear una propuesta
        Proposal proposal = new Proposal();
        proposal.setDescription("Descripción de la propuesta");

        // Asignar el recurso a la propuesta
        resource.setProposal(proposal);

        // Verificar que el recurso se haya asignado correctamente a la propuesta
        assertEquals(proposal, resource.getProposal());
        assertNotNull(resource.getProposal());
    }

    @Test
    public void testUpdateActivityStatus() {
        // Crear una actividad
        ProjectActivity activity = new ProjectActivity();
        activity.setName("Actividad 1");
        activity.setDescription("Descripción de la actividad");
        activity.setStatus("En progreso");

        // Actualizar el estado de la actividad
        activity.setStatus("Completada");

        // Verificar que el estado se haya actualizado correctamente
        assertEquals("Completada", activity.getStatus());
    }
}
