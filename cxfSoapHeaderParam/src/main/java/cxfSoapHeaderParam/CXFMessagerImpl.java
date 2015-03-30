package cxfSoapHeaderParam;

public class CXFMessagerImpl implements CXFMessenger {

	@Override
	public String sendMessage(String language, String recipient,
			String messageContent) {
		System.out.println("language: " + language);
		return null;
	}

}
