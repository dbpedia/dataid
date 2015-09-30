/**
 * Created by Chile on 9/9/2015.
 */

var dataIdGen = angular.module('dataIdGen', ['angularJsonld', 'ui.bootstrap', 'ngSanitize'])
var context = null;
var validationModalDialog = null;

dataIdGen.config(function(jsonldContextProvider){
    /* If we need to change the semantics of 'fullName' we just do it here for the entire application */
    context = getContext();
    jsonldContextProvider.add(context);
});

/** 
 * This method can be used to display the information for this data row
 *
 * @param row	- data for this row
 **/
function formatTable( row ) {
   
    var returnString = "";
	returnString = returnString + '<table>\n';
	returnString = returnString + '\t\t<tr><th>Field Name</th><th>Field Value</th>\n<tr>';
	
	 // create pattern for name space prefixes         	
	 var nsPrefixPattern = null;
	 if (undefined != row.namespacePrefixes &&
	    null != row.namespacePrefixes) {
	         		
	    var patternString = "(";
	    var hasEntry = false;
		for (nsPrefixIndex in row.namespacePrefixes) {
		   	var nsPrefix = row.namespacePrefixes[nsPrefixIndex]
		   	if (hasEntry) {
		   		patternString = patternString + "|(" + 
		   						nsPrefix + ")";
		    } else {
		    	patternString = patternString + "(" + 
		    					nsPrefix + ")";
		    	hasEntry = true;
		    }
		 }		         	
		         	
		 patternString = patternString + ")";
		         	
		 nsPrefixPattern = new RegExp(patternString);
	}
    
    // now go through each property
	for (property in row) {
	  
	    // filter out version field
	    if ("_version_" === property ||
	    	"namespacePrefixes" === property) {
	    	continue;
	    }
	        		
	    /// TODO mullekay : Get port for sparql endpoint!
	    var sparqlUrl = location.protocol + '//' +
	    				'vmdbpedia.informatik.uni-leipzig.de:8890' +
	    				'/sparql?format=application%2Fjson&query=';
	        		
	   	var value = row[property];
	   	if (nsPrefixPattern.test(value)) {
			if (value instanceof Array) {
		   		var tmp = "";
		     			
		 		for (var i in value) {		       				
		   			var sparqlQuery = 'SELECT ?subject ?predicate ?object WHERE ' +
		        				'{ { <' + value[i] + '> ?predicate ?object . } ' +
		        				'UNION { ?subject ?object <' + value[i] + '> . } }';
		        	sparqlQuery = sparqlUrl + escape(sparqlQuery);
		        				
		        	tmp = tmp + '<a target="_blank" href="' + sparqlQuery + '">' + value[i] + '</a>, ';
		        }
		        			
		        // assign tmp as value
		        value = tmp;
		    } else {		        		
		       	var sparqlQuery = 'SELECT ?predicate ?object WHERE ' +
		       						'{ { <' + value + '> ?predicate ?object . } ' +
		       						'UNION { ?predicate ?object <' + value + '> . } }';
		       	sparqlQuery = sparqlUrl + escape(sparqlQuery);
		        			
		       	value = '<a target="_blank" href="' + sparqlQuery + '">' + value + '</a>';
			}
		}
	        		
		returnString = returnString + '\t\t<tr><td>' +
	    							property + '</td><td>' +
	        							value + '</td></tr>';
	}
	            
	returnString = returnString + '\n</table><p><p>';
    return returnString;
}

