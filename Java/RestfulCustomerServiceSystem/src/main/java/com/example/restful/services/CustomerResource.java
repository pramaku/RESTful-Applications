// CustomerResource.java - (insert one line description here)

package com.example.restful.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.example.restful.domain.Customer;

/**
 *
 */
@Path("/customers")
public class CustomerResource
{
    private Map<Integer, Customer> customerDB = new ConcurrentHashMap<Integer, Customer>();
    private AtomicInteger idCounter = new AtomicInteger();

    // POST call to create a new customer resource
    @POST
    @Consumes("application/xml")
    public Response createCustomer(final InputStream is)
    {
        Customer customer = null;
        try
        {
            customer = readCustomer(is);
        }
        catch (final JAXBException e)
        {
            // TODO Auto-generated catch block
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        customer.setId(idCounter.incrementAndGet());
        customerDB.put(customer.getId(), customer);
        return Response.created(URI.create("/customers/" + customer.getId())).build();
    }

    // GET call to retrieve a given customer resource based on ID.
    @GET
    @Path("{id}")
    @Produces("application/xml")
    public StreamingOutput getCustomer(@PathParam("id") final int id)
    {
        final Customer customer = customerDB.get(id);
        if (customer == null)
        {
            System.out.println("No resource with ID - " + id);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return new StreamingOutput()
        {
            public void write(final OutputStream os)
                    throws IOException,
                    WebApplicationException
            {
                try
                {
                    outputCustomer(os, customer);
                }
                catch (final JAXBException e)
                {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
        };
    }

    @GET
    @Path("{firstname}-{lastname}")
    public StreamingOutput getCustomer(
            @PathParam("firstname") final String firstName,
            @PathParam("lastname") final String lastName)
    {
        for (final int id : customerDB.keySet())
        {
            final Customer customer = customerDB.get(id);
            if (customer.getFirstName().equals(firstName)
                    &&
                    customer.getLastName().equals(lastName))
            {
                return new StreamingOutput()
                {
                    public void write(final OutputStream os)
                            throws IOException,
                            WebApplicationException
                    {
                        try
                        {
                            outputCustomer(os, customer);
                        }
                        catch (final JAXBException e)
                        {
                            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                        }
                    }
                };
            }
        }

        // we could not find the customer with given first and last name
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    // PUT call to update a customer resource with given fields
    @PUT
    @Path("{id}")
    @Consumes("application/xml")
    public void updateCustomer(@PathParam("id") final int id, final InputStream is)
    {
        System.out.println("PUT request for id " + id);
        Customer update = null;
        try
        {
            update = readCustomer(is);
        }
        catch (final JAXBException e)
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

    // DELETE call to delete the customer resource
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

    @GET
    @Path("0")
    @Produces("application/xml")
    public StreamingOutput testResource()
    {
        System.out.println("testResource handling GET call");
        return new StreamingOutput()
        {
            public void write(final OutputStream arg0)
                    throws IOException,
                    WebApplicationException
            {
                final PrintStream writer = new PrintStream(arg0);
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
                writer.println("<customer id=\"0\">");
                writer.println("<name>test</name>");
                writer.println("</customer>");
            }
        };
    }

    /**
     * Serialize the given input XML to customer Object.
     *
     * @param is
     * @return
     * @throws JAXBException
     */
    private Customer readCustomer(final InputStream is)
            throws JAXBException
    {
        // TODO Auto-generated method stub
        System.out.println("POST INPUT - " + is.toString());
        final JAXBContext context = JAXBContext.newInstance(Customer.class);
        final Unmarshaller um = context.createUnmarshaller();
        final Customer customer = (Customer) um.unmarshal(is);
        return customer;
    }

    /**
     * Convert the given customer object to XML as output stream
     *
     * @param os
     * @param customer
     * @throws JAXBException
     */
    protected void outputCustomer(final OutputStream os, final Customer customer)
            throws JAXBException
    {
        final JAXBContext context = JAXBContext.newInstance(Customer.class);
        final Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        m.marshal(customer, os);
    }
}
