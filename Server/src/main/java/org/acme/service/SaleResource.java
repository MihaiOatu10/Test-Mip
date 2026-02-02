package org.acme.service;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SaleResource {

    @Inject
    SaleService saleService;

    @POST
    public Response createSale(SaleService.SaleRequest request) {
        try {
            var sale = saleService.createSale(request);
            return Response.status(Response.Status.CREATED).entity(sale).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/report")
    public Response getReport() {
        var report = saleService.getGlobalSalesReport();
        return Response.ok(report).build();
    }

    @GET
    public Response listSales() {
        return Response.ok(org.acme.entity.Sale.listAll()).build();
    }
}