function genController($scope, $modal, $http, $document) {
    $scope.messages = {};
    $scope.messages.notValidURI = "Not a valid URI!";
    $scope.showValidation = false;
    $scope.newIdSwitch = false;
    $scope.currentTab = 'creator';
    $scope.languages = getLanguages();
    $scope.selectedLanguage = 'en';
    $scope.agentRoles = ["Creator", "Maintainer", "Publisher", "Contact", "Contributor" ];
    $scope.validationResult = {'@graph':[], '@context':{}};
    $scope.valid = 'unvalidated';
    $scope.config = null;
    $scope.cleanId = null;
    $scope.published = false;

    (function(){
        sendRequest("dataIdCreatorConfig.json", "GET", null, false, function(e){
            if(e.target.status >202)
                console.log(e.target.responseText);
            else
            {
                $scope.config = JSON.parse(e.target.responseText);
            }
        }, function(){});
    })();
    
    $scope.sendSolrSearchRequest = function() {    	
    	// construct url
	 	var agentUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '')+'/dataid/publisher/executeTextSearch';
	 	var method = "POST";
	 	
	 	// get value of search query
	 	var searchQuery = document.all.searchQuery.value;
	 	if (null == searchQuery || 0 == searchQuery.trim()) {
	 		// don't do anything, if there is no query
	 		return;
	 	}
	 	
        var searchQueryJson = "\"searchQuery\":\"" + searchQuery.replace(/"/g,"'") + "\""
        
        var facetSelection = document.all.facetQuery;
	 	
	 	// get filter queries
	 	var facetQueriesJson = null;
	 	if (undefined != facetSelection && null != facetSelection) {
	 		// create json string
	 		facetQueriesJson = ",\"facetQueries\":[";
	 		// go through facet selection options
	 		
	 		var facetOptions;
	 		/// TODO mullekay : Please check why this changes!
	 		if (undefined != facetSelection.length) {
	 			facetOptions = facetSelection;
	 		} else {
	 			facetOptions = facetSelection.form.elements;
	 		}
	 		 
	 		var hasEntry = false;
        	$.each(facetOptions, function(i, facetOption) {
        		if (true == facetOption.checked) {
        			
        			if (false == hasEntry) {
        				hasEntry = true;
        			} else {
        				facetQueriesJson = facetQueriesJson + ",";
        			}
        			
        			// found a checked selection
        			facetQueriesJson = facetQueriesJson + "\"" +
        							   facetOption.value.replace(/"/g, "'") + "\"";
        		}
        	});
        	facetQueriesJson = facetQueriesJson + "]";
	 	} else {
	 		facetQueriesJson = "";
	 	}
	    
	    /// TODO mullekay: create trigger on event handler + transfer old search query --> compare and drive filtering by it
	    var postData;
	    if (undefined != $scope.previousQuery &&
	    	$scope.previousQuery.valueOf() === searchQuery.replace(/"/g,"'").valueOf()) {
	    	postData = "{" + searchQueryJson + facetQueriesJson + "}";
	    } else {
	    	// no filter queries are submitted, since query differs
	    	postData = "{" + searchQueryJson + "}";
	    }	    
	    
	    sendRequest(agentUrl, method, postData, true, function (e) {
	    	// get json response
	    	var responseText = e.target.responseText;
	    	var jsonObject = JSON.parse(responseText);
	    	
	    	$scope.previousQuery = jsonObject.searchQuery;
	    	
	    	// get facet queries
	    	var facetQueries = jsonObject.facetQuery;
	    	if (undefined == facetQueries ||
	    		null == facetQueries) {
	    		 $('#searchResult').empty();
	    		 return;
	    	}
	    	
	    	var docs = jsonObject.documentResults;
	    	if (undefined == docs ||
	    		null == docs) {
	    		 $('#searchResult').empty();
	    		 return;
	    	}	    	
	    	
	    	var previousFilterQueriesSet = new Set();
	    	var previousFacetFilterQueries = jsonObject.facetQueries;
	    	if (undefined != previousFacetFilterQueries && null != previousFacetFilterQueries && 0 < previousFacetFilterQueries.length) {
	    		 $.each(previousFacetFilterQueries, function(i, item) {
	    		 	previousFilterQueriesSet.add(item);
	    		  });
	    	}
	    	
	        // get all the facet queries and put them into check box form
	        var index = 0;
	        var checkBoxOptions = "";
	        $.each(facetQueries, function(i, item) {
	         	for (property in item) {
	         		// check whether this is a known query
	         		var knowsQuery = previousFilterQueriesSet.has(property);
	         		var checked = "";
	         		if (true == knowsQuery) {
	         			checked = " checked ";
	         			// delete query
	         			previousFilterQueriesSet.delete(property);
	         		}
	         		
	         		var id = 'checkedId_' + index;
	         		
	         		checkBoxOptions = checkBoxOptions +
	         			'<label for="check' + index + '"> ' +
		         			'<input type="checkbox" name="facetQuery" value="' +
		         			property + '" id="' + id + '"' +
		         			checked + '> ' +
		         			// display it correctly to the user
		         			property.replace(/'/g, "\"") +
		         			' (' + item[property] + ')' +
		         			'</input>'
	         			'</label>\n';
	         		
	         		//id.observe('click', sendSolrSearchRequest());
	         			
	         		index++;
	         	};
	        });
	        
	        // this is run in case a filter query was not used
	        previousFilterQueriesSet.forEach(function(item, sameItem, previousFilterQueriesSet) {
	        
         		var checked = " checked ";
         		var id = 'checkedId_' + index;
         		
         		checkBoxOptions = checkBoxOptions +
         			'<label for="check' + index + '">' +
         			'<input type="checkbox" ' +
         			'name="facetQuery" value="' +
         			item + '" id="' + id + '"' + checked + '> ' +
         			item.replace(/'/g, "\"") + ' (0)</input></label>';
         			
         		//id.observe('click', sendSolrSearchRequest());
         		
         		index++;
	        });
	        
	        
	        
	        $('#searchResult').empty();
	        
		   	        // get the document data
		   	var total = 'Found ' + docs.length + ' results';
	        $('#searchResult').append('<div>' + total + '</div><p><p>');
        
		   	
		   	 // create radio buttons which contain facet queries
	        $('#searchResult').append('<form name="FacetSelectForm" action="#">' +
	        					'<b>Choose which facet queries should be used to filter more specific DataID entries</b>' +
	        					'<fieldset name="SelectedFacetQueries">' +
	        					checkBoxOptions +
	        					'</fieldset></form>');
		   
	        //create result table
	        $('#searchResult').append('<table id="ResultTable" class="display" cellspacing="0" width="100%">' +
	        						'<thead>' +
	        						  	'<tr>' +
                							'<th></th>>' +
                							'<th>ID</th>>' +
                							'<th>Type</th>>' +
            							'</tr>>' +
        							'</thead>>' + 
        							'<tfoot>' +
            						'<tr>' +
                						'<th></th>' +
                						'<th>ID</th>' +
                						'<th>Type</th>' +
            						'</tr>' +
        							'</tfoot>' +
    								'</table>');
    								
    		var dt = $('#ResultTable').DataTable( {
		        "processing": true,
		        "serverSide": false,
  				"data" : docs, // pass in result data
		       "columns" : [
		            {
		                "class":          	"details-control",
		                "orderable":      	false,
		                "documentResults":  null,
		                "defaultContent": ""
		            },
		            { "data": "id" },
		            { "data": "type" }
		        ],
		    } );
		    
		    // Array to track the ids of the details displayed rows
		    var detailRows = [];
		 
		    $('#ResultTable tbody').on( 'click', 'tr td.details-control', function () {
		        var tr = $(this).closest('tr');
		        var row = dt.row( tr );
		        var idx = $.inArray( tr.attr('id'), detailRows );
		 
		        if ( row.child.isShown() ) {
		            tr.removeClass( 'details' );
		            row.child.hide();
		 
		            // Remove from the 'open' array
		            detailRows.splice( idx, 1 );
		        }
		        else {
		            tr.addClass( 'details' );
		            row.child( formatTable( row.data() ) ).show();
		 
		            // Add to the 'open' array
		            if ( idx === -1 ) {
		                detailRows.push( tr.attr('id') );
		            }
		        }
		    } );
		 
		    // On each draw, loop over the `detailRows` array and show any child rows
		    dt.on( 'draw', function () {
		        $.each( detailRows, function ( i, id ) {
		            $('#'+id+' td.details-control').trigger( 'click' );
		        } );
		    } );    
	      }, function () {});
    }

    $scope.countInstancesOfAnyType = function()
    {
        var counts = {};
        for(var i = 0; i< $scope.dataid['@graph'].length; i++) {
            var num = $scope.dataid['@graph'][i]['@type'];
            counts[num] = counts[num] ? counts[num]+1 : 1;
        }
        return counts;
    };

    $scope.getNameById = function(id)
    {
        var obj = getById(id, $scope.graph);
        if(isOfType(obj, 'dataid:Agent'))
            return obj['dataid:agentName']['@value'];
        else
            return obj['dc:title']['@value'];
    }

    $scope.replacePrexixes = function(str)
    {
        var context = getContext();
        for(var key in context)
        {
            if(str.indexOf(context[key]) >= 0)
                str = str.replace(context[key], (key + ':')).replace(/for type [^\s]+/g, '');
        }
        return str;
    };

    $scope.setErrorTableWarningColor = function (level) {
        if (level == 'rlog:WARN')
            return {color: "yellow"}
        if (level == 'rlog:ERROR')
            return {color: "red"}
    }


    $scope.getLabelClass = function(id)
    {
        var obj = getById(id, $scope.graph);
        var zw = {
            "dataid:Dataset":"label-info",
            "void:DatasetDescription":"label-success",
            "dataid:Distribution":"label-default",
            "dataid:Agent":"label-primary"
        };
        var zz = null;
        if(obj.constructor === Array)
            zz = "label label-xlg " + zw[obj['@type'][obj['@type'].length-1]] ;
        else
            zz = "label label-xlg " + zw[obj['@type']] ;
        return zz;
    };

    $scope.changeSubset = function(sett)
    {
        console.log(JSON.stringify(sett));
        if(isOfType(sett, 'void:DatasetDescription'))
            ($("#dataiduri")).focus();
        if(isOfType(sett, 'dataid:Agent'))
            $scope.openAgent(null, sett);
        if(isOfType(sett, 'dataid:Dataset'))
            $scope.openDataset(null, sett);
        if(isOfType(sett, 'dataid:Distribution') || isOfType(sett, 'dataid:SparqlEndpoint'))
            $scope.openDist(null, sett);
    }

    $scope.getNewId = function(parent, type)
    {
        var counts = $scope.countInstancesOfAnyType();
        var tt = type.replace('dataid:', '').replace('http://dataid.dbpedia.org/ns/core#', '');
        tt = type.replace('void:', '').replace('http://rdfs.org/ns/void#', '');
        var zw = 0;
        if(type in counts)
            zw = counts[type];
        var parentName = refactorNewId( parent['@id'], tt + '-' + zw);
        return parentName;
    };

    $scope.delSetAndChildren = function(sett)
    {
        var parent = $scope.getParent(sett);
        if(!(parent === undefined || parent == null))
            return;
        var children = getChildren(sett);
        if(isOfType('dataid:Agent'))
            $scope.delAgent(sett);
        if(isOfType('dataid:Dataset'))
            $scope.delDataset2(sett);
        if(isOfType('dataid:Distribution') || isOfType('dataid:SparqlEndpoint'))
            $scope.delDistribution(sett);
        //TODO Linkset
        for(var i =0; i < children.length; i++) {
            $scope.delSetAndChildren(children[i]);
        }
    };
    $scope.addDataset2 = function(parent) {
        var dataset = null;
        if(jsonLdTypeComparator(parent['@type'], "dataid:Dataset"))
        {
            var newId = $scope.getNewId(parent, "void:Subset");
            dataset = getEmptyDataset(newId, parent['@id'])
            parent['void:subset']['@value'].push(dataset['@id']);
        }
        if(jsonLdTypeComparator(parent['@type'], "void:DatasetDescription"))
        {
            var newId = $scope.getNewId(parent, "dataid:Dataset");
            dataset = getEmptyDataset(newId, parent['@id'])
            parent['foaf:topic']['@value'].push(dataset['@id']);
        }
        $scope.graph.push(dataset);
        return dataset;
    };

    $scope.delDataset2 = function(dataset) {
        var parent = $scope.getParent(dataset);
        var arr = null;
        if(isOfType(parent, "dataid:Dataset"))
            arr = parent['void:subset'];
        if(isOfType(parent, "void:DatasetDescription"))
            arr = parent['foaf:topic'];

        removeFromArray(dataset, arr);
        removeFromArray(dataset, $scope.graph);
    };

    $scope.delDistribution = function(dataset) {
        removeFromArray(dataset, $scope.graph);
        var parent = $scope.getParent(dataset);
        removeFromArray(dataset['@id'], parent['dcat:distribution']['@value']);
        removeFromArray(dataset['@id'], parent['void:sparqlEndpoint']['@value']);
    };

    $scope.addAgent = function(type) {
        var newId = $scope.getNewId($scope.root, type);
        var agent = getEmptyAgent(newId, [type])
        $scope.graph.push(agent);
        return  agent;
    };

    $scope.delAgent = function(dataset) {
        if(getAllOfTypes('dataid:Agent', $scope.graph).length == 1) //we need at least one Agent
        {
            alert('you need at least one Agent');
            return;
        }
        removeFromArray(dataset, $scope.graph);
        for(var i =0; i < $scope.graph.length; i++)
        {
            var arr = $scope.graph[i]['dataid:associatedAgent']['@value'];
            if(!!arr)
                removeFromArray(dataset['@id'], arr);
        }
    };

    $scope.activatePopover = function() {
        $('.popr').popr({
            'speed': 200,
            'mode': 'bottom'
        });
    }

    $scope.getParent = function(member)
    {
        return getById(member['@parent'], $scope.dataid['@graph']);
    };

    $scope.checkParent = function(child, parent)
    {
        return child['@parent'] == parent['@id'];
    };

    $scope.agentFilter =function(agent) {
        return isOfType(agent, 'dataid:Agent');
    };

    $scope.errorFilter =function(error) {
        return isOfType(error, 'rut:TestCaseResult');
    };

    $scope.datasetFilter =function(dataset) {
        return isOfType(dataset, 'dataid:Dataset');
    };

    $scope.dataidFilter =function(dataset) {
        return isOfType(dataset, 'void:DatasetDescription');
    };

    $scope.isOfTypeS =function(obj, type) {
        return isOfType(obj, type);
    };

    $scope.getAllAgents = function()
    {
        var zw = getAllOfTypes(['dataid:Agent'], $scope.graph);
        return zw;
    };

    $scope.getAllDistributions = function()
    {
        return getAllOfTypes(['dataid:Distribution', 'dataid:SparqlEndpoint'], $scope.graph);
    };

    $scope.openValidating = function() {

        validationModalDialog = $modal.open({
            templateUrl: 'validating.html',
            controller: ModalValidatingCtrl,
            size: 'md',
            backdrop : 'static',
            resolve: {            }
        });
        validationModalDialog.result.then(function(resp) {
            if(resp == 'cancel')
                alert('canceled'); //TODO abort validation
        });
    };

    $scope.openNewAgent = function() {
        $scope.openAgent(1, null);
    };
    $scope.openAgent = function(parent, agent) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalAgentContent.html',
            controller: ModalAgentInstanceCtrl,
            size: 'lg',
            backdrop : 'static',
            resolve: {
                agent: function() {
                    if(agent == null)
                    {
                        var newId = $scope.getNewId($scope.root, 'dataid:Agent');
                        agent = getEmptyAgent(newId, []);
                    }

                    return {agent: agent, messages: $scope.messages};
                }
            }
        });
        modalDistInstance.result.then(function(newAge) {
            if(!(parent === undefined || parent == null))
            {
                $scope.pushNewSet(null, newAge);
            }

        });
    };

    $scope.openNewDataset = function(parent) {
        $scope.openDataset(parent, null);
    };
    $scope.openDataset = function(parent, dataset) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalDatasetContent.html',
            controller: ModalDatasetInstanceCtrl,
            size: 'lg',
            backdrop : 'static',
            resolve: {
                dataset: function() {
                    if(dataset == null)
                    {
                        var newId = $scope.getNewId(parent, 'dataid:Dataset');
                        dataset = getEmptyDataset(newId, parent['@id']);
                    }

                    return {dataset: dataset, messages: $scope.messages, licenses: getLicenses(), agents: $scope.getAllAgents(), distributions: $scope.getAllDistributions(), openDist: $scope.openNewDist, openAgent: $scope.openNewAgent};
                }
            }
        });
        modalDistInstance.result.then(function(retSet) {
            if(!(parent === undefined || parent === null))
                $scope.pushNewSet(parent, retSet);
        });
    };

    $scope.pushNewSet = function(parent, newSet)
    {
        newSet = finalizeSet(parent, newSet);
        if(!!parent && isOfType(parent, 'void:DatasetDescription') && isOfType(newSet, 'dataid:Dataset'))
        {
            parent['foaf:topic']['@value'].push(newSet['@id']);
        }
        else if(isOfType(newSet, 'dataid:Distribution'))
        {
            parent['dcat:distribution']['@value'].push(newSet['@id']);
        }
        else if(isOfType(newSet, 'dataid:SparqlEndpoint'))
        {
            parent["void:sparqlEndpoint"]['@value'].push(newSet['@id']);
        }
        $scope.graph.push(newSet);
    };

    $scope.openNewDist = function(parent) {
        $scope.openDist(parent, null);
    };
    $scope.openDist = function(parent, distribution) {

        var modalDistInstance = $modal.open({
            templateUrl: 'modalDistContent.html',
            controller: ModalDistInstanceCtrl,
            size: 'lg',
            backdrop : 'static',
            resolve: {
                dist: function() {
                    if(distribution == null) {
                        var newId = $scope.getNewId(parent, 'dataid:Distribution');
                        distribution = getEmptyDistribution(newId, parent['@id'], 'dataid:Distribution');
                    }
                    return {distribution: distribution, license: getLicenses(), scope: $scope};
                }
            }
        });
        modalDistInstance.result.then(function(dist) {
            if(!(parent === undefined || parent === null))
            {
                $scope.pushNewSet(parent, dist);
            }
        });
    };

    $scope.openUpload = function() {

        var modalDistInstance = $modal.open({
            templateUrl: 'modalUploadContent.html',
            controller: ModalUploadCtrl,
            size: 'lg',
            backdrop : 'static',
            resolve: {            }
        });
        modalDistInstance.result.then(function(content) {
            $scope.updateLocalId(integrateDataId(JSON.parse(content)));
        });
    };

    $scope.updateLocalId = function(dataid)
    {
        $scope.dataid = dataid;
        $scope.graph = dataid["@graph"];
        $scope.root = $scope.graph[0];
        $scope.agent = getAllOfTypes(['dataid:Agent'], $scope.graph)[0];
        $scope.root['dataid:associatedAgent']['@value'].push($scope.agent['@id']);
        $scope.validate($scope.dataid);
    }

    $scope.getObjFromId = function(id)
    {
        for(var i=0; i < $scope.graph.length; i++)
        {
            if($scope.graph[i]['@id'] == id)
            {
                return $scope.graph[i];
            }
        }
        return {};
    };

    $scope.validate = function(dataid)
    {
        $scope.cleanId = cleanDataId(dataid);
        $scope.openValidating();
        console.log($scope.cleanId);
        var request = sendRequest($scope.config['validatorEndpoint'], "POST", JSON.stringify($scope.cleanId), true, function(e){
            if(e.target.status >202)
                console.log(e.target.responseText);
            else
            {

                $scope.validationResult = JSON.parse(e.target.responseText);
                console.log($scope.validationResult);
                var errors = getAllOfTypes(['rut:TestCaseResult'], $scope.validationResult['@graph']);
                var counts = {};
                for(var i = 0; i< errors.length; i++) {
                    var num = errors[i]["level"];
                    counts[num] = counts[num] ? counts[num]+1 : 1;
                }
                if('rlog:ERROR' in counts)
                    $scope.valid = 'false';
                else if('rlog:WARN' in counts)
                    $scope.valid = 'warnings';
                else
                    $scope.valid = 'true';
                $scope.$apply();
                validationModalDialog.close();
            }
        }, function (e) { });
    };

    $scope.publishDataId = function()
    {
        if($scope.valid == 'true' && $scope.cleanId !== undefined && $scope.cleanId != null)
        {
            sendRequest($scope.config['insertIdEndpoint'], "POST", $scope.cleanId, true, function(e){
                if(e.target.status >202)
                    console.log(e.target.responseText);
                else
                {
                    $scope.published = true;
                    alert(e.target.responseText);
                }
            }, function(e){
                alert(e.target.responseText);
            });
        }
        else
            alert("DataID is not valid! Please validate the DataID first.");
    };

    $scope.getValidationColor = function()
    {
        var zw = {
            "true":"panel panel-success",
            "unvalidated":"panel panel-default",
            "warnings":"panel panel-warning",
            "false":"panel panel-danger"
        };
        return zw[$scope.valid];
    };

    $scope.getValidationHeading = function()
    {
        var zw = {
            "true":"DataID is valid",
            "unvalidated":"DataID yet to be validated",
            "warnings":"DataID is valid but recommended values are missing",
            "false":"DataID is not valid"
        };
        return zw[$scope.valid];
    };

    $scope.isFormValid = function()
    {
        if(($('#dataiduri')).val().trim() == "http://example.org/dataids#dataid1")
        {
            alert("Please change the uri of the DataID.\nDon't use the example URL.");
            return false;
        }
        if($scope.root['foaf:topic']['@value'].length == 0)
        {
            alert("Please add at least one dataset.\nSelect the Menu option next to your DataID on the left hand side.");
            return false;
        }
        return this.form.$valid;
    };

    $scope.$watchCollection('graph', function(newVal){
            if(isOfType(newVal[newVal.length-1], 'dataid:Agent'))
                $scope.$root.$broadcast('newAgent',{"val":newVal[newVal.length-1]});
            else if(isOfType(newVal[newVal.length-1], 'dataid:Distribution'))
                $scope.$root.$broadcast('newDistribution',{"val":newVal[newVal.length-1]});
            else if(isOfType(newVal[newVal.length-1], 'dataid:Dataset'))
            {
                $scope.$root.$broadcast('newDataset',{"val":newVal[newVal.length-1]});
            }
    });


    $scope.$watch("root['@id']", function(value, old){
        //TODO validate URL please :)
        if(value === undefined || value.length < 7 || value.substr(0, 7) != "http://")
        {
            value = "http://";
            $scope.root['@id'] =  "http://";
        }
        if(old === undefined || old.length < 7 || old.substr(0, 7) != "http://")
            old = "http://";
        console.log('watch: newVal: ' + value + ' - oldVal: ' + old);
        $scope.$broadcast('uriupdate',{instance:$scope.root, newVal:value, oldVal:old});
    });

    $scope.$on('uriupdate', function(event, newId){
        console.log('newVal: ' + $scope.root['@id']);

        var children = getChildren(newId.instance);
        for(var i=0; i < children.length; i++)
        {
            var c = getById(children[i], $scope.graph);
            console.log('onreceife: got child: ' + c['@id']);
            if(c !== undefined && !!c && c['@parent'] == newId.oldVal)
            {
                console.log('onreceife: parent: ' + c['@parent']);
                c['@parent'] = newId.newVal;
            }
        }
    });


    $scope.dataid = getEmptyDataId('http://dbpedia.dataid.org/myExampleDataId#1');
    $scope.graph = $scope.dataid["@graph"];
    $scope.root = $scope.graph[0];
    $scope.agent = $scope.addAgent('dataid:Creator');
    $scope.root['dataid:associatedAgent']['@value'].push($scope.agent['@id']);
    $scope.root = finalizeSet(null, $scope.root);
    //$scope.dataset = $scope.addDataset2($scope.root);

    //AGentBox declaration
    $scope.agentDontShowType=true;
    $scope.agentBoxTitle = 'Enter your contact information';

    //DatsetBox declaration
    $scope.datasetBoxTitle = 'Define a root-dataset';
    $scope.activatePopover();
}

