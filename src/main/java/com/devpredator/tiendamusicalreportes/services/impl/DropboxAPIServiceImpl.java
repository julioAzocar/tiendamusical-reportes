package com.devpredator.tiendamusicalreportes.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devpredator.tiendamusicalreportes.services.DropboxAPIService;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;

import net.sf.jasperreports.engine.JasperPrint;


//clase implementa logica de negocio para cargar y descrgar reportes dropbox 

@Service
public class DropboxAPIServiceImpl implements DropboxAPIService {


	@Value("${spring.dropbox.directorio.reporte}")
	String DIRECTORIO_REPORTES;
	
	@Value("${spring.dropbox.archivo.jrxml}")
	String ARCHIVO_JASPER_JRXML;
	
	
	@Override
	public Response descargarReporte(DbxClientV2 dbxClientV2, String orderID, String cliente) {
		// TODO Auto-generated method stub
		
		ByteArrayOutputStream archivoBytes = new ByteArrayOutputStream();
		String mensaje="";
		
		try {
			DbxDownloader<FileMetadata> downloader = dbxClientV2.files().download(DIRECTORIO_REPORTES + ARCHIVO_JASPER_JRXML);
			downloader.download(archivoBytes);
		
			mensaje="Comprobante generador exitosamente";
			
		} catch (DownloadErrorException e) {
			//error en descarga de dropbox
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DbxException e) {
			// error de comunicacion con dropbox
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (IOException e) {
			// error en archivo
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		
		
		return Response.status(Response.Status.OK).entity(mensaje).build();
		
		//com.dropbox.core.InvalidAccessTokenException: {"error_summary": "expired_access_token
		//generate access token en dropbox 
		
	}

	@Override
	public void cargarReporteToDropbox(DbxClientV2 dbxClientV2, String orderID, String cliente,
			JasperPrint jasperprint) {
		// TODO Auto-generated method stub

	}

}
