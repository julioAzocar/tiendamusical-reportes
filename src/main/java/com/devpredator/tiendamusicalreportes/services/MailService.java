package com.devpredator.tiendamusicalreportes.services;

import javax.ws.rs.core.Response;

import com.dropbox.core.v2.DbxClientV2;

//interfaz proporciona metodos para envio de mail notificacion 
public interface MailService {

	//permite enviar correo atravez de servicio aws ses
	//dbxClientV2 informacion de dropbox
	//destinatario email de destino
	//cliente nombre completo de cliente
	//orderID orden de pedido de compra
	public Response enviarEmail(DbxClientV2 dbxClientV2, String destinatario,String cliente,String orderID);
	
}
