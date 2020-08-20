<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8" />
<title>wSeqHBase</title>
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
				$('html,body').animate({scrollTop:$(this.hash).offset().top},900);
			});
		});
	</script>
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
			var $body   = $('body'),
				$iframe = $body.data('iframe.fv');
			if ($iframe) {
				// Adjust the height of iframe when hiding the picker
				$iframe.height($body.height());
			}
		}

		function adjustByScrollHeight() {
			var $body   = $('body'),
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
			var items =  20;
			var shown =  3;
			$('#loadMore').click(function () {
				$('#showLess').show();
				shown = $('#myList li:visible').size()+1;
				if(shown< items) {$('#myList li:lt('+shown+')').show();}
				else {$('#myList li:lt('+items+')').show();
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

	<!-- software -->
	<div class="list">
		<div class="container">
			<div class="list-info">
				<h3 class="type">wSeqHBase</h3>
			</div>

			<form method="post" action="wSeq_processing.jsp" name="upform"
				enctype="multipart/form-data">
				<div class="grid_3 grid_44">
					<h3>Step 1: Upload vcf & ped Files</h3>
					<div class="form-group">
						<ul id="myList">
							<li><input id="file1" type="file" name="uploadfile1"
								data-show-preview="false"></li>
							<li><input id="file2" type="file" name="uploadfile2"
								data-show-preview="false"></li>
							<li><input id="file3" type="file" name="uploadfile3"
								data-show-preview="false"></li>
							<li><input id="file4" type="file" name="uploadfile4"
								data-show-preview="false"></li>
							<li><input id="file5" type="file" name="uploadfile5"
								data-show-preview="false"></li>
							<li><input id="file6" type="file" name="uploadfile6"
								data-show-preview="false"></li>
							<li><input id="file7" type="file" name="uploadfile7"
								data-show-preview="false"></li>
							<li><input id="file8" type="file" name="uploadfile8"
								data-show-preview="false"></li>
							<li><input id="file9" type="file" name="uploadfile9"
								data-show-preview="false"></li>
							<li><input id="file10" type="file" name="uploadfile10"
								data-show-preview="false"></li>
							<li><input id="file11" type="file" name="uploadfile11"
								data-show-preview="false"></li>
							<li><input id="file12" type="file" name="uploadfile12"
								data-show-preview="false"></li>
							<li><input id="file13" type="file" name="uploadfile13"
								data-show-preview="false"></li>
							<li><input id="file14" type="file" name="uploadfile14"
								data-show-preview="false"></li>
							<li><input id="file15" type="file" name="uploadfile15"
								data-show-preview="false"></li>
							<li><input id="file16" type="file" name="uploadfile16"
								data-show-preview="false"></li>
							<li><input id="file17" type="file" name="uploadfile17"
								data-show-preview="false"></li>
							<li><input id="file18" type="file" name="uploadfile18"
								data-show-preview="false"></li>
							<li><input id="file19" type="file" name="uploadfile19"
								data-show-preview="false"></li>
							<li><input id="file20" type="file" name="uploadfile20"
								data-show-preview="false"></li>
						</ul>
						<div id="showLess">
							<button type="button" class="btn btn-primary">
								<i class="glyphicon glyphicon-chevron-up"></i>
							</button>
						</div>
						<div id="loadMore">
							<button type="button" class="btn btn-primary">
								<i class="glyphicon glyphicon-chevron-down"></i>
							</button>
						</div>
						<input type="hidden" name="todo" value="upload">
					</div>
				</div>
				<div class="clearfix"></div>
				<hr>
				<div class="grid_3 grid_44">
					<h3>Step 2: Biological Function</h3>
					<div class="form-group">
						<SELECT id="func" multiple="multiple" class="form-control"
							name="func">
							<optgroup label="Exonic">
								<option value="frameshift insertion" selected="selected">frameshift</option>
								<option value="stopgain" selected="selected">stopgain</option>
								<option value="stoploss" selected="selected">stoploss</option>
								<option value="nonframeshift insertion">nonframeshift
									insertion</option>
								<option value="nonframeshift deletion">nonframeshift
									deletion</option>
								<option value="nonfgrameshift block substitution">nonframeshift
									block substitution</option>
								<option value="nonsynonymous SNV" selected="selected">nonsynonymous
									SNV</option>
								<option value="synonymous SNV">synonymous SNV</option>
								<option value="unknown">unknown</option>
							</optgroup>
							<optgroup label="Other">
								<option value="splicing" selected="selected">splicing</option>
								<option value="ncRNA">ncRNA</option>
								<option value="UTR5">UTR5</option>
								<option value="UTR3">UTR3</option>
								<option value="intronic">intronic</option>
								<option value="upstream">upstream</option>
								<option value="downstream">downstream</option>
								<option value="intergenic">intergenic</option>
							</optgroup>
						</SELECT>
					</div>
				</div>
				<div class="clearfix"></div>
				<hr>
				<div class="grid_3 grid_44">
					<h3>Step 3: Maximum Variant Frequency</h3>
					<div class="form-group freq">
						<input type="text" class="form-control" name="freq" value="5%">
					</div>
				</div>
				<hr>
				<div class="grid_3 grid_44">
					<h3>Step 4: Minimum Read Depth</h3>
					<div class="form-group rd">
						<input type="text" class="form-control" name="rd" value="20">
					</div>
				</div>
				<div class="clearfix"></div>
				<hr>
				<div class="grid_3 grid_44">
					<h3>Step 5: Email (Enter email to receive analysis results)</h3>
					<div class="input-group email">
						<input type="text" class="form-control" name="email"
							placeholder="Enter email to receive results..."> 
					</div>
				</div>
                                <div class="clearfix"></div>
				<hr>
                                <input type="submit" class="btn btn-primary" value="Submit Request">
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
<script>
    $("#file1").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file2").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file3").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file4").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file5").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file6").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file7").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file8").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file9").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file10").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file11").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file12").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});	
    $("#file13").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file14").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file15").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});		
	$("#file16").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file17").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file18").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
	$("#file19").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});
    $("#file20").fileinput({
        uploadUrl: '#', // you must set a valid URL here else you will get an error
        allowedFileExtensions : ['pdf','json', 'vcf','ped'],
        //allowedFileTypes: ['image', 'video', 'flash'],
        slugCallback: function(filename) {
            return filename.replace('(', '_').replace(')', '_').replace('[', '_').replace(']', '_');
        }
	});	
    /*
    $(".file").on('fileselect', function(event, n, l) {
        alert('File Selected. Name: ' + l + ', Num: ' + n);
    });
    */
	</script>
</html>