<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>API - ${context}</title>
 

  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
   <style>

   div {
	word-wrap: break-word;
}

pre{
	text-align: left;
	background-color: rgb(244,240,244);
}
    </style>
</head>
<body>
	 
	<h1></h1>	
	<table class="table table-striped" >
 	 <tr >
	<td style="border: 1px solid black; " width="25%"><h3>Functionality</h3></td>
    <td style="border: 1px solid black;" width="15%"><h3>Type</h3></td>
    <td style="border: 1px solid black;" width="60%"><h3>Request Parameters</h3></td> 
   
   </tr>
    <tr>
    <td style="border: 1px solid black;">API Details</td>
    <td style="border: 1px solid black;"><h3>GET</h3></td>
  	<td style="border: 1px solid black;">
  	<div class="container">
 		<a data-target="#demo" data-toggle="collapse" href="#demo"> ${IP}${context}/api/doc</a>
 		 <div  width="50" id="demo" class="collapse">
			Api Details
  		</div>
	</div></td>
 </tr>
 
 
    <tr>
    <td style="border: 1px solid black;">Request access Token</td>
    <td style="border: 1px solid black;"><h3>POST</h3></td>
	<td style="border: 1px solid black;">
  	<div class="container">
  	 <a data-target="#token" data-toggle="collapse" href="#token"> ${IP}${context}/oauth/token</a>
 		 <div width="50" id="token" class="collapse">
		Request Username and Password for oauth:<br>
		<pre>clientId: gmJHRe0BqCZLA8kOQzBKPmmxwjHEou1SZp3Lb6K0<br>clientSecret: 3uucRwadqPRcqbUQO3rFeLQerze83ZG4mnyAFzMS</pre>
