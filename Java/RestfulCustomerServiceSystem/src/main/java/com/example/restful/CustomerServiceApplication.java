// CustomerServiceApplication.java - (insert one line description here)

package com.example.restful;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.xml.bind.JAXBException;

import com.example.restful.services.CustomerResource;
import com.example.restful.contenthandlers.JsonContentMarshaller;
import com.example.restful.contenthandlers.JsonContentUnMarshaller;

/**
 *
 */
@ApplicationPath("rest")
public class CustomerServiceApplication extends Application
{

    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();

    public CustomerServiceApplication() throws JAXBException
    {
        singletons.add(new CustomerResource());
        singletons.add(new JsonContentMarshaller());
        singletons.add(new JsonContentUnMarshaller());
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return empty;
    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
