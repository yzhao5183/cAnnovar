<html lang="en">
<%@ page language="java"
	import="org.omics.java.*,java.util.*, java.io.*"%>
<jsp:useBean id="annotateBean" scope="page"
	class="org.omics.java.Annovar">
</jsp:useBean>

<head>
<meta charset="UTF-8" />
<title>Annotation Result</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- bootstarp-css -->
<link href="css/bootstrap.css" rel="stylesheet" type="text/css"
	media="all" />
</head>
<body>
	<%
		String query = request.getParameter("query");
		if(query!=null){
			String str = query;
			String[] output= annotateBean.getVariantQuery(str);
			out.println("<!-- software -->");
			out.println("<div class='list'>");
			out.println("<div class='container'>");
			out.println("<table class='table table-striped'>");
			out.println("  <thead>");
			out.println("    <tr>");
			out.println("      <th>Variable</th>");
			out.println("      <th>Value</th>");
			out.println("    </td></tr>");
			out.println("  </thead>");
			out.println("  <tbody>");
			out.println("    <tr>");
			out.println("    <th scope='row'>Chrom#</th>");
			out.println("      <td>"+output[0]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>Cytoband</th>");
			out.println("      <td>"+output[1]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>start</th>");
			out.println("      <td>"+output[2]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>stop</th>");
			out.println("      <td>"+output[3]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>rel</th>");
			out.println("      <td>"+output[4]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>alt</th>");
			out.println("      <td>"+output[5]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>Func_refGene</th>");
			out.println("      <td>"+output[6]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>Gene_refGene</th>");
			out.println("      <td>"+output[7]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>GeneDetail_refGene</th>");
			out.println("      <td>"+output[8]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>ExonicFunc_refGene</th>");
			out.println("      <td>"+output[9]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>esp6500siv2_all</th>");
			out.println("      <td>"+output[10]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_afr</th>");
			out.println("      <td>"+output[11]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_all</th>");
			out.println("      <td>"+output[12]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_amr</th>");
			out.println("      <td>"+output[13]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_eas</th>");
			out.println("      <td>"+output[14]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_fin</th>");
			out.println("      <td>"+output[15]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_nfe</th>");
			out.println("      <td>"+output[16]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_oth</th>");
			out.println("      <td>"+output[17]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>exac_sas</th>");
			out.println("      <td>"+output[18]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>g1000g2015aug_afr</th>");
			out.println("      <td>"+output[19]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>g1000g2015aug_all</th>");
			out.println("      <td>"+output[20]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>g1000g2015aug_amr</th>");
			out.println("      <td>"+output[21]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>g1000g2015aug_eas</th>");
			out.println("      <td>"+output[22]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>g1000g2015aug_eur</th>");
			out.println("      <td>"+output[23]);
			out.println("    </td></tr>");
			out.println("    <tr>");
			out.println("    <th scope='row'>g1000g2015aug_sas</th>");
			out.println("      <td>"+output[24]);
			out.println("    </td></tr>");
			out.println("  </tbody>");
			out.println("</table>");
			out.println("</div>");
			out.println("</div>");
			out.println("<!-- //software -->");
		}
	%>
</body>
</html>