// CustomerResourceTest.java - (insert one line description here)

package com.example.restful.test;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.restful.domain.Customer;

/**
 *
 */
public class CustomerResourceTest
{
    static Client client = null;
    final String scheme = "http";
    final String host = "localhost";
    final int port = 8080;
    final String contextPath = "RestfulCustomerServiceSystem-1.0-SNAPSHOT";
    final String servletURI = "rest";
    final String customer_resource_path = "customers";

    static URI resourceLoc = null;
    static Customer customer = null;

    public URI buildBasicUrl()
            throws URISyntaxException
    {
        final String path = "/" + contextPath + "/" + servletURI + "/" + customer_resource_path;
        final URI uri = new URI(scheme, null, host, port, path, null, null);
        return uri;
    }

    @BeforeClass
    public static void setUp()
    {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void tearDown()
    {
        if (client != null)
        {
            client.close();
        }
    }

    @Test
    public void testCreateCustomer()
            throws JAXBException,
            URISyntaxException
    {
        final Customer customer = new Customer();
        customer.setFirstName("Bill");
        customer.setLastName("Mark");
        customer.setStreet("jfk street");
        customer.setCity("Boston");
        customer.setState("MC");
        customer.setCountry("US");
        customer.setZip("456789");

        final JAXBContext cxt = JAXBContext.newInstance(Customer.class);

        final Marshaller m = cxt.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        final StringWriter postData = new StringWriter();
        m.marshal(customer, postData);
        final Response response = client.target(buildBasicUrl()).request().post(Entity.xml(postData.toString()));
        Assert.assertEquals(201, response.getStatus());
        resourceLoc = response.getLocation();
        response.close();
    }

    @Test
    public void testGetCustomer()
            throws JAXBException,
            URISyntaxException
    {
        testCreateCustomer();

        final String path = resourceLoc.getPath();
        final String idStr = path.substring(path.lastIndexOf('/') + 1);
        final URI uri = URI.create(buildBasicUrl().toString() + "/" + idStr);

        final String response = client.target(uri).request().get(String.class);
        Assert.assertNotNull(response);

        final JAXBContext cxt = JAXBContext.newInstance(Customer.class);
        final Unmarshaller m = cxt.createUnmarshaller();

        final StringReader input = new StringReader(response);
        customer = (Customer) m.unmarshal(input);

        Assert.assertEquals("456789", customer.getZip());
    }

    @Test
    public void testGetCustomerFromName()
    		throws JAXBException, URISyntaxException
    {
    	testCreateCustomer();

        final URI uri = URI.create(buildBasicUrl().toString() + "/" + "Bill-Mark");

        final String response = client.target(uri).request().get(String.class);
        Assert.assertNotNull(response);

        final JAXBContext cxt = JAXBContext.newInstance(Customer.class);
        final Unmarshaller m = cxt.createUnmarshaller();

        final StringReader input = new StringReader(response);
        customer = (Customer) m.unmarshal(input);

        Assert.assertEquals("456789", customer.getZip());
    }
    
    @Test
    public void testUpdateCustomer()
            throws JAXBException,
            URISyntaxException
    {
        // GET a resource first
        testGetCustomer();
        final JAXBContext cxt = JAXBContext.newInstance(Customer.class);

        final Marshaller m = cxt.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        final StringWriter putData = new StringWriter();

        // update the resource
        customer.setCity("Maimi");
        m.marshal(customer, putData);

        final String path = resourceLoc.getPath();
        final String idStr = path.substring(path.lastIndexOf('/') + 1);
        final URI uri = URI.create(buildBasicUrl().toString() + "/" + idStr);

        final Response response = client.target(uri).request().put(Entity.xml(putData.toString()));
        Assert.assertEquals(204, response.getStatus());

        // get the updated resource
        final String responseStr = client.target(uri).request().get(String.class);
        Assert.assertNotNull(responseStr);

        final JAXBContext cxt1 = JAXBContext.newInstance(Customer.class);
        final Unmarshaller um = cxt1.createUnmarshaller();

        final StringReader input = new StringReader(responseStr);
        customer = (Customer) um.unmarshal(input);

        // verify that the update is done
        Assert.assertEquals("Maimi", customer.getCity());

    }

    @Test(expected = NotFoundException.class)
    public void testDeleteCustomer()
            throws JAXBException,
            URISyntaxException
    {
        // Create a resource
        testCreateCustomer();

        final String path = resourceLoc.getPath();
        final String idStr = path.substring(path.lastIndexOf('/') + 1);
        final URI uri = URI.create(buildBasicUrl().toString() + "/" + idStr);

        final Response response = client.target(uri).request().delete();
        Assert.assertEquals(204, response.getStatus());

        // Now GET on same resource should give 404 Not Found
        client.target(uri).request().get(String.class);
    }

}
