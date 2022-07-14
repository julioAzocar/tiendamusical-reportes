package com.devpredator.tiendamusicalreportes.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devpredator.tiendamusicalreportes.services.DropboxAPIService;
import com.devpredator.tiendamusicalreportes.services.JasperReportsService;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;


//clase implementa logica de negocio para cargar y descrgar reportes dropbox 

@Service
public class DropboxAPIServiceImpl implements DropboxAPIService {


	@Value("${spring.dropbox.directorio.reporte}")
	String DIRECTORIO_REPORTES;
	
	@Value("${spring.dropbox.archivo.jrxml}")
	String ARCHIVO_JASPER_JRXML;
	
	@Autowired
	private JasperReportsService jasperReportsServiceImpl;
	
	
	
	@Override
	public Response descargarReporte(DbxClientV2 dbxClientV2, String orderID, String cliente) {
		// TODO Auto-generated method stub
		
		ByteArrayOutputStream archivoBytes = new ByteArrayOutputStream();
		String mensaje="";
		
		try {
			
			//descar archivo jrxml de dropbox
			DbxDownloader<FileMetadata> downloader = dbxClientV2.files().download(DIRECTORIO_REPORTES + ARCHIVO_JASPER_JRXML);
			downloader.download(archivoBytes);
		
			mensaje="Comprobante generador exitosamente";
			
			//compila archivo jasper y llena info consultada en bd
			JasperPrint jasperPrint = this.jasperReportsServiceImpl.CompilarReporteJasper(archivoBytes, orderID);
			
			//subida a dropbox
			this.cargarReporteToDropbox(dbxClientV2, orderID, cliente, jasperPrint);
			
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
		} catch (ClassNotFoundException e) {
			// error driver
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (SQLException e) {
			// error conectarce o sql
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (JRException e) {
			// error generar reporte jasper
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		
		
		return Response.status(Response.Status.OK).entity(mensaje).build();
		
		//com.dropbox.core.InvalidAccessTokenException: {"error_summary": "expired_access_token
		//generate access token en dropbox 
		
	}

	@Override
	public void cargarReporteToDropbox(DbxClientV2 dbxClientV2, String orderID, String cliente,
			JasperPrint jasperprint) throws IOException, JRException, UploadErrorException, DbxException {
		// TODO Auto-generated method stub
		String nombreArchivoPDF = orderID + ".pdf";
		
		//crea archivo temporal 
		File filePDF = File.createTempFile("temp", nombreArchivoPDF);
				
		InputStream archivoExport = new FileInputStream(filePDF);
		
		JRPdfExporter jrPdfExporter = new JRPdfExporter();
		jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperprint));
		jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePDF));
		
		SimplePdfReportConfiguration simplePdfReportConfiguration = new SimplePdfReportConfiguration();
		jrPdfExporter.setConfiguration(simplePdfReportConfiguration);
		jrPdfExporter.exportReport();
				
		UploadBuilder uploadBuilder = dbxClientV2.files().uploadBuilder(this.DIRECTORIO_REPORTES + "/" + cliente + "/" + nombreArchivoPDF);
		uploadBuilder.withClientModified(new Date(filePDF.lastModified()));
		uploadBuilder.withMode(WriteMode.ADD);
		uploadBuilder.withAutorename(true);
		
		uploadBuilder.uploadAndFinish(archivoExport);
		
		archivoExport.close();
		
				
	}

}
