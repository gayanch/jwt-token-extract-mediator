package com.wso2telco.jwt.property.extract.mediator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.Base64;
import java.util.Map;

public class JWTPropertyExtractMediator extends AbstractMediator {
	private String jwtHeaderName;
	private String jwtClaimName;
	private String messageContextProperty;
	private String tenantName;
	
	public boolean mediate(MessageContext context) {
		org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) context)
				.getAxis2MessageContext();
		Map<String, String> headers = (Map) axis2MessageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
		String jwtToken = headers.get(jwtHeaderName);
		if (null == jwtToken) {
			log.error("JWT Token is not available in transport headers under the Header Name provided: " + jwtHeaderName);
			return true;
		}
		String jwtParts[] = jwtToken.split("\\.");
		String jwtAuthSection = jwtParts[1];
		String jsonString = new String(Base64.getDecoder().decode(jwtAuthSection));
		JsonObject jsonJwtAuthSection = new JsonParser().parse(jsonString).getAsJsonObject();
		String jwtClaimValue = jsonJwtAuthSection.get(jwtClaimName).toString();
		String messageContextValue = jwtClaimValue;
		if (tenantName != null && jwtClaimValue.contains(tenantName)) {
			messageContextValue = jwtClaimValue.replace(tenantName, "");
		}
		context.setProperty(messageContextProperty, messageContextValue);
		return true;
	}
	
	public void setJwtHeaderName(String jwtHeaderName) {
		this.jwtHeaderName = jwtHeaderName;
	}
	
	public String getJwtHeaderName() {
		return this.jwtHeaderName;
	}
	
	public void setMessageContextProperty(String  messageContextProperty) {
		this.messageContextProperty = messageContextProperty;
	}
	
	public String getMessageContextProperty() {
		return this.messageContextProperty;
	}

	public String getJwtClaimName() {
		return jwtClaimName;
	}

	public void setJwtClaimName(String jwtClaimName) {
		this.jwtClaimName = jwtClaimName;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
}
