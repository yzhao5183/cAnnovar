<!DOCTYPE html>
<html lang="en">
<%@ page language="java"
         import="org.omics.java.*,java.util.*, java.io.*"%>
<%@ page errorPage="ExceptionHandler.jsp"%>
<jsp:useBean id="annotateBean" scope="page"
             class="org.omics.java.cAnnovar">
</jsp:useBean>

<head>
<meta charset="UTF-8" />
<title>Submitted</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="apple-touch-icon" href="images/Double_Helix_website.jpg">
<link rel="shortcut icon" href="images/Double_Helix_website.jpg">
<!-- bootstarp-css -->
<link href="css/bootstrap.css" rel="stylesheet" type="text/css"	media="all" />
<!-- css -->
<link rel="stylesheet" href="css/style.css" type="text/css" media="all" />
</head>
<body>
	<!-- software -->
	<div class="list">
		<div class="container">
			<div class="list-info">
                            <h3 class="type">Annotation Complete!!</h3>	
		<%
			String folder = request.getParameter("folder");
                        String email = request.getParameter("email");
		%> 
                            <p class="type">Sending to <%=email%> ...</p>
		<%      final File dest = new File(folder);
			ArrayList<String> files = FileProcessing.listFilesForFolder(dest);

			String keyspace = "annovar";
                        String[] connectionParameters = {"localhost", "9042", keyspace};

                        Annotation2 an = new Annotation2(connectionParameters, "", "");    
                        ArrayList<String> annotatedFiles = annotateBean.getVariantFile(an,folder,files);
                        an.closeConnection();
			
//		  	org.omics.java.JavaMail.resultMail(email,folder,annotatedFiles);
//		  	org.omics.java.JavaMail.resultMail(email,folder,files);

                %>
                        </div>
                </div>
        </div>
</body>
</html>