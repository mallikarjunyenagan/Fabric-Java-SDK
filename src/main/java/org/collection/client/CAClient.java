      
package org.collection.client;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.collection.user.UserContext;
import org.collection.util.Util;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

/**
 * Wrapper class for HFCAClient.
 * 
 * @author Vivek Gani
 *
 */

public class CAClient {

	String caUrl;
	Properties caProperties;

	HFCAClient instance;

	UserContext adminContext;

	public UserContext getAdminUserContext() {
		return adminContext;
	}

	/**
	 * Set the admin user context for registering and enrolling users.
	 * 
	 * @param userContext
	 */
	public void setAdminUserContext(UserContext userContext) {
		this.adminContext = userContext;
	}

	/**
	 * Constructor
	 * 
	 * @param caUrl 
	 * @param caProperties
	 * @throws MalformedURLException
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws InvalidArgumentException 
	 * @throws CryptoException 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public CAClient(String caUrl, Properties caProperties) throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
		this.caUrl = caUrl;
		this.caProperties = caProperties;
		init();
	}

	public void init() throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		instance = HFCAClient.createNewInstance(caUrl, caProperties);
		instance.setCryptoSuite(cryptoSuite);
	}

	public HFCAClient getInstance() {
		return instance;
	}

	/**
	 * Enroll admin user.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public UserContext enrollAdminUser(String username, String password) throws Exception {
		UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), username);
		if (userContext != null) {
			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl + " admin is already enrolled.");
			return userContext;
		}
		Enrollment adminEnrollment = instance.enroll(username, password);
		adminContext.setEnrollment(adminEnrollment);
		Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "CA -" + caUrl + " Enrolled Admin.");
		Util.writeUserContext(adminContext);
		return adminContext;
	}

	/**
	 * Register user.
	 * 
	 * @param username
	 * @param organization
	 * @return
	 * @throws Exception
	 */
	public String registerUser(String username, String organization) throws Exception {
		UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), username);
		if (userContext != null) {
			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl +" User " + username+ " is already registered.");
			return null;
		}
		RegistrationRequest rr = new RegistrationRequest(username, organization);
		String enrollmentSecret = instance.register(rr, adminContext);
		Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "CA -" + caUrl + " Registered User - " + username);
		return enrollmentSecret;
	}

	/**
	 * Enroll user.
	 * 
	 * @param user
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public UserContext enrollUser(UserContext user, String secret) throws Exception {
		UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), user.getName());
		if (userContext != null) {
			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl + " User " + user.getName()+" is already enrolled");
			return userContext;
		}
		Enrollment enrollment = instance.enroll(user.getName(), secret);
		user.setEnrollment(enrollment);
		Util.writeUserContext(user);
		Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "CA -" + caUrl +" Enrolled User - " + user.getName());
		return user;
	}

}