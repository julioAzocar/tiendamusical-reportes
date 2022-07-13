package com.devpredator.tiendamusicalreportes.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

@Component
@Path("/reportesWS")
public class ReportesWS {
	//http://localhost:8080/tiendamusical-reportes/devpredator/reportesWS/pruebasWS
	@GET
	@Path("/pruebasWS")
	public String pruebaWS() {
		return "Ingresando al webservices ReportesWS";
	}
	
	
}
