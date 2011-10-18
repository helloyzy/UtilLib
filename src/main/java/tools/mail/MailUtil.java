package tools.mail;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class MailUtil {

	/**
	 * Description -- get the count of mail for a given mailbox
	 * 
	 * @param host
	 * @param userName
	 * @param password
	 * @return int
	 * @throws Exception
	 */
	public static int getMailCount(String host, String userName, String password)
			throws Exception {
		Message[] mailMessages = receiveMail(host, userName, password, false, false);
		return mailMessages.length;
	}

	/**
	 * Description -- clean a given mailbox
	 * 
	 * @param host
	 * @param userName
	 * @param password
	 * @throws Exception
	 */
	public static void cleanMailbox(String host, String userName,
			String password) throws Exception {
		receiveMail(host, userName, password, true, false);
	}

	/**
	 * Description -- Receive all mails from the mail server using the given
	 * host,userName and password, after this, there will be no mails left in
	 * the server for this user.
	 * 
	 * @param host
	 * @param userName
	 * @param password
	 * @return Message[]
	 */
	public static Message[] receiveMail(String host, String userName,
			String password) throws Exception {
		return receiveMail(host, userName, password, true, true);
	}

	/**
	 * Description -- Receive mails from the mail server using the given
	 * host,userName and password
	 * 
	 * @param host
	 * @param userName
	 * @param password
	 * @param isDeleteMail
	 *            -- flag to identify whether the mails should be deleted
	 * @param isGetMailContent
	 *            -- flag to identify whether to get the mail content, if this
	 *            flag is set to true, "isDeleteMail" flag does not make any
	 *            sense because the mails will be deleted after their contents
	 *            are retrieved
	 * @return Message[]
	 * @throws Exception
	 */
	static Message[] receiveMail(String host, String userName, String password,
			boolean isDeleteMail, boolean isGetMailContent) throws Exception {
		Message[] messages = null;
		Message[] result = null;
		Store mailStore = null;
		Folder inboxFolder = null;
		try {
			// get system properties
			Properties props = System.getProperties();
			props.put("mail.smtp.host", host);
			Session session = Session.getDefaultInstance(props);

			mailStore = session.getStore("pop3");
			mailStore.connect(host, userName, password);

			inboxFolder = mailStore.getFolder("INBOX");
			inboxFolder.open(Folder.READ_WRITE);

			messages = inboxFolder.getMessages();
			result = new Message[messages.length];

			if ((messages != null) && (messages.length > 0)) {
				for (int i = 0; i < messages.length; i++) {
					Message message = messages[i];
					if (isGetMailContent) {
						result[i] = copyMailMessage(message);
					} else {
						result[i] = message;
					}
					if (isDeleteMail) {
						message.setFlag(Flags.Flag.DELETED, true);
					}
				}
			}
		} finally {
			if (inboxFolder != null) {
				try {
					// closing the folder with expunge set to true forces
					// phisically deletion of the messages
					inboxFolder.close(true);
				} catch (Exception ignored) {
				}
			}
			if (mailStore != null) {
				try {
					mailStore.close();
				} catch (Exception ignored) {
				}
			}
		}
		return result;
	}

	static Message copyMailMessage(Message message) throws Exception {
		Message mailMessage = new MockMailMessage();
		mailMessage.setSubject(message.getSubject());
		Part messagePart = message;
		String contentType = messagePart.getContentType();
		if (contentType.startsWith("text/plain")
				|| contentType.startsWith("text/html")) {
			mailMessage.setText((String) message.getContent());
		}
		return mailMessage;
	}

}
