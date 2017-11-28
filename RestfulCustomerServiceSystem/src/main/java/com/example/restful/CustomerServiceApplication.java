// CustomerServiceApplication.java - (insert one line description here)
// (C) Copyright 2017 Hewlett Packard Enterprise Development LP

package com.example.restful;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.example.restful.services.CustomerResource;

/**
 *
 */
@ApplicationPath("rest")
public class CustomerServiceApplication extends Application
{

    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();

    public CustomerServiceApplication()
    {
        System.out.println("Adding CustomerResource as singleton");
        singletons.add(new CustomerResource());
        empty.add(CustomerResource.class);
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
