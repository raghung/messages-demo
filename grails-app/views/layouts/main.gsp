<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
		<r:require modules="application"/>
		<g:layoutHead/>
		<r:layoutResources />
	</head>
	<body>
		<sec:ifLoggedIn>
    		Signed in as <b>${sec.username()}</b>, <g:link controller="logout"> Logout</g:link>
     	</sec:ifLoggedIn>
     	<hr>
		<g:layoutBody/>
		
		<g:javascript library="application"/>
		<r:layoutResources />
	</body>
</html>
