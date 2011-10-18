package tools.jms;

import java.util.Hashtable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSUtil {

	/**
	 * default wait time when receiving message from queue/topic
	 */
	public static final long DEFAULT_WAITTIME = -1;

	/**
	 * create a context
	 * 
	 * @param properties
	 * @return Context
	 * @throws Exception
	 */
	public static Context createJMSContext(JMSConnectionProperties properties)
			throws Exception {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put(Context.SECURITY_PRINCIPAL, properties.getJndiUserName());
		ht.put(Context.SECURITY_CREDENTIALS, properties.getJndiPwd());
		ht.put(Context.INITIAL_CONTEXT_FACTORY, properties
				.getInitialContextFactory());
		ht.put(Context.PROVIDER_URL, properties.getJmsProviderUrl());
		Context ctx = new InitialContext(ht);
		return ctx;
	}	
	

	/**
	 * Description -- create a queue connection and start it
	 * 
	 * @param context
	 * @param properties
	 * @return a started QueueConnection
	 * @throws Exception
	 */
	public static QueueConnection establishQueueConnection(Context context,
			JMSConnectionProperties properties) throws Exception {
		QueueConnectionFactory qcf = null;
		qcf = (QueueConnectionFactory) context.lookup(properties
				.getQueueConnectionFactory());
		QueueConnection queueConnection = qcf.createQueueConnection(properties
				.getJmsUserName(), properties.getJmsPwd());
		queueConnection.start();
		return queueConnection;
	}

	/**
	 * Description -- First try to get the queue from the Context according to
	 * the qName, if this fails, create the queue.
	 * 
	 * @param ctx
	 * @param qc
	 * @param qName
	 * @return Queue
	 * @throws Exception
	 */
	public static Queue getQueue(Context ctx, QueueConnection qc, String qName)
			throws Exception {
		return getQueue(ctx, qc, qName, true);
	}

	/**
	 * Description -- First try to get the queue from the Context according to
	 * the qName, if this fails, and the createIfNotExist is true, create the
	 * queue.
	 * 
	 * @param ctx
	 * @param qc
	 * @param qName
	 * @param createIfNotExist
	 *            -- flag used to identify whether to create the queue when it
	 *            does not exist
	 * @return Queue or null if the queue does not exist and createIfNotExist is
	 *         false
	 * @throws Exception
	 */
	public static Queue getQueue(Context ctx, QueueConnection qc, String qName,
			boolean createIfNotExist) throws Exception {
		QueueSession queueSession = null;
		try {
			queueSession = qc.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			return getQueue(ctx, queueSession, qName, createIfNotExist);
		} finally {
			closeSessionIgnoreException(queueSession);
		}
	} 
	
	/**
	 * Description -- First try to get the queue from the Context according to
	 * the qName, if this fails, create the queue.
	 * 
	 * @param ctx
	 * @param qs
	 * @param qName
	 * @return Queue
	 * @throws Exception
	 */
	public static Queue getQueue(Context ctx, QueueSession qs, String qName)
			throws Exception {
		return getQueue(ctx, qs, qName, true);
	}
	
	/**
	 * Description -- First try to get the queue from the Context according to
	 * the qName, if this fails, and the createIfNotExist is true, create the
	 * queue.
	 * 
	 * @param ctx
	 * @param qs
	 * @param qName
	 * @param createIfNotExist
	 *            -- flag used to identify whether to create the queue when it
	 *            does not exist
	 * @return Queue or null if the queue does not exist and createIfNotExist is
	 *         false
	 * @throws Exception
	 */
	public static Queue getQueue(Context ctx, QueueSession qs, String qName,
			boolean createIfNotExist) throws Exception {
		try {
			return (Queue) ctx.lookup(qName);
		} catch (NamingException e) {
			// maybe this queue does not exist,ignore this exception
			if (!createIfNotExist) {
				return null;
			}
		}
		return qs.createQueue(qName);
	}

	/**
	 * Create a QueueReceiver to receive Queue message.
	 * 
	 * @return QueueReceiver
	 */

	public static QueueReceiver createQueueReceiver(QueueSession qs,
			Queue queue) throws Exception {
		return qs.createReceiver(queue);		
	}

	/**
	 * Create a QueueReceiver to receive Queue message.
	 * 
	 * @return QueueReceiver
	 */
	public static QueueReceiver createQueueReceiver(Context ctx,
			QueueSession qs, String qName) throws Exception {
		Queue queue = getQueue(ctx, qs, qName);
		return createQueueReceiver(qs, queue);
	}

	/**
	 * This method creates an empty JMS TextMessage for queue.
	 * 
	 * @param QueueConnection
	 * @return TextMessage
	 * @throws Exception
	 */
	public static TextMessage createQueueTextMessage(QueueConnection qc)
			throws Exception {
		QueueSession queueSession = null;
		try {
			queueSession = qc.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			return queueSession.createTextMessage();
		} finally {
			closeSessionIgnoreException(queueSession);
		}
	}
	
	/**
	 * This method creates an empty JMS BytesMessage for queue.
	 * 
	 * @param QueueConnection
	 * @return BytesMessage
	 * @throws Exception
	 */
	public static BytesMessage createQueueBytesMessage(QueueConnection qc)
			throws Exception {
		QueueSession queueSession = null;
		try {
			queueSession = qc.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			return queueSession.createBytesMessage();
		} finally {
			closeSessionIgnoreException(queueSession);
		}
	}
	
	/**
	 * This method creates an empty JMS ObjectMessage for queue.
	 * 
	 * @param QueueConnection
	 * @return ObjectMessage
	 * @throws Exception
	 */
	public static ObjectMessage createQueueObjectMessage(QueueConnection qc)
			throws Exception {
		QueueSession queueSession = null;
		try {
			queueSession = qc.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			ObjectMessage message = queueSession.createObjectMessage();
			return message;
		} finally {
			closeSessionIgnoreException(queueSession);
		}
	}

	/**
	 * This method clears all the messages on a given queue.
	 * 
	 * @param QueueSession
	 * @param queue
	 * @throws Exception
	 */
	public static void purgeQueue(QueueSession qs, Queue queue)
			throws Exception {
		QueueReceiver receiver = null;
		try {
			receiver = createQueueReceiver(qs, queue);
			Message message = receiver.receiveNoWait();
			// keep receiving as long as there are messages on the queue
			while (message != null) {
				message = receiver.receiveNoWait();
			}
		} finally {
			closeMsgConsumerIgnoreException(receiver);
		}

	}

	/**
	 * This method clears all the messages on a given queue.
	 */
	public static void purgeQueue(Context ctx, QueueSession qs,
			String queueName) throws Exception {
		Queue queue = getQueue(ctx, qs, queueName);
		purgeQueue(qs, queue);
	}

	/**
	 * This method uses the specified queue to send the message out
	 */
	public static void sendMessage(QueueConnection qc, Queue queue,
			Message message, int deliveryMode, int priority, long timeToLive)
			throws Exception {
		QueueSession queueSession = null;
		QueueSender queueSender = null;
		try {
			queueSession = qc.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			queueSender = queueSession.createSender(queue);
			queueSender.send(message, deliveryMode, priority, timeToLive);
		} finally {
			closeMsgProducerIgnoreException(queueSender);
			closeSessionIgnoreException(queueSession);
		}
	}

	/**
	 * This method uses the specified queue to send the message out
	 */
	public static void sendMessage(QueueConnection qc, Queue queue,
			Message message) throws Exception {
		sendMessage(qc, queue, message, Message.DEFAULT_DELIVERY_MODE,
				Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
	}

	/**
	 * This method uses the specified queue to send the message out
	 */
	public static void sendMessage(Context ctx, QueueConnection qc,
			String queueName, Message message) throws Exception {
		Queue queue = getQueue(ctx, qc, queueName);
		sendMessage(qc, queue, message);
	}

	/**
	 * This method uses the specified queue to send the message out
	 */
	public static void sendMessage(Context ctx, QueueConnection qc,
			String queueName, String messageContent) throws Exception {
		TextMessage message = createQueueTextMessage(qc);
		message.setText(messageContent);
		sendMessage(ctx, qc, queueName, message);
	}

	/**
	 * This method uses the specified queue to send the message out and it also
	 * specifies the reply queue.
	 */
	public static void sendMessage(Context ctx, QueueConnection qc,
			String queueName, String replyQueueName, Message message)
			throws Exception {
		Queue replyQueue = getQueue(ctx, qc, replyQueueName);
		message.setJMSReplyTo(replyQueue);
		sendMessage(ctx, qc, queueName, message);
	}

	/**
	 * This method uses the specified queue to send the message out and it also
	 * specifies the reply queue.
	 */
	public static void sendMessage(Context ctx, QueueConnection qc,
			String queueName, String replyQueueName, String messageContent)
			throws Exception {
		TextMessage message = createQueueTextMessage(qc);
		message.setText(messageContent);
		sendMessage(ctx, qc, queueName, replyQueueName, message);
	}

	/**
	 * Receive a message from the specified queue during the "waitTime".
	 * If the waitTime <=0, it will not wait
	 * 
	 * @param qs
	 * @param queue
	 * @param waitTime
	 *            -- if this time elapses and we still get no message, return
	 *            null
	 * @return Message or null if "waitTime" elapses and we still get no message
	 * @throws Exception
	 */
	public static Message receiveMessage(QueueSession qs, Queue queue,
			long waitTime) throws Exception {
		QueueReceiver queueReceiver = null;
		try {
			queueReceiver = createQueueReceiver(qs, queue);
			Message message = null;
			// if the wait time is less than 0, invoke receiveNoWait() method
			if (waitTime < 0) {
				message = queueReceiver.receiveNoWait();
			} else {
				message = queueReceiver.receive(waitTime);
			}
			return message;
		} finally {
			closeMsgConsumerIgnoreException(queueReceiver);
		}
	}

	/**
	 * Receive a message from the specified queue
	 * 
	 * @see receiveMessage(QueueSession qs, Queue queue, long waitTime)
	 */
	public static Message receiveMessage(Context ctx, QueueSession qs,
			String queueName, long waitTime) throws Exception {
		Queue queue = getQueue(ctx, qs, queueName);
		return receiveMessage(qs, queue, waitTime);
	}

	/**
	 * Receive a message from the specified queue
	 * 
	 * @see receiveMessage(QueueSession qs, Queue queue, long waitTime)
	 */
	public static Message receiveMessage(Context ctx,QueueSession qs,
			String queueName) throws Exception {
		return receiveMessage(ctx, qs, queueName, DEFAULT_WAITTIME);
	}

	/**
	 * Receive a message(expect it to be TextMessage) from the specified queue
	 * and get the content from it, if this message is not a TextMessage, return
	 * null.
	 * 
	 * @param qs
	 * @param queue
	 * @param waitTime
	 *            -- if this time expires and we still get no message, return
	 *            null
	 * @return String if the received message is a TextMessage, Null otherwise.
	 * @throws Exception
	 */
	public static String receiveText(QueueSession qs, Queue queue,
			long waitTime) throws Exception {
		Message message = receiveMessage(qs, queue, waitTime);
		if (message == null) {
			return null;
		}
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			return textMessage.getText();
		}
		return null;
	}

	/**
	 * Receive the message content
	 * 
	 * @see String receiveText(QueueSession qs, Queue queue, long waitTime)
	 */
	public static String receiveText(Context ctx, QueueSession qs,
			String queueName, long waitTime) throws Exception {
		Queue queue = getQueue(ctx, qs, queueName);
		return receiveText(qs, queue, waitTime);
	}

	/**
	 * Receive the message content
	 * 
	 * @see String receiveText(QueueSession qs, Queue queue, long waitTime)
	 */
	public static String receiveText(Context ctx, QueueSession qs,
			String queueName) throws Exception {
		return receiveText(ctx, qs, queueName, DEFAULT_WAITTIME);
	}

	/**
	 * Description -- create a topic connection and start it
	 * 
	 * @param context
	 * @param properties
	 * @return a started TopicConnection
	 * @throws Exception
	 */
	public static TopicConnection establishTopicConnection(Context context,
			JMSConnectionProperties properties) throws Exception {
		TopicConnectionFactory tcf = null;
		tcf = (TopicConnectionFactory) context.lookup(properties
				.getTopicConnectionFactory());
		TopicConnection topicConnection = tcf.createTopicConnection(properties
				.getJmsUserName(), properties.getJmsPwd());
		topicConnection.start();
		return topicConnection;
	}

	/**
	 * Description -- First try to get the topic from the Context according to
	 * the tName, if this fails, create the topic.
	 * 
	 * @param Context
	 * @param TopicConnection
	 * @param tName
	 *            -- the Name of the Topic
	 * @return Topic
	 * @throws Exception
	 */
	public static Topic getTopic(Context ctx, TopicConnection tc, String tName)
			throws Exception {
		return getTopic(ctx, tc, tName, true);
	}

	/**
	 * Description -- First try to get the topic from the Context according to
	 * the tName, if this fails, and the "createIfNotExist" is true, create the
	 * topic.
	 * 
	 * @param ctx
	 * @param tc
	 * @param tName
	 * @param createIfNotExist
	 *            -- if this flag is true, create the topic if it does not exist
	 * @return Topic or null if the topic does not exist and the
	 *         "createIfNotExist" is false
	 * @throws Exception
	 */
	public static Topic getTopic(Context ctx, TopicConnection tc, String tName,
			boolean createIfNotExist) throws Exception {
		TopicSession topicSession = null;
		try {
			topicSession = tc.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			return getTopic(ctx, topicSession, tName, createIfNotExist);
		} finally {
			closeSessionIgnoreException(topicSession);
		}
	}
	
	/**
	 * Description -- First try to get the topic from the Context according to
	 * the tName, if this fails, create the topic.
	 * 
	 * @param Context
	 * @param TopicSession
	 * @param tName
	 *            -- the Name of the Topic
	 * @return Topic
	 * @throws Exception
	 */
	public static Topic getTopic(Context ctx, TopicSession ts, String tName)
			throws Exception {
		return getTopic(ctx, ts, tName, true);
	}
	
	/**
	 * Description -- First try to get the topic from the Context according to
	 * the tName, if this fails, and the "createIfNotExist" is true, create the
	 * topic.
	 * 
	 * @param ctx
	 * @param ts
	 * @param tName
	 * @param createIfNotExist
	 *            -- if this flag is true, create the topic if it does not exist
	 * @return Topic or null if the topic does not exist and the
	 *         "createIfNotExist" is false
	 * @throws Exception
	 */
	public static Topic getTopic(Context ctx, TopicSession ts, String tName,
			boolean createIfNotExist) throws Exception {
		try {
			return (Topic) ctx.lookup(tName);
		} catch (NamingException e) {
			// maybe this topic does not exist,ignore this exception
			if (!createIfNotExist) {
				return null;
			}
		}
		// create a new topic
		return ts.createTopic(tName);
	}

	/**
	 * Create a TopicSubscriber to subscribe topic messages.
	 * 
	 * @return TopicSubscriber
	 */

	public static TopicSubscriber createTopicSubscriber(TopicSession ts,
			Topic topic) throws Exception {
		return ts.createSubscriber(topic);
	}

	/**
	 * Create a TopicSubscriber to subscribe topic messages.
	 * 
	 * @return TopicSubscriber
	 */
	public static TopicSubscriber createTopicSubscriber(Context ctx,
			TopicSession ts, String tName) throws Exception {
		Topic topic = getTopic(ctx, ts, tName);
		return createTopicSubscriber(ts, topic);
	}

	/**
	 * This method creates an empty JMS TextMessage for topic.
	 * 
	 * @param TopicConnection
	 * @return TextMessage
	 * @throws Exception
	 */
	public static TextMessage createTopicTextMessage(TopicConnection tc)
			throws Exception {
		TopicSession topicSession = null;
		try {
			topicSession = tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			return topicSession.createTextMessage();
		} finally {
			closeSessionIgnoreException(topicSession);
		}
	}

	/**
	 * This method uses a topic to publish a message.
	 */
	public static void publishMessage(TopicConnection tc, Topic topic,
			Message message, int deliveryMode, int priority, long timeToLive)
			throws Exception {
		TopicSession topicSession = null;
		TopicPublisher topicPublisher = null;
		try {
			topicSession = tc.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			topicPublisher = topicSession.createPublisher(topic);
			topicPublisher.publish(message, deliveryMode, priority, timeToLive);
		} finally {
			closeMsgProducerIgnoreException(topicPublisher);
			closeSessionIgnoreException(topicSession);
		}
	}
	
	/**
	 * This method uses a topic to publish a message.
	 */
	public static void publishMessage(Context ctx, TopicConnection tc,
			String topicName, Message message) throws Exception {
		Topic topic = getTopic(ctx, tc, topicName);
		publishMessage(tc, topic, message, Message.DEFAULT_DELIVERY_MODE,
				Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
	}

	/**
	 * This method uses a topic to publish a message.
	 */
	public static void publishMessage(Context ctx, TopicConnection tc,
			String topicName, String messageContent) throws Exception {
		TextMessage message = createTopicTextMessage(tc);
		message.setText(messageContent);
		publishMessage(ctx, tc, topicName, message);
	}
	
	public static void closeContextIgnoreException(Context context) {
		if (context != null) {
			try {
				context.close();
			} catch (Exception e) {
				// Ignore this exception
			}
		}
	}
	
	public static void closeConnectionIgnoreException(Connection connToClose) {
		if (connToClose != null) {
			try {
				connToClose.close();
			} catch (Exception e) {
				// Ignore this exception
			}
		}
	}

	/**
	 * Close a specified Session ignore any exception caused in this process
	 */
	public static void closeSessionIgnoreException(Session sessionToClose) {
		if (sessionToClose != null) {
			try {
				sessionToClose.close();
			} catch (Exception e) {
				// Ignore this exception
			}
		}
	}

	/**
	 * Close a specified MessageConsumer ignore any exception caused in this
	 * process
	 */
	public static void closeMsgConsumerIgnoreException(
			MessageConsumer consumerToClose) {
		if (consumerToClose != null) {
			try {
				consumerToClose.close();
			} catch (Exception e) {
				// Ignore this exception
			}
		}
	}

	/**
	 * Close a specified MessageProducer ignore any exception caused in this
	 * process
	 */
	public static void closeMsgProducerIgnoreException(
			MessageProducer producerToClose) {
		if (producerToClose != null) {
			try {
				producerToClose.close();
			} catch (Exception e) {
				// Ignore this exception
			}
		}
	}
	
	/**
	 * Get message contents 
	 * <li> TextMessage - directly get the message contents
	 * <li> BytesMessage - construct the message contents with "UTF-8" encoding
	 * @param message
	 * @return String or null if any exception occurs
	 */
	public static String getMessageTextContent(Message message) {
		String result = null;
		try {
			if (message instanceof TextMessage) {
				TextMessage textMsg = (TextMessage)message;
				result = textMsg.getText();
			} else if (message instanceof BytesMessage) {
				BytesMessage bytesMsg = (BytesMessage)message;
				byte[] messgeBytes = new byte[(int)bytesMsg.getBodyLength()];
				bytesMsg.readBytes(messgeBytes);
				result = new String(messgeBytes, "UTF-8");
			}
		} catch (Exception e) {
			// Ignore
		}
		return result;
	}
	
	/**
	 * Get the name of the message destination (it may be a valid queue name or topic name)
	 * @param message
	 * @return String or null if the destination can not be retrieved
	 */
	public static String getMessageDestination(Message message) {
		String result = null;
		try {
			Destination dest = message.getJMSDestination();
			if (dest instanceof Queue) {
				Queue queue = (Queue) dest;
				result = queue.getQueueName();
			} else if (dest instanceof Topic) {
				Topic topic = (Topic) dest;
				result = topic.getTopicName();
			}
		} catch (JMSException e) {
			// Ignore
		}
		return result;
	}

}
