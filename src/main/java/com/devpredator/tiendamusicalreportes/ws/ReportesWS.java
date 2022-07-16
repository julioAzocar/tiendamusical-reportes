package com.devpredator.tiendamusicalreportes.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devpredator.tiendamusicalreportes.services.DropboxAPIService;
import com.devpredator.tiendamusicalreportes.services.MailService;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

@Component
@Path("/reportesWS")
public class ReportesWS {
	//TOKEN API DROPBOX
	@Value("${spring.dropbox.access.token}")
	String ACCESS_TOKEN;
	
	//se inyecta servicio dropbox con spring
	@Autowired
	private DropboxAPIService dropboxAPIServiceImpl;
	
	//servicio de correos
	@Autowired
	private MailService mailServiceImpl;
	
	//http://localhost:8080/tiendamusical-reportes/devpredator/reportesWS/pruebasWS
	@GET
	@Path("/pruebasWS")
	public String pruebaWS() {
		return "Ingresando al webservices ReportesWS";
	}
	
	@POST
	@Path("/generarReporte")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)//String APPLICATION_FORM_URLENCODED
	public Response generarReporte(@FormParam("orderId") String orderId,
			@FormParam("cliente") String cliente,
			@FormParam("destinatario") String destinatario) {
		
		DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("dropbox/devpredator").build();
		DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig,ACCESS_TOKEN);
		
		Response response = this.dropboxAPIServiceImpl.descargarReporte(dbxClientV2, orderId, cliente);
		
		Response responseEmail =  this.mailServiceImpl.enviarEmail(dbxClientV2, destinatario, cliente, orderId);
		
		return response;
	}
	//postman test
	//		http://localhost:8080/tiendamusical-reportes/devpredator/reportesWS/generarReporte
	//			body: x-www-form-urlencoded
	//			datos de test orderId:123 cliente="julio" destinatario="julio.azocar@hotmail.com"
	//Server -ejecutar server clean y debug 
	//Dropbox https://www.dropbox.com/developers/
	//	        -aplicaciones TiendaMusical-Reportes -permisos 
	//	            activar 
	//	                files.content.write
	//	                files.content.read
	
	
	
                
                
	
}