Response: 200<br><pre>{
  "access_token": "2a403934-0354-43c8-a607-a489887b5668",
  "token_type": "bearer",
  "expires_in": 86400,
  "scope": "trust"
}
</pre> 
<BR>Response : 401
<pre>
{
  "error": "invalid_client",
  "error_description": "A new version of Liv. is available. Please update app to new version to continue Liv.ing"
}
</pre>

		</div>
	</div></td>
  </tr>
 
 		<!--<tr>
			<td style="border: 1px solid black;">Connect</td>
			<td style="border: 1px solid black;"><h3>GET</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#demo7" data-toggle="collapse" href="#demo7">
						${IP}${context}/api/connect</a>
					<div width="50" id="demo7" class="collapse">
						Request:<br>
						<pre></pre>
						 Response:<br>
						<pre>Status 200:<br>{
  "result": "Success"
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		-->
		<tr>
			<td style="border: 1px solid black;">Get Customer</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#getAccounts" data-toggle="collapse" href="#getAccounts">
						${IP}${context}/api/getCustomer</a>
					<div width="50" id="getAccounts" class="collapse">
						Request:<br>
						<pre>
{
 "authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw="
}
						</pre>
						 Response:<br>
						<pre>Status 200:<br>{
  "result": "success",
  "customer": {
    "email": "DINU@GMAIL.COM",
    "name": "NAME47684966"
  }
}</pre>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
 
 <tr>
			<td style="border: 1px solid black;">MePay</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#valtransfer" data-toggle="collapse" href="#valtransfer">
						${IP}${context}/api/mePay</a>
					<div width="50" id="valtransfer" class="collapse">
						Request:<br>
						<pre>
{
  "authCode": "K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
  "transfer": {
    "amount": "5",
    "mobileNo": "971552658232"
  }
}
						</pre>
						<br> Response:<br>
						<pre>Status 200:<br>{
  "transfer": {
    "amount": 10,
    "mobileNo": "971552658232"
  }
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		<tr>
			<td style="border: 1px solid black;">Transfer</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#transfer" data-toggle="collapse" href="#transfer">
						${IP}${context}/api/balance</a>
					<div width="50" id="transfer" class="collapse">
						Request:<br>
						<pre>{
 "authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
 "accountNumber":"0214768496608"
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
  "balance": 6805.37
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		<tr>
			<td style="border: 1px solid black;">accountListing</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#accountListing" data-toggle="collapse" href="#accountListing">
						${IP}${context}/api/accountListing</a>
					<div width="50" id="accountListing" class="collapse">
						Request:<br>
						<pre>{
 "authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw="
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
    "accounts": [
        {
            "balance": 162361.13,
            "accountNumber": "1014454713403",
            "accountName":"Saving"
        },
        {
            "balance": 0.16,
            "accountNumber": "1064454713413",
            "accountName":"Current_Account"
        },
        {
            "balance": 11722.8,
            "accountNumber": "1104454713401",
            "accountName":""
        },
        {
            "balance": 78.82,
            "accountNumber": "0214108947701",
            "accountName":""
            
        },
        {
            "balance": 525.48,
            "accountNumber": "0214454713404",
            "accountName":""
            
        },
        {
            "balance": 162.04,
            "accountNumber": "0264454713408",
            "accountName":""

        },
        {
            "balance": 456.11,
            "accountNumber": "0384454713412",
            "accountName":""

        }
    ]
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		<tr>
			<td style="border: 1px solid black;">Sewa OutStanding</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#sewaOutstanding" data-toggle="collapse" href="#sewaOutstanding">
						${IP}${context}/api/sewaOutstanding</a>
					<div width="50" id="sewaOutstanding" class="collapse">
						Request:<br>
						<pre>{
	"authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
	"sewa":{
		"number":"3520359894"
	}
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
    "sewa": {
        "amount": "-28196.91"
    }
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		<tr>
			<td style="border: 1px solid black;">Sewa Payment</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#sewaPayment" data-toggle="collapse" href="#sewaPayment">
						${IP}${context}/api/sewaPayment</a>
					<div width="50" id="sewaPayment" class="collapse">
						Request:<br>
						<pre>{
	"authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
	"sewa":{
		"number":"3520359894",
		"amount":"20"
	}
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
    "sewa": {
        "number": "3520359894",
        "amount": "20"
    }
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		
		<tr>
			<td style="border: 1px solid black;">Nol balance</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#nolbalance" data-toggle="collapse" href="#nolbalance">
						${IP}${context}/api/nolBalance</a>
					<div width="50" id="nolbalance" class="collapse">
						Request:<br>
						<pre>{
	"authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
	"nol":{
		"number":"2006701888"
	}
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
    "nol": {
        "amount": "123"
    }
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		<tr>
			<td style="border: 1px solid black;">Nol Payment</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#nolPayment" data-toggle="collapse" href="#nolPayment">
						${IP}${context}/api/nolPayment</a>
					<div width="50" id="nolPayment" class="collapse">
						Request:<br>
						<pre>{
	"authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
	"nol":{
		"number":"2006701888",
		"amount":"20"
	}
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
    "nol": {
        "number": "2006701888",
        "amount": "20"
    }
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		
		<tr>
			<td style="border: 1px solid black;">Salik balance</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#salikbalance" data-toggle="collapse" href="#salikbalance">
						${IP}${context}/api/salikBalance</a>
					<div width="50" id="salikbalance" class="collapse">
						Request:<br>
						<pre>{
	"authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
	"salik":{
		"number":"32100001",
		"pin":6843
	}
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
    "salik": {
        "amount": "123"
    }
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		<tr>
			<td style="border: 1px solid black;">Salik Payment</td>
			<td style="border: 1px solid black;"><h3>POST</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#salikPayment" data-toggle="collapse" href="#salikPayment">
						${IP}${context}/api/salikPayment</a>
					<div width="50" id="salikPayment" class="collapse">
						Request:<br>
						<pre>{
	"authCode":"K+u0K8R52vMZErhD4Yt4h50nvbFQA0QMOrUOE2PcMgw=",
	"salik":{
		"number":"2006701888",
		"amount":"20"
	}
}
	</pre>
						 Response:<br>
						<pre>Status 200:<br>{
    "salik": {
        "number": "2006701888",
        "amount": "20"
    }
}</pre><br>
					
						</pre>

					</div>
				</div>
			</td>
		</tr>
		
		
		<tr>
			<td style="border: 1px solid black;">Mock Response</td>
			<td style="border: 1px solid black;"><h3>Get</h3></td>
			<td style="border: 1px solid black;">
				<div class="container">
					<a data-target="#mockResponnse" data-toggle="collapse" href="#mockResponnse">
						${IP}${context}/api/mockResponse</a>
					<div width="50" id="mockResponnse" class="collapse">
						<pre> What ever the json is in mockResponse file this service will response.
						mockResponse file path = 10.10.10.212 (C:\SCS_MOCK_RESPONSE\mockResponse)
	</pre>
					

					</div>
				</div>
			</td>
		</tr>
  
  
	</table>
</body>
</html>