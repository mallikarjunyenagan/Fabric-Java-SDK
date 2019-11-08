
package org.collection.user;

import org.collection.client.CAClient;
import org.collection.config.Config;
import org.collection.response.registerUserResponse;
import org.collection.util.Util;

/**
 * 
 * @author Vivek Gani
 *
 */
public class RegisterEnrollUser {
	registerUserResponse register=new registerUserResponse();
	public String registerEnrollUser() {
		try {
			Util.cleanUp();
			String caUrl = Config.CA_ORG1_URL;
			CAClient caClient = new CAClient(caUrl, null);
			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG1);
			adminUserContext.setMspId(Config.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

			// Register and Enroll user to Org1MSP
			UserContext userContext = new UserContext();
			String name = "user"+System.currentTimeMillis();
			userContext.setName(name);
			userContext.setAffiliation(Config.ORG1);
			userContext.setMspId(Config.ORG1_MSP);

			String eSecret = caClient.registerUser(name, Config.ORG1);

			userContext = caClient.enrollUser(userContext, eSecret);
			
			return "Registered and Enrolled User Successfully";
			


		} catch (Exception e) {
			e.printStackTrace();			
			return "Error occured, Please try again";
		
		}
		
	}

}