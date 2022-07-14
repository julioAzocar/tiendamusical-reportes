package com.devpredator.tiendamusicalreportes.services;

import javax.ws.rs.core.Response;

import com.dropbox.core.v2.DbxClientV2;

import net.sf.jasperreports.engine.JasperPrint;

//proporciona metodos para acceder a la api de dropbox

public interface DropboxAPIService {

	//permite descargar archivo compilado de jasperreport para compilar el reporte y generar pdf
	//DbxClientV2 : objeto conectar a dropbox
	//orderID : numero de pedido
	//cliente : nombre cliente
	Response descargarReporte(DbxClientV2 dbxClientV2,String orderID,String cliente);
	
	//sube reporte a dropbox
	//jasperprint archivo de jasper generado como pdf
	void cargarReporteToDropbox(DbxClientV2 dbxClientV2,String orderID,String cliente, JasperPrint jasperprint);
	
	
}
