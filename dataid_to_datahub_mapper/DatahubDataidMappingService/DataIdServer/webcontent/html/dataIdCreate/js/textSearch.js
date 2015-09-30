/**
 * Created by mullekay
 * 
 * The methods in this file
 * can be used to perform
 * text search methods.
 *
 */
 
 function getSearchInformation() {
 	// test printout
 	$scope.Message = "Button clicked."
 	
 	// construct url
 	var agentUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '')+'/dataid/publisher/validateid';
 	var method = "POST";
    var postData = searchQuery + filterQueries;
    sendRequest(agentUrl, method, postData, true, function () {
            var response = request.responseText;
        }, function () {
        });
 }