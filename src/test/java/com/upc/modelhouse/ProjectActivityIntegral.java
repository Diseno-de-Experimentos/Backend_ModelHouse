package com.upc.modelhouse;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.upc.modelhouse.ServiceManagement.domain.model.entity.ProjectActivity;
import com.upc.modelhouse.ServiceManagement.domain.model.entity.ProjectResource;
import com.upc.modelhouse.ServiceManagement.domain.model.entity.Proposal;
import com.upc.modelhouse.ServiceManagement.domain.model.entity.Request;

public class ProjectActivityIntegral {

    // REQUEST AND PROPOSAL
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
