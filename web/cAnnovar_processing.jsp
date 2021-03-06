<!DOCTYPE html>
<html lang="en">
<%@ page language="java"
         import="javazoom.upload.*,java.util.*, java.io.*"%>
<%@ page errorPage="ExceptionHandler.jsp"%>
<jsp:useBean id="upBean" scope="session"
	class="javazoom.upload.UploadBean">
</jsp:useBean>

<head>
<meta charset="UTF-8" />
<title>Processing</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="apple-touch-icon" href="images/Double_Helix_website.jpg">
<link rel="shortcut icon" href="images/Double_Helix_website.jpg">
<!-- bootstarp-css -->
<link href="css/bootstrap.css" rel="stylesheet" type="text/css"	media="all" />
<!-- css -->
<link rel="stylesheet" href="css/style.css" type="text/css" media="all" />
</head>
<body  onload="setTimeout(function() { document.myform.submit() }, 5)">
	<!-- software -->
	<div class="list">
		<div class="container">
			<div class="list-info">
				<h3 class="type">Submitted!!</h3>
		<%
			String email = ""; String folder = "";
			if (MultipartFormDataRequest.isMultipartFormData(request)) {
				// Uses MultipartFormDataRequest to parse the HTTP request.
				MultipartFormDataRequest mrequest = new MultipartFormDataRequest(request);
				String todo = null;
				if (mrequest != null)
					todo = mrequest.getParameter("todo");
				if ((todo != null) && (todo.equalsIgnoreCase("upload"))) {
					String param = "";
					Hashtable files = mrequest.getFiles();
					if (mrequest != null) {
						String[] vals = mrequest.getParameterValues("func");
						if (vals != null) {
							for (int j = 0; j < vals.length; j++) {
//								out.println("<BR> Biological Function=" + vals[j]);
								param = param + "|" + vals[j];
							}
						}
						String freq = mrequest.getParameter("freq");
						String rd = mrequest.getParameter("rd");
						email = mrequest.getParameter("email");
						folder = "/home/yqzhao/Downloads/uploads/" + email + "/" + System.currentTimeMillis();
//						out.println("<BR> Frequency=" + freq);
//						out.println("<BR> Read Depth=" + rd);
//						out.println("<BR> Email=" + email);
//						out.println("<BR> Folder=" + folder);
						param = param + "," + freq + "," + rd;
						if (param.startsWith("|")) {
							param = param.substring(1);
						}
						upBean.setFolderstore(folder);
						PrintWriter writer = new PrintWriter(folder + "/param.txt", "UTF-8");
						writer.println(param);
						writer.close();
					}

					if ((files != null) && (!files.isEmpty())) {
						UploadFile file = (UploadFile) files.get("uploadfile1");
//						if (file != null)
//							out.println("<li>Form fields : uploadfile" + "<BR> Uploaded file : " + file.getFileName()
//									+ " (" + file.getFileSize() + " bytes)" + "<BR> Content Type : "
//									+ file.getContentType());
						// Uses the bean now to store specified by jsp:setProperty at the top.
						if((file.getFileName() != null)&& file.getFileSize()<=1000000000&&(file.getContentType().equals("text/vcard")||file.getContentType().equals("application/octet-stream"))){
							System.out.println(file.getFileSize()+":"+file.getContentType());
                                                        upBean.store(mrequest, "uploadfile1");
						}

					} else {
//						out.println("<li>No uploaded files");
					}
				} 
//				else
//					out.println("<BR> todo=" + todo);
			}
			
//			if(!email.equals("")){
//				org.omics.java.JavaMail.confirmationMail(email);	
//			}
			%> 
                        <p class="type">Will send results to <%=email%> upon completion...</p>
                        </div>
                </div>
        </div>
        <form method="post" name="myform" action="cAnnovar_submitted.jsp" >
            <p>
                <input name="email" value=<%=email%>  type="hidden" />
            </p>
            <p>
                <input name="folder" value=<%=folder%>   type="hidden"/>
            </p>
        </form>                        
</body>
</html>