package com.devpredator.tiendamusicalreportes.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

//verifica comunicacion de app java con dropbox

public class DropBoxApiTest {

	@Test
	public void test() {
		String TOKEN = "sl.BLSLX-LCr9XDGNt-YJss97HDBl2GFJ2NP1rNEc0w6hG3ZiCzlBArq2M2pje2FcA5yFUIjbMboJssfUnjWHJzPni_cWBgUh9L1IQy-OqhHGjbiiSP1yf2RqYxUMlWmXPekZZI2oY";
	
		DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("devpredator/test-dropbox").build();
		DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig,TOKEN);
		try {
			
			assertNotNull(dbxClientV2);
			
			FullAccount fullAccount = dbxClientV2.users().getCurrentAccount();
			
			System.out.println("Nombre de cuenta : " + fullAccount.getEmail());
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
