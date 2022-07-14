package com.devpredator.tiendamusicalreportes.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

//interfaz que contiene los metodos de logica de negocio para compilar y generar reporte pdf

public interface JasperReportsService {

	//Permite compilar el archivo jasper jrxml descargado de dropbox 
	//@throws ClassNotFoundException {@link ClassNotFoundException} connection error de driver
	//@throws SQLException {@link SQLException} connection error al conectarse a la base de datos
	//@throws JRException {@link JRException} error al compilar reporte de jasper
	//@throws IOException {@link IOException} error al cerrar archivos Stream
	JasperPrint CompilarReporteJasper(ByteArrayOutputStream archivoBytes, String orderID) 
			throws ClassNotFoundException, SQLException, JRException, IOException;
	
	
	
}
