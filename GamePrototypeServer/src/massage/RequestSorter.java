package massage;

/**
 * @author DZQ
 * check and sort the request from a client.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestSorter {
    
    private String welcmeRes = "";

    // check the command from client.
    public String checkCommand(String request) throws Exception {
            if (request.isEmpty()) {
                    return "ReqEmp";
            } else {
                    if (checkFormat("^(id:)[0-9]+", request.toLowerCase())) {
                            welcmeRes = "Sir, your id is " + request.substring(3, request.length()) + ".\r\n";

//                        	System.out.println(request);
                            return "Welcome";
                    } else {

                            if (checkFormat("^[0-9]+(:){1}[a-z]+", request.toLowerCase())) {
                                    String[] splitResult = request.split(":");
                                    String clientId = splitResult[0];

                                    if (checkClientId(clientId)) {
                                    		//even you create many instance of ValidRequestHandler, but they all share the static client id
                                            String req = splitResult[1];
                                            ValidRequestHandler vReq = new ValidRequestHandler();
                                            vReq.ClientId = clientId;
                                            
                                            if (vReq.run(req.toLowerCase())){
                                                    switch (req.toLowerCase()) {
                                                    case "start":
                                                    case "get":
                                                    case "sell":
                                                            return "OK";
                                                    case "quit":
                                                            return "quit";
                                                    default:
//                                                            System.err.println(req.toLowerCase());
                                                            return "InvReq";
                                                    }
                                            }else{
//                                                    System.err.println(req.toLowerCase());
                                                    return "InvReq";
                                            }
                                    } else
                                            return "InvId";

                            } else
                                    return "WroForm";

                    }
            }
    }

    
    // use Regular Expressions to check the format of the request
    private boolean checkFormat(String p, String req) {
            Pattern pattern = Pattern.compile(p);
            Matcher matcher = pattern.matcher(req);
            return matcher.find();
    }

    
    // check the validity of a client id
    private boolean checkClientId(String clientId) throws Exception {
            return checkFormat("^[0-9]{5}", clientId);
    }

    
    // set the reply message
    public String setResponse(String request) throws Exception {
            String response = new String();

            switch (request) {
            case "ReqEmp":
                    response = "At your command, sir!.\r\n";
                    break;
            case "Welcome":
                    response = welcmeRes;
                    break;
            case "InvId":
                    response = "Sir, the id you provided is invalid.\r\n";
                    break;
            case "InvReq":
                    response = "Sir, your command is invalid.\r\n";
                    break;
            case "WroForm":
                    response = "Sir, the format of your command is invalid.\r\n";
                    break;
            case "OK":
                    response = "Yes, sir! \r\n";
                    break;
            case "quit":
                    response = "Have a good day! \r\n";
                    break;
            default:
                    response = "";
                    System.err.println(request);
            }
            return response;
    }

}