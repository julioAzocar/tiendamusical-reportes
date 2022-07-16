package com.devpredator.tiendamusicalreportes.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.devpredator.tiendamusicalreportes.services.MailService;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
//implementa metodos para logica de envio de correo
@Service
public class MailServiceImpl implements MailService {

	@Value("${spring.mail.aws.smpt.host}")
	String host;
	
	@Value("${spring.mail.aws.smpt.user}")
	String user;
	
	@Value("${spring.mail.aws.smpt.password}")
	String password;
	
	@Value("${spring.mail.aws.smpt.sender}")
	String sender;
	
	@Value("${spring.mail.aws.smpt.port}")
	String port;
	
	@Value("${spring.mail.aws.smpt.tls}")
	String tls;
	
	@Value("${spring.dropbox.directorio.reporte}")
	String pathReportesDropbox;
	
	@Override
	public Response enviarEmail(DbxClientV2 dbxClientV2, String destinatario, String cliente, String orderID) {

		//propiedades de conexion con aws ses
		Properties properties = System.getProperties();
		properties.setProperty("mail.smpt.host", this.host);
		properties.setProperty("mail.smpt.auth", "true");
		properties.setProperty("mail.smpt.starttls.enable", this.tls);
		properties.setProperty("mail.smpt.port", this.port);
		
		Session session = Session.getDefaultInstance(properties,new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user,password);
			}
		});
		
		//creando estructura de correo 
		
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(this.sender));//valida correo
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			message.setSubject("Compra realizada exitosamente " + orderID);
			
			//pdf
			ByteArrayOutputStream archivoBytes = new ByteArrayOutputStream();
			DbxDownloader<FileMetadata> downloader = dbxClientV2.files()
					.download(this.pathReportesDropbox + "/" + cliente + "/" + orderID + ".pdf");
			
			downloader.download(archivoBytes);
			
			//cuerpo msg 
			BodyPart bodyParttext = new MimeBodyPart();
			bodyParttext.setText("Has realizado tu compra de manera exitosa, "
					+ "adjunto a este correo podras encontrar tu comprobante de correo");
			
			byte[] bytes = archivoBytes.toByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);
			ByteArrayDataSource bads = new ByteArrayDataSource(inputStream, "application/pdf");
			
			BodyPart bodyPartFile = new MimeBodyPart();
			
			//contenido mensaje
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyParttext);
			bodyPartFile.setDataHandler(new DataHandler(bads));
			bodyPartFile.setFileName("COMPROBANTE-" + orderID + ".pdf");
			multipart.addBodyPart(bodyPartFile);
			
			message.setContent(multipart);
			
			//aws ses
			AmazonSimpleEmailService ses = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1).build();//us-east-1
			
			PrintStream printStream = System.out;
			message.writeTo(printStream);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray())); //aws ses -> formated y raw
			
			SendRawEmailRequest sendRawEmailRequest = new SendRawEmailRequest(rawMessage);
			
			ses.sendRawEmail(sendRawEmailRequest);
			
			return Response.ok().build();
			
		} catch (AddressException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (MessagingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DownloadErrorException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DbxException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		
		
//
//251. Amazon SES - Simple Email Service
//    ses -envio de mail automaticos 
//    -envio de correo, template. ports config 
//    1-iniciar sesion en aws 
//    2-buscar ses 
//    3-Verified identities
//        -email adress ,julio.azocarm@gmail.com , verificar con mail link ses enviado a correo, crear identidad 
//    4-dar click en link de correo recibido , para verificar identidad 
//        Amazon Web Services – Email Address Verification Request in region US East (N. Virg
//        -en aws ses, se actualiza a verificado 
//    5-utilizar julio.azocar@hotmail.com
//    6-iam new user ->devpredator89->Andromeda1980.
//    acceskey: AKIA54WFMYBVZVCZ4THR
//    secre access jey : 8LuQ5qeCWvcOuQuJA+nzuo8kBH8QGi11NQozpUkV
//    7-iam user-selecionar devpredator89->add police->AmazonSESFullAccess
//
//252. Configurando AWS SES en Proyecto Reportes
//    1-project reportes en pom.xml agregar 
//    		<!-- ::::::::: DEPENDENCIAS DE AWS SDK :::::::::::::::: -->
//            <!-- https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk -->
//            <dependency>
//                <groupId>com.amazonaws</groupId>
//                <artifactId>aws-java-sdk</artifactId>
//                <version>1.11.908</version>
//            </dependency>
//    2-agregar en properies 
//            apring.mail.aws.smpt.user = AKIA54WFMYBVZVCZ4THR
//            apring.mail.aws.smpt.password = 8LuQ5qeCWvcOuQuJA+nzuo8kBH8QGi11NQozpUkV
//            apring.mail.aws.smpt.sender = julio.azocar@hotmail.com
//            apring.mail.aws.smpt.port = 587
//    3-new interfaz MailService
//
//
//254. Lógica Envío Notificación - Parte 2
//    1-error de amazon problema de token 
//        jul. 16, 2022 12:22:24 A. M. com.amazonaws.internal.InstanceMetadataServiceResourceFetcher handleException
//        ADVERTENCIA: Fail to retrieve token 
//        AWS credentials from environment variables (AWS_ACCESS_KEY_ID (or AWS_ACCESS_KEY) and AWS_SECRET_KEY
//    2-solucion-falta plugin de amazon para eclipse 
//        en eclipse-marketplace buscar aws toolkit
//        -instalar " aws toolkit for eclipse 2.0"
//        -esperar que instale
//        -esperar pide reiniciar eclipse 
//        -despues de reiniciar pide credenciales de aws 
//            user = AKIA54WFMYBVZVCZ4THR
//            password = 8LuQ5qeCWvcOuQuJA+nzuo8kBH8QGi11NQozpUkV
//        -finish 
//    3-se agrega icono de aws en eclipse- para ingresar key y password de user aws 
//        en icono-preferences 
//    4-error de region se aplica us-east-1
//    
		
//::::::::::::reporte subido a dropbox y enviado a correo hotmail exitosamente ::::::::::::::
		
		
		
    
		
		
		
		
		
		
		
		
		
		
	}

}
