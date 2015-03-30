package cxfSoapHeaderParam;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://services.devsumo.com/cxfMessenger/v003")
public interface CXFMessenger {
    @WebMethod
    String sendMessage(
        @WebParam(name="language", 
                  header=true) String language,
        @WebParam(name="recipient") String recipient,
        @WebParam(name="messageContent") String messageContent);
}