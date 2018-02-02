// 

package com.example.restful.services;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.example.restful.domain.Customer;

/**
 * Controller class to handle rest requests for customer resource
 *
 */
@Path("/customers")
public class CustomerResource
{
    private Map<Integer, Customer> customerDB = new ConcurrentHashMap<Integer, Customer>();
    private AtomicInteger idCounter = new AtomicInteger();

    /**
     * Creates a customer resource on the system with the given data
     *
     * @param is the request data in the input stream
     * @return the HTTP created response on success
     */
    @POST
    @Consumes({"application/xml, application/json"})
    public Response createCustomer(final Customer customer)
    {
    	System.out.println("POST request to create resource " + customer);
        if (customer == null)
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        customer.setId(idCounter.incrementAndGet());
        customerDB.put(customer.getId(), customer);
        return Response.created(URI.create("/customers/" + customer.getId())).build();
    }

    /**
     * GET call to retrieve a given customer resource based on ID
     *
     * @param id the customer id for the requested resource
     * @return the customer resource data in XML
     */
    @GET
    @Path("{id}")
    @Produces({"application/xml, application/json"})
    public Customer getCustomer(@PathParam("id") final int id)
    {
        final Customer customer = customerDB.get(id);
        if (customer == null)
        {
            System.out.println("No resource with ID - " + id);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return customer;
    }

    /**
     * Get the customer resource based on given first and last name
     *
     * @param firstName
     * @param lastName
     * @return the customer resource data in xml
     */
    @GET
    @Path("{firstname}-{lastname}")
    @Produces({"application/xml, application/json"})
    public Customer getCustomer(
            @PathParam("firstname") final String firstName,
            @PathParam("lastname") final String lastName)
    {
        for (final int id : customerDB.keySet())
        {
            final Customer customer = customerDB.get(id);
            if (customer == null)
            {
            	break;
            }
            if (customer.getFirstName().equals(firstName)
                    &&
                    customer.getLastName().equals(lastName))
            {
            	return customer;
            }
        }

        // we could not find the customer with given first and last name
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    /**
     * Update the customer resource with the given details.
     * 
     * @param id the customer resource id to be updated
     * @param is the input customer data to be used for update
     */
    @PUT
    @Path("{id}")
    @Consumes({"application/xml, application/json"})
    public void updateCustomer(@PathParam("id") final int id, final Customer update)
    {
        System.out.println("PUT request for id " + id);
        if (update == null)
        {
            // TODO Auto-generated catch block
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final Customer current = customerDB.get(id);
        if (current == null)
        {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        current.setFirstName(update.getFirstName());
        current.setLastName(update.getLastName());
        current.setCity(update.getCity());
        current.setStreet(update.getStreet());
        current.setState(update.getState());
        current.setZip(update.getZip());
        current.setCountry(update.getCountry());

    }

    /**
     * delete the given customer resource
     * 
     * @param id the id of the customer resource to be deleted
     */
    @DELETE
    @Path("{id}")
    public void deleteCustomer(@PathParam("id") final int id)
    {
        final Customer customer = customerDB.get(id);
        if (customer == null)
        {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        else
        {
            customerDB.remove(id);
        }
    }

    @POST
    @Path("/0")
    @Produces("application/json")
    public void testResource(String path)
    {
        System.out.println("testResource handling PUt call");
        System.out.println(path);
    }
}
