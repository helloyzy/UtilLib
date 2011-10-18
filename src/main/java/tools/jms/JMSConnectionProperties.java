package tools.jms;

public class JMSConnectionProperties {
	
	private String jndiUserName;	

	private String jndiPwd;
	
	private String jmsUserName;
	
	private String jmsPwd;
	
	private String jmsProviderUrl;
	
	private String initialContextFactory;
	
	private String queueConnectionFactory;
	
	private String topicConnectionFactory;
	
	public String getJndiUserName() {
		return jndiUserName;
	}

	public void setJndiUserName(String jndiUserName) {
		this.jndiUserName = jndiUserName;
	}

	public String getJndiPwd() {
		return jndiPwd;
	}

	public void setJndiPwd(String jndiPwd) {
		this.jndiPwd = jndiPwd;
	}

	public String getJmsUserName() {
		return jmsUserName;
	}

	public void setJmsUserName(String jmsUserName) {
		this.jmsUserName = jmsUserName;
	}

	public String getJmsPwd() {
		return jmsPwd;
	}

	public void setJmsPwd(String jmsPwd) {
		this.jmsPwd = jmsPwd;
	}

	public String getJmsProviderUrl() {
		return jmsProviderUrl;
	}

	public void setJmsProviderUrl(String jmsProviderUrl) {
		this.jmsProviderUrl = jmsProviderUrl;
	}
	
	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	public void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}

	public String getQueueConnectionFactory() {
		return queueConnectionFactory;
	}

	public String getTopicConnectionFactory() {
		return topicConnectionFactory;
	}
	
	public void setQueueConnectionFactory(String queueConnectionFactory) {
		this.queueConnectionFactory = queueConnectionFactory;
	}

	public void setTopicConnectionFactory(String topicConnectionFactory) {
		this.topicConnectionFactory = topicConnectionFactory;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("JMS connecntion properties: \n");
		
		buffer.append("InitialContextFactory - ");
		buffer.append(initialContextFactory);
		buffer.append("\n");
		
		buffer.append("QueueConnectionFactory - ");
		buffer.append(queueConnectionFactory);
		buffer.append("\n");
		
		buffer.append("TopicConnectionFactory - ");
		buffer.append(topicConnectionFactory);
		buffer.append("\n");
		
		buffer.append("JmsProviderUrl - ");
		buffer.append(jmsProviderUrl);
		buffer.append("\n");
		
		buffer.append("Jms User Name/Password - ");
		buffer.append(jmsUserName);
		buffer.append(":");
		buffer.append(jmsPwd);
		buffer.append("\n");
		
		buffer.append("JNDI User Name/Password - ");
		buffer.append(jndiUserName);
		buffer.append(":");
		buffer.append(jndiPwd);
		buffer.append("\n");
		
		return buffer.toString();
	}

}
