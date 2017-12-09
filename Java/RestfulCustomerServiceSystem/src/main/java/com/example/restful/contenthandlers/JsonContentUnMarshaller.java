package com.example.restful.contenthandlers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.example.restful.domain.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JsonContentUnMarshaller implements MessageBodyReader<Customer> 
{
	private ObjectMapper cxt;
	
	public JsonContentUnMarshaller()
	{
		this.cxt = new ObjectMapper();
	}
	
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.toString().equals(MediaType.APPLICATION_JSON);
	}

	public Customer readFrom(Class<Customer> type, 
							Type genericType, 
							Annotation[] annotations, 
							MediaType mediaType,
							MultivaluedMap<String, String> httpHeaders, 
							InputStream entityStream)
									throws IOException, WebApplicationException {
		return cxt.readValue(entityStream, type);
		
	}
	
	
}
