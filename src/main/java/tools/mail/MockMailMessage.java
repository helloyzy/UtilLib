package tools.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

public class MockMailMessage extends Message {
	
	String subject;
	
	String content;
	
	public MockMailMessage() {
		super();
	}

	@Override
	public void addFrom(Address[] arg0) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addRecipients(RecipientType arg0, Address[] arg1)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public Flags getFlags() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Address[] getFrom() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getReceivedDate() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Address[] getRecipients(RecipientType arg0)
			throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getSentDate() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubject() throws MessagingException {
		return subject;
	}

	@Override
	public Message reply(boolean arg0) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveChanges() throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFlags(Flags arg0, boolean arg1)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFrom() throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFrom(Address arg0) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRecipients(RecipientType arg0, Address[] arg1)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSentDate(Date arg0) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSubject(String arg0) throws MessagingException {
		this.subject = arg0;
		
	}

	public int getSize() throws MessagingException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLineCount() throws MessagingException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getContentType() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isMimeType(String mimeType) throws MessagingException {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDisposition() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDisposition(String disposition) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public String getDescription() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDescription(String description) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public String getFileName() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFileName(String filename) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public InputStream getInputStream() throws IOException, MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataHandler getDataHandler() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getContent() throws IOException, MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDataHandler(DataHandler dh) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void setContent(Object obj, String type) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void setText(String text) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void setContent(Multipart mp) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void writeTo(OutputStream os) throws IOException, MessagingException {
		// TODO Auto-generated method stub
		
	}

	public String[] getHeader(String header_name) throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setHeader(String header_name, String header_value)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void addHeader(String header_name, String header_value)
			throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	public void removeHeader(String header_name) throws MessagingException {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getAllHeaders() throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getMatchingHeaders(String[] header_names)
			throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getNonMatchingHeaders(String[] header_names)
			throws MessagingException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
