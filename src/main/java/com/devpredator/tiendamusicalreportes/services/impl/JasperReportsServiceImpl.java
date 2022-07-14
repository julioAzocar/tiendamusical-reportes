package com.devpredator.tiendamusicalreportes.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devpredator.tiendamusicalreportes.services.JasperReportsService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;


//implementa metodos de logica negocio de jasperReport
@Service
public class JasperReportsServiceImpl implements JasperReportsService {

	@Value("${spring.datasource.driverClassName}")
	String driver;
	@Value("${spring.datasource.url}")
	String url;
	@Value("${spring.datasource.username}")
	String user;
	@Value("${spring.datasource.password}")
	String password;
	
	@Override
	public JasperPrint CompilarReporteJasper(ByteArrayOutputStream archivoBytes, String orderID) throws ClassNotFoundException, SQLException, JRException, IOException {

		//se obtiene imagen logo de archivo classpath de proyecto
		InputStream imageInputStream = this.getClass().getClassLoader().getResourceAsStream("images/devpredator.jpg");
		
		//envio de parametros para el reporte jrxml
		Map<String, Object> map = new HashMap<>();
		map.put("orderID",orderID);
		map.put("logo",imageInputStream);
		
		//convierte archivo de salida a un flujo de bytes
		byte[] bytes = archivoBytes.toByteArray();
		InputStream archivoInputStream = new ByteArrayInputStream(bytes);
		
		//Se asignan paparemtros de conexion para archivo de jasper
		Class.forName(this.driver);
		Connection connection = DriverManager.getConnection(this.url,this.user,this.password);
		
		JasperReport jasperReport = JasperCompileManager.compileReport(archivoInputStream);
		
		//cierra objetos de archivo
		imageInputStream.close();
		archivoInputStream.close();
		
		//envia a Jasper report reporte, parametros y coneccion
		return JasperFillManager.fillReport(jasperReport, map,connection);
	}

}