var ModalDatasetInstanceCtrl = function($scope, $modalInstance, dataset) {
    $scope.dataset = dataset.dataset;
    $scope.messages = dataset.messages;
    $scope.agents = dataset.agents;
    $scope.licenses = dataset.licenses;
    $scope.languages = getLanguages();
    $scope.distributions = dataset.distributions;
    $scope.openDistr = dataset.openDist;
    $scope.openAgent = dataset.openAgent;
    $scope.ok = function() {
        $modalInstance.close($scope.dataset);
    };

    $scope.$on('newAgent', function(event, args){
        $scope.agents.push(args.val);
    });
    $scope.$on('newDistribution', function(event, args){
        $scope.distributions.push(args.val);
/*        $("#ditr").removeClass('ng-invalid')
        $("#ditr").removeClass('ng-pristine');
        $("#ditr").addClass('ng-valid');
        $("#ditr").addClass('ng-dirty');*/
        //alert(JSON.stringify(angular.element(this.formDataset['ditr']).$valid));
    });
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.$watch("dataset['dc:title']['@value']", function(value) {
        $scope.dataset['@id'] = refactorNewId($scope.dataset['@id'], value);
    });
    $scope.$watch("dataset['dc:license']['@value']", function(value) {
        $scope.dataset['dataid:licenseName']['@value'] = $.grep(getLicenses(), function(e){ return e.val == value; }).name;
    });
    $scope.$watch("selectedLanguage", function(value) {
        initializeLanguage($scope.dataset, value);
    });
    $scope.$watch("dataset['@id']", function(value, old){
        $scope.$root.$broadcast('uriupdate',{instance:$scope.dataset, newVal:value, oldVal:old});
    });
    $scope.selectedLanguage = 'en';
};

