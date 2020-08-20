<html lang="en">
    <%@ page language="java"
             import="org.omics.java.*,java.util.*, java.io.*"%>
    <jsp:useBean id="annotateBean" scope="page"
                 class="org.omics.java.Annovar">
    </jsp:useBean>

    <head>
        <meta charset="UTF-8" />
        <title>Cassandra Annovar</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords"
              content="Travlio Responsive web template, Bootstrap Web Templates, Flat Web Templates, Andriod Compatible web template, 
              Smartphone Compatible web template, free webdesigns for Nokia, Samsung, LG, SonyErricsson, Motorola web design" />
        <script type="application/x-javascript">


            addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } 


        </script>
        <link rel="apple-touch-icon" href="images/Double_Helix_website.jpg">
        <link rel="shortcut icon" href="images/Double_Helix_website.jpg">
        <!-- bootstarp-css -->
        <link href="css/bootstrap.css" rel="stylesheet" type="text/css"
              media="all" />
        <!-- fileinput -->
        <link href="css/fileinput.css" media="all" rel="stylesheet"
              type="text/css" />
        <script
        src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
        <script src="js/fileinput.js" type="text/javascript"></script>
        <script src="js/fileinput_locale_fr.js" type="text/javascript"></script>
        <script src="js/fileinput_locale_es.js" type="text/javascript"></script>
        <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"
        type="text/javascript"></script>
        <!---- start-smoth-scrolling---->
        <script type="text/javascript" src="js/move-top.js"></script>
        <script type="text/javascript" src="js/easing.js"></script>
        <script type="text/javascript">
            jQuery(document).ready(function($) {
            $(".scroll").click(function(event){
            event.preventDefault();
            $('html,body').animate({scrollTop:$(this.hash).offset().top}, 900);
            });
            });</script>
        <!-- Add SelectBox main JS and CSS files -->
        <link rel="stylesheet" href="css/bootstrap-multiselect.css" />
        <script src="js/bootstrap-multiselect.js"></script>
        <script>
            $(document).ready(function() {
            // You don't need to care about onDropdownShow, onDropdownHide options
            // and adjustByScrollHeight(), adjustByHeight() methods
            // They are for this specific demo
            $('#multiselectForm')
                    .formValidation({
                    framework: 'bootstrap',
                            // Exclude only disabled fields
                            // The invisible fields set by Bootstrap Multiselect must be validated
                            excluded: ':disabled',
                            icon: {
                            valid: 'glyphicon glyphicon-ok',
                                    invalid: 'glyphicon glyphicon-remove',
                                    validating: 'glyphicon glyphicon-refresh'
                            },
                            fields: {
                            gender: {
                            validators: {
                            notEmpty: {
                            message: 'The gender is required'
                            }
                            }
                            },
                                    browsers: {
                                    validators: {
                                    callback: {
                                    message: 'Please choose 2-3 browsers you use for developing',
                                            callback: function(value, validator, $field) {
                                            // Get the selected options
                                            var options = validator.getFieldElements('browsers').val();
                                            return (options != null
                                                    && options.length >= 2 && options.length <= 3);
                                            }
                                    }
                                    }
                                    }
                            }
                    })
                    .find('[name="gender"]')
                    .multiselect({
                    onChange: function(element, checked) {
                    $('#multiselectForm').formValidation('revalidateField', 'gender');
                    adjustByScrollHeight();
                    },
                            onDropdownShown: function(e) {
                            adjustByScrollHeight();
                            },
                            onDropdownHidden: function(e) {
                            adjustByHeight();
                            }
                    })
                    .end()
                    .find('[name="browsers"]')
                    .multiselect({
                    enableFiltering: true,
                            includeSelectAllOption: true,
                            // Re-validate the multiselect field when it is changed
                            onChange: function(element, checked) {
                            $('#multiselectForm').formValidation('revalidateField', 'browsers');
                            adjustByScrollHeight();
                            },
                            onDropdownShown: function(e) {
                            adjustByScrollHeight();
                            },
                            onDropdownHidden: function(e) {
                            adjustByHeight();
                            }
                    })
                    .end();
            // You don't need to care about these methods
            function adjustByHeight() {
            var $body = $('body'),
                    $iframe = $body.data('iframe.fv');
            if ($iframe) {
            // Adjust the height of iframe when hiding the picker
            $iframe.height($body.height());
            }
            }

            function adjustByScrollHeight() {
            var $body = $('body'),
                    $iframe = $body.data('iframe.fv');
            if ($iframe) {
            // Adjust the height of iframe when showing the picker
            $iframe.height($body.get(0).scrollHeight);
            }
            }
            });
            $('#multiselectForm')
                    // Initializing formValidation first
                    .formValidation({
                    excluded: ':disabled',
                            ...
                    })
                    .find('[name="gender"]')
                    .multiselect({
                    ...
                    })
                    .end()
                    .find('[name="browsers"]')
                    // And multiselect later
                    .multiselect({
                    enableFiltering: true,
                            includeSelectAllOption: true,
                            ...
                    })
                    .end();
        </script>
        <!-- css -->
        <link rel="stylesheet" href="css/style.css" type="text/css" media="all" />
        <!-- Initialize the plugin: -->
        <script type="text/javascript">
            $(document).ready(function() {
            $('#func').multiselect({
            enableClickableOptGroups: true,
                    includeSelectAllOption: true
            });
            });
        </script>
        <script type="text/javascript">
            $(document).ready(function () {
            // Load the first 3 list items from another HTML file
            //$('#myList').load('externalList.html li:lt(3)');
            $('#myList li:lt(3)').show();
            $('#showLess').hide();
            var items = 20;
            var shown = 3;
            $('#loadMore').click(function () {
            $('#showLess').show();
            shown = $('#myList li:visible').size() + 3;
            if (shown < items) {$('#myList li:lt(' + shown + ')').show(); }
            else {$('#myList li:lt(' + items + ')').show();
            $('#loadMore').hide();
            }
            });
            $('#showLess').click(function () {
            $('#myList li').not(':lt(3)').hide();
            });
            });
        </script>
    </head>
    <body>
        <%
            String query = request.getParameter("query");
            if (query != null) {
                String str = query;
                String[] output = annotateBean.getVariantQuery(str);
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
                out.println("      <td>" + output[0]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>Cytoband</th>");
                out.println("      <td>" + output[1]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>start</th>");
                out.println("      <td>" + output[2]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>stop</th>");
                out.println("      <td>" + output[3]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>rel</th>");
                out.println("      <td>" + output[4]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>alt</th>");
                out.println("      <td>" + output[5]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>Func_refGene</th>");
                out.println("      <td>" + output[6]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>Gene_refGene</th>");
                out.println("      <td>" + output[7]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>GeneDetail_refGene</th>");
                out.println("      <td>" + output[8]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>ExonicFunc_refGene</th>");
                out.println("      <td>" + output[9]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>esp6500siv2_all</th>");
                out.println("      <td>" + output[10]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_afr</th>");
                out.println("      <td>" + output[11]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_all</th>");
                out.println("      <td>" + output[12]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_amr</th>");
                out.println("      <td>" + output[13]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_eas</th>");
                out.println("      <td>" + output[14]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_fin</th>");
                out.println("      <td>" + output[15]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_nfe</th>");
                out.println("      <td>" + output[16]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_oth</th>");
                out.println("      <td>" + output[17]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>exac_sas</th>");
                out.println("      <td>" + output[18]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>g1000g2015aug_afr</th>");
                out.println("      <td>" + output[19]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>g1000g2015aug_all</th>");
                out.println("      <td>" + output[20]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>g1000g2015aug_amr</th>");
                out.println("      <td>" + output[21]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>g1000g2015aug_eas</th>");
                out.println("      <td>" + output[22]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>g1000g2015aug_eur</th>");
                out.println("      <td>" + output[23]);
                out.println("    </td></tr>");
                out.println("    <tr>");
                out.println("    <th scope='row'>g1000g2015aug_sas</th>");
                out.println("      <td>" + output[24]);
                out.println("    </td></tr>");
                out.println("  </tbody>");
                out.println("</table>");
                out.println("</div>");
                out.println("</div>");
                out.println("<!-- //software -->");
            }
        %>

        <!-- software -->
        <div class="list">
            <div class="container">
                <form method="post" action="testAnnovar.jsp">
                    <div class="grid_3 grid_44">
                        <h3>Query</h3>
                        <div class="form-group query">
                            <input type="text" class="form-control" name="query"
                                   value="1,1p31.1,75072264,75072264,G,T">
                        </div>
                    </div>
                    <hr>
                    <input type="submit" class="btn btn-primary" value="Submit">
                </form>
                <form method="post" action="testAnnovar_result.jsp">
                    <div class="grid_3 grid_44">
                        <h3>Query</h3>
                        <div class="form-group query">
                            <input type="text" class="form-control" name="query"
                                   value="chr1:75072264_75072264;G>T">
                        </div>
                    </div>
                    <hr>
                    <input type="submit" class="btn btn-primary" value="Submit">
                </form>
            </div>
        </div>
        <!-- //software -->
        <!-- footer -->
        <div class="footer">
            <!-- container -->
            <div class="container">
                <div class="footer-top">
                    <div class="footer-grids">
                        <div class="col-md-3 footer-logo">
                            <a href="index.html">He Lab@MCRF</a>
                        </div>
                        <div class="col-md-3 f-address">
                            <h5>Address</h5>
                            <ul>
                                <li>1000 N. Oak Avenue</li>
                                <li>Marshfield, WI 54449</li>
                                <li>Telephone: +1 234 567 9871</li>
                                <li>FAX: +1 234 567 9871</li>
                                <li>E-mail : <a href="mailto:HE.MAX@mcrf.mfldclin.edu">HE.MAX@mcrf.mfldclin.edu</a></li>
                            </ul>
                        </div>
                        <div class="col-md-6 f-address f-contact">
                            <h5>Stay in Touch</h5>
                            <form>
                                <input type="text" placeholder="Email address" required="">
                                <input type="submit" value="Subscribe">
                            </form>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                    <div class="footer-bottom">
                        <div class="footer-nav">
                            <ul>
                                <li><a href="index.html" class="active">Home</a></li>
                                <li><a href="people.html">About</a></li>
                                <li><a href="http://helab.omicspace.org/research">Research</a></li>
                                <li><a href="index.html#softwares">Software</a></li>
                                <li><a href="http://helab.omicspace.org/publications">Publication</a></li>
                                <li><a href="contact.html">Contact</a></li>
                            </ul>
                        </div>
                        <div class="copyright">
                            <p>All rights reserved © He Lab @ MCRF</p>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>
            <!-- //container -->
        </div>
        <!-- //footer -->
    </body>
</html>