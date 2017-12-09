package com.example.restful.contenthandlers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.example.restful.domain.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
@Produces("application/json")
public class JsonContentMarshaller implements MessageBodyWriter<Customer>
{
	private ObjectMapper cxt;
	
	public JsonContentMarshaller()
	{
		this.cxt = new ObjectMapper();
	}

	public void writeTo(Customer target, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException 
	{
		try
		{
			cxt.enable(SerializationFeature.INDENT_OUTPUT);
			cxt.writeValue(entityStream, target);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.toString().equals(MediaType.APPLICATION_JSON);
	}
}