var ModalAgentInstanceCtrl = function($scope, $modalInstance, agent) {
    $scope.agent = agent.agent;
    $scope.messages = agent.messages;
    $scope.languages = getLanguages();
    $scope.ok = function() {
        $modalInstance.close($scope.agent);
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.$watch("agent['dataid:agentName']['@value']", function(value) {
        $scope.agent['@id'] = refactorNewId($scope.agent['@id'], value);
    });
    $scope.$watch("selectedLanguage", function(value) {
        initializeLanguage($scope.dataset, value);
    });
    $scope.someTypeSelected = function () {
        return $scope.agent['@type'].filter(function( obj ) { return !!obj && obj.trim() != 'dataid:Agent'; }).length == 0;
    };

    $scope.$watch("agent['@id']", function(value, old){
        $scope.$root.$broadcast('uriupdate',{instance:$scope.agent, newVal:value, oldVal:old});
    });
    $scope.selectedLanguage = 'en';
};

var ModalDistInstanceCtrl = function($scope, $modalInstance, dist) {
    $scope.distribution = dist.distribution;
    $scope.license = dist.license;
    $scope.agents = dist.scope.getAllAgents();
    $scope.languages = getLanguages();
    $scope.foreignScope = dist.scope;
    $scope.openAgent = dist.scope.openNewAgent;

    $scope.$on('newAgent', function(event, args){
        $scope.agents.push(args.val);
    });

    $scope.$watch("distribution['dc:title']['@value']", function(value) {
        $scope.distribution['@id'] = refactorNewId($scope.distribution['@id'], value);
    });

    $scope.ok = function() {
        $modalInstance.close($scope.distribution);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.$watch("selectedLanguage", function(value) {
        initializeLanguage($scope.dataset, value);
    });
    $scope.$watch("distribution['@id']", function(value, old){
        $scope.$root.$broadcast('uriupdate',{instance:$scope.distribution, newVal:value, oldVal:old});
    });
    $scope.selectedLanguage = 'en';
};

var ModalValidatingCtrl = function($scope, $modalInstance) {
    $scope.ok = function(ret) {
        $modalInstance.close(ret);
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};

var ModalUploadCtrl = function($scope, $modalInstance) {
    $scope.dataidtext = "paste a DataID here...";
    $scope.filepath = "";
    $scope.ok = function() {
        $modalInstance.close($scope.dataidtext);
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};

function initializeLanguage(object, lang)
{
    for(var key in object)
    {
        if(object[key]['@language'] != undefined)
        {
            if(!(lang in object[key]['@value'])) //not!
                object[key]['@value'][lang] = "";
        }
    }
}

function finalizeSet(parent, newSet)
{
    var name = null;
    if(isOfType(newSet, 'dataid:Agent'))
    {
        name = newSet['dataid:agentName']['@value'];
    }
    else
    {
        name = newSet['dc:title']['@value'];
        if(!newSet['dc:issued']['@value'])
            newSet['dc:issued']['@value'] = new Date().toISOString();
        else
            newSet['dc:modified']['@value'] = new Date().toISOString();
    }
    if(!!parent)
        newSet['@id'] = refactorNewId(parent['@id'], name);

    return newSet;
}

function refactorNewId(parentId, newName)
{
    var zw = parentId.substring(0, Math.max(parentId.lastIndexOf('#')+1, parentId.lastIndexOf('/')+1));
    return zw + encodeURI(newName);
}

function removeFromArray(obj, arr)
{
    var ind = arr.indexOf(obj);
    if(ind >= 0)
        arr.splice(ind, 1);
}

function getId(elem){

    return elem.name;
}

function getLdToAngularMap(jsonLdObj)
{
    var map = {};
    for(key in jsonLdObj)
    {
        map[key.replace(':', '').replace('@', '')] = key;
    }
    return map;
}

function jsonLdTypeComparator(comp1, comp2)
{
    if(!comp1 || comp1 == null || !comp2 || comp2 == null)
        return false;
    var c1split = comp1.split([':']);
    var c1long = comp1.substr(0, 4) == 'http' ? comp1 : context[c1split[0]] + c1split[1];
    var c2split = comp2.split([':']);
    var c2long = comp2.substr(0, 4) == 'http' ? comp2 : context[c2split[0]] + c2split[1];

    if(!c1long || !c2long)
        return false;
    return c1long.trim().toLowerCase() == c2long.trim().toLowerCase();
}

function cleanDataId(dataid)
{
    var id = getEmptyDataId("hdsjgfjh");
    id['@graph'] = [];

    for(var i=0; i < dataid['@graph'].length; i++)
    {
        id['@graph'].push(cleanDataIdPart(dataid['@graph'][i]));
    }
    return id;
}

function cleanDataIdPart(obj) {
    if(obj === null || typeof(obj) !== 'object' || 'isActiveClone' in obj)
        return obj;

    var temp = obj.constructor();

    if('@id' in obj)
    {
        if(obj['@id'] == null || obj['@id'] === undefined)
            return null;
    }
    if('@value' in obj)
    {
        if(obj['@value'] == null || obj['@value'] === undefined)
            return null;
        if(obj['@value'].constructor === Array)
        {
            if(!!obj['@type'] && obj['@type'].length > 0 && obj['@type'][0].trim().substr(0, 4) != "xsd:")
            {
                var tempArr = [];
                for(var j=0; j < obj['@value'].length; j++)
                {
                    tempArr.push({ "@id" : obj['@value'][j]});
                }
                return tempArr;
            }
            else
            {
                var ta = [];
                for(var i=0; i < obj['@value'].length; i++)
                {
                    ta.push(cleanDataIdPart(obj['@value'][i]));
                }
                return ta;
            }
        }
    }
    if('@language' in obj && !!obj['@value'])
    {
        var tenpArray = [];
        for(k in obj['@value'])
        {
            tenpArray.push({"@language":k, "@value":obj['@value'][k]});
        }
        return tenpArray;
    }

    if('@type' in obj)
    {
        if(!!obj['@type'] && obj['@type'].length == 1)
            obj['@type'] = obj['@type'][0];
        else
            obj['@type'] = obj['@type'];
    }
    for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
            obj['isActiveClone'] = null;
            if (key != '@parent' && key != '@required' && key != "$$hashKey" && key != "@pristine")
            {
                var zw = cleanDataIdPart(obj[key]);
                if(!!zw)
                    temp[key] = zw;
            }
            delete obj['isActiveClone'];
        }
    }
    return temp;
}

function integrateDataId(dataid)
{
    var root = getAllOfTypes(['void:DatasetDescription'], dataid['@graph']);
    if(root.length != 1)
    {
        if(root.length < 1)
            alert("This is no DataID! (void:DatasetDescription is missing)");
        else
            alert("This file has multiple DataID declarations! Load only one DataID at a time.")
        return null;
    }
    root = root[0];
    var retDataId = getEmptyDataId(root['@id']);
    retDataId['@graph'][0] = integrateSet(root, retDataId['@graph'][0]);
    var parentMap = {};

    for(var i =0; i < dataid['@graph'].length; i++)
    {
        var zw = getOptionalArray(dataid['@graph'][i], 'void:subset');
        for(var j =0; j < zw.length; j++)
        {
            parentMap[zw[j]] = dataid['@graph'][i]['@id'];
        }
        zw = getOptionalArray(dataid['@graph'][i], 'dcat:distribution');
        for(var j =0; j < zw.length; j++)
        {
            parentMap[zw[j]] = dataid['@graph'][i]['@id'];
        }
        zw = getOptionalArray(dataid['@graph'][i], 'foaf:topic');
        for(var j =0; j < zw.length; j++)
        {
            parentMap[zw[j]] = dataid['@graph'][i]['@id'];
        }
    }
    for(var i=0; i < dataid['@graph'].length; i++)
    {
        if(isOfType(dataid['@graph'][i], 'dataid:Agent'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyAgent(dataid['@graph'][i]['@id'], dataid['@graph'][i]['@type'])));
        else if(isOfType(dataid['@graph'][i], 'dataid:Dataset'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyDataset(dataid['@graph'][i]['@id'], parentMap[dataid['@graph'][i]['@id']])));
        else if(isOfType(dataid['@graph'][i], 'dataid:Distribution'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyDistribution(dataid['@graph'][i]['@id'], parentMap[dataid['@graph'][i]['@id']], dataid['@graph'][i]['@type'])));
        else if(isOfType(dataid['@graph'][i], 'dataid:Linkset'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyLinkset(dataid['@graph'][i]['@id'])));
    }
    return retDataId;
}

function getChildren(sett)
{
    var children = [];
    if(sett['dataid:associatedAgent'] != null)
        children = children.concat(sett['dataid:associatedAgent']['@value']);
    if(sett['dcat:distribution'] != null)
        children = children.concat(sett['dcat:distribution']['@value']);
    if(sett['void:subset'] != null)
        children = children.concat(sett['void:subset']['@value']);
    if(sett['foaf:topic'] != null)
        children = children.concat(sett['foaf:topic']['@value']);
    return children;
}

function getOptionalArray(obj, property)
{
    if(!!obj[property])
    {
        if(obj[property].constructor === Array)
            return obj[property];
        else
            return [obj[property]];
    }
    return [];
}

function integrateSet(orig, template)
{
    for(var key in template) {
        if(typeof(template[key]) == 'object') {
            if ('@language' in template[key] && !!orig[key]) {
                var langs = {};
                if(orig[key].constructor === Array)
                {
                    for (var i =0; i< orig[key].length; i++) {
                        langs[orig[key][i]['@language']] = orig[key][i]['@value'];
                    }
                }
                else
                {
                    langs[orig[key]['@language']] = orig[key]['@value'];
                }
                template[key]['@value'] = langs;
            }
            else if (!!template[key]['@value'] && template[key]['@value'].constructor === Array) {
                if (!!orig[key] && orig[key].constructor === Array)
                {
                    if(!!orig[key]['@type'] && orig[key]['@type'][0].trim().substr(0,4) != "xsd:")
                        template[key]['@value'] = idsToStrings(orig[key]);
                    else
                        template[key]['@value'] = orig[key];
                }

                else
                    template[key]['@value'].push(orig[key]);
            }
            else {
                template[key]['@value'] = orig[key]['@value'];
                if(!!orig[key]['@type'] && orig[key]['@type'].constructor !== Array)
                    template[key]['@type'] = [orig[key]['@type']];
                else
                    template[key]['@type'] = orig[key]['@type'];
                template[key]['@language'] = orig[key]['@language'];
            }
        }
        else
        {
            if(!!orig[key])
                template[key] = orig[key];
        }
    }
    return template;
}

function idsToStrings(ids)
{
    if(ids.constructor !== Array)
        return [ids['@id']];
    else
    {
        var tempArr = [];
        for(var i=0; i < ids.length; i++)
        {
            tempArr.push(ids[i]['@id']);
        }
        return tempArr;
    }
}

function isOfType(obj, type)
{
    var typeArr = obj['@type'];
    if(typeArr == undefined)
        return false;
    if(typeArr.constructor === Array)
    {
        for(var i = 0; i< typeArr.length; i++)
        {
            if(jsonLdTypeComparator(typeArr[i], type))
            {
                return true;
            }
        }
    }
    else
    {
        if(jsonLdTypeComparator(typeArr, type))
            return true;
    }
    return false;
}

function sortSelection(selElem) {
    var tmpAry = new Array();
    for (var i=0;i<selElem.options.length;i++) {
        tmpAry[i] = new Array();
        tmpAry[i][0] = selElem.options[i].text;
        tmpAry[i][1] = selElem.options[i].value;
    }
    tmpAry.sort();
    while (selElem.options.length > 0) {
        selElem.options[0] = null;
    }
    for (var i=0;i<tmpAry.length;i++) {
        var op = new Option(tmpAry[i][0], tmpAry[i][1]);
        selElem.options[i] = op;
    }
    return;
}

function sendRequest(url, method, data, async, onLoad, onError)
{
    var request = new XMLHttpRequest();
    request.onload = onLoad;
    request.onerror = onError;
    request.open(method, url, async);
    request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    request.send(data);
    return request;
}

getAllOfTypes = function(types, graph)
{
    var ret = [];
    for(var i=0; i < graph.length; i++)
    {
        for(var j=0; j < types.length; j++)
        {
            if(!!graph[i] && !!types[j] && isOfType(graph[i], types[j]))
                ret.push(graph[i]);
        }
    }
    return ret;
};

getById = function(id, graph)
{
    return graph.filter(function( obj ) { return obj['@id'] == id; })[0];
};