/**
 * Created by Chile on 9/9/2015.
 */
var dataIdGen = angular.module('dataIdGen', ['angularJsonld', 'ui.bootstrap', 'ngMessages']);
var context = null;
var validationModalDialog = null;

dataIdGen.config(['$tooltipProvider', function($tooltipProvider){
    $tooltipProvider.setTriggers({
        'test': 'test'
    });
}]);
dataIdGen.config(function(jsonldContextProvider){
    /* If we need to change the semantics of 'fullName' we just do it here for the entire application */
    context = getContext();
    jsonldContextProvider.add(context);

    (function() {
        if (!String.prototype.format) {
            String.prototype.format = function() {
                var args = arguments;
                return this.replace(/{(\d+)}/g, function(match, number) {
                    return typeof args[number] != 'undefined' ? args[number] : match;
                });
            };
        }
    })();
});


dataIdGen.controller('genController',
    function genController($scope, $modal, $window, $http, $document) {
        $scope.messages = {};
        $scope.messages.notValidURI = "Not a valid URI!";
        $scope.showValidation = false;
        $scope.newIdSwitch = false;
        $scope.currentTab = 'creator';
        $scope.selectedLanguage = 'en';
        $scope.agentRoles = ["Creator", "Maintainer", "Publisher", "Contact", "Contributor" ];
        $scope.validationResult = {'@graph':[], '@context':{}};
        $scope.valid = 'unvalidated';
        $scope.config = null;
        $scope.cleanId = null;
        $scope.published = false;
        $scope.window = $window;
        $scope.currentBranch = 'master';
        $scope.currentDelta = 0;
        $scope.branch={};
        $scope.branchGraph = null;
        $scope.popovers = getPopoverContext();
        $scope.popMap = {};

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

        $scope.licenses = getJsonResource($scope.config['licenseQuery']);
        $scope.languages = getJsonResource($scope.config['languageQuery']);
        $scope.mimetypes = getJsonResource($scope.config['mimeQuery']);

        $('#versionGraph').on('click', function(e) {

        });

        //close any popover on outside click
        $('html').on('click', function(e) {
            if (!e.target.classList.contains('popoverButton') && !e.target.classList.contains('popoverNoClose') && !e.target.classList.contains('help-button')&& !e.target.classList.contains('glyphicon')) {
                var pops = $('.popover');
                for(var i =0; i < pops.length; i++) {
                    if ($( "div[name=popoverDontClose]", pops[i]).length == 0)
                        pops[i].parentNode.removeChild(pops[i]);
                }
            }
        });

        $scope.toggleOptionPanel = function()
        {
            var panel = $('#accordion');
            var page = $('#page-wrapper');
            var logo = $('#dataidlogo');
            var nav = $('#innernav');
            var leftNav = $('#leftNavBottom');
            var leftNavBot = $('#leftNavBottomCompressed');
            var optBtn = $('#options-btn');
            var optBtnComp = $('#options-btn-compressed');
            if (panel.hasClass('navbar-static-side')) {
                panel.removeClass('navbar-static-side');
                panel.addClass('navbar-hidden-side');
                panel.fadeOut('fast', function () {
                    panel.stop().animate({
                        width: '0px',
                        opacity: 0.0
                    }, 'fast');
                });
                leftNav.fadeOut('fast', function () {
                    leftNav.stop().animate({
                        width: '0px',
                        opacity: 0.0
                    }, 'fast');
                });
                leftNavBot.stop().animate({
                    width: '50px',
                    opacity: 1
                }, 'fast', function () {
                    leftNavBot.fadeIn('fast');
                });
                optBtn.fadeOut('fast', function () {
                    optBtn.stop().animate({
                        width: '0px',
                        opacity: 0.0
                    }, 'fast');
                });
                optBtnComp.stop().animate({
                    width: '50px',
                    opacity: 1
                }, 'fast', function () {
                    optBtnComp.fadeIn('fast');
                });
                page.animate({marginLeft: '-=300px'}, 'fast');
                logo.animate({left: '-=300px'}, 'fast');
                nav.animate({marginLeft: '-=350px'}, 'fast');
            } else {
                panel.removeClass('navbar-hidden-side');
                panel.addClass('navbar-static-side');
                panel.stop().animate({
                    width: '350px',
                    opacity: 1
                }, 'fast', function () {
                    panel.fadeIn('fast');
                });
                leftNav.stop().animate({
                    width: '350px',
                    opacity: 1
                }, 'fast', function () {
                    leftNav.fadeIn('fast');
                });
                leftNavBot.fadeOut('fast', function () {
                    leftNavBot.stop().animate({
                        width: '0px',
                        opacity: 0.0
                    }, 'fast');
                });

                optBtn.stop().animate({
                    width: '350px',
                    opacity: 1
                }, 'fast', function () {
                    optBtn.fadeIn('fast');
                });
                optBtnComp.fadeOut('fast', function () {
                    optBtnComp.stop().animate({
                        width: '0px',
                        opacity: 0.0
                    }, 'fast');
                });
                page.animate({marginLeft: '+=300px'}, 'fast');
                logo.animate({left: '+=300px'}, 'fast');
                nav.animate({marginLeft: '+=350px'}, 'fast');
            }
        };

        $scope.updateBranchGraph = function()
        {
            $scope.branchGraph = new GitGraph({
                mode: "compact",
                elementId: "versionGraph",
                template: {
                    branch: {
                        spacingX: 40,
                        showLabel: true
                    },
                    commit: {
                        showLabel: true,
                        spacingY: 20,
                        dot: {
                            size: 8
                        },
                        message: {
                            displayAuthor: true,
                            displayBranch: false,
                            displayHash: false,
                            font: "normal 12pt Arial"
                        }
                    }
                }
            });
            var master = $scope.branchGraph.branch("master");
            master.commit({
                dotColor: "red",
                message: "Pimp dat commit",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            master.commit({
                dotColor: "red",
                message: "dfhjfjf ",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            master.commit({
                dotColor: "red",
                message: "ab gehts",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            var test1 = master.branch("master");
            test1.commit({
                dotColor: "yellow",
                message: "ab gehts",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            test1.commit({
                dotColor: "yellow",
                message: "miep",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });

            master.commit({
                dotColor: "red",
                message: "ttttt",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            master.commit({
                dotColor: "red",
                message: "hffdgre",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            master.commit({
                dotColor: "red",
                message: "tttergehtt",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            master.commit({
                dotColor: "red",
                message: "rthtrh",
                author: "Jacky <prince@dutunning.com>",
                isMouseover: true,
                showLabel: true
            });
            var develop = $scope.branchGraph.branch("develop");

            $scope.branchGraph.canvas.addEventListener( "commit:mouseenter", function ( event ) {
                //console.log( "You're over a commit.", "Here is a bunch of data ->", event.data );
                var tooltip = $('#versionGraph').popover({
                    title:event.data.commit.message
                });
                /*                graph.popover({
                 placement:'right',
                 title:'some title',
                 content: 'popover-dataid-menu.html'
                 });*/
            } );
        };


        $scope.preparePopover = function(popover, input)
        {
            if(typeof input == 'string')
                input = $scope.formAgent[input];
            $scope.popMap[input.$name] = preparePopover(popover, input);
            return $scope.popMap[input.$name] == null ? null : $scope.popMap[input.$name].template;
        };

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
        };

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
        };

        //remote call a function with a string parameter
        $scope.callFunction = function (name, stringParameter){
            angular.isFunction($scope[name])
            $scope[name](stringParameter)
        };

        $scope.preparePopover = function(popover, input)
        {
            $scope.popMap[input.$name] = preparePopover(popover, input);
            return $scope.popMap[input.$name] == null ? null : $scope.popMap[input.$name].template;
        };

        $scope.showAsInvalid = function(input)
        {
            return false; //showAsInvalid(input);
        };

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
            if(typeof sett == 'string')
                sett = $scope.getObjFromId(sett);
            console.log(JSON.stringify(sett));
            if(isOfType(sett, 'void:DatasetDescription'))
                ($("#dataiduri")).focus();
            if(isOfType(sett, 'dataid:Agent'))
                $scope.openAgent(sett);
            if(isOfType(sett, 'dataid:Dataset'))
                $scope.openDataset(null, sett);
            if(isOfType(sett, 'dataid:Distribution'))
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
            var parentName = parent['@id'] + '#' +  tt + '-' + zw;
            return parentName;
        };

        $scope.delSetAndChildren = function(sett)
        {
            if(typeof sett == 'string')
                sett = $scope.getObjFromId(sett);
            var children = getChildren(sett);
            if(isOfType(sett, 'dataid:Agent'))
            {
                $scope.delAgent(sett);
            }
            if(isOfType(sett, 'dataid:Dataset'))
            {
                $scope.delDataset2(sett);
            }
            if(isOfType(sett, 'dataid:Distribution'))
            {
                $scope.delDistribution(sett);
            }
            //TODO Linkset
            for(var i =0; i < children.length; i++) {
                for (var j = 0; j < children[i]['@value'].length; j++) {
                    $scope.delSetAndChildren(children[i]['@value'][j]);
                }
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

        $scope.alert = function(msg)
        {
            alert(msg);
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
            return getAllOfTypes(['dataid:Distribution'], $scope.graph);
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

        $scope.openNewAgent = function(role) {
            if(role === undefined || role == null)
                role = 'Agent';
            var newId = $scope.getNewId($scope.root, 'dataid:' + role);
            $scope.openAgent(getEmptyAgent(newId, []));
        };
        $scope.openAgent = function(agent) {
            var modalDistInstance = $modal.open({
                templateUrl: 'modalAgentContent.html',
                controller: ModalAgentInstanceCtrl,
                size: 'lg',
                backdrop : 'static',
                resolve: {
                    agent: function() {
                        return {agent: agent, languages: $scope.languages, messages: $scope.messages, isTemplate: $scope.isTemplate};
                    }
                }
            });
            modalDistInstance.result.then(function(newAge) {
                $scope.pushNewSet(null, newAge);
            });
        };

        $scope.openNewDataset = function(parent) {
            var newId = $scope.getNewId(parent, 'dataid:Dataset');
            $scope.openDataset(parent, getEmptyDataset(newId, parent['@id']));
        };
        $scope.openDataset = function(parent, dataset) {
            var modalDistInstance = $modal.open({
                templateUrl: 'modalDatasetContent.html',
                controller: ModalDatasetInstanceCtrl,
                size: 'lg',
                backdrop : 'static',
                resolve: {
                    dataset: function() {
                        return {dataset: dataset,
                            messages: $scope.messages,
                            licenses: $scope.licenses,
                            languages: $scope.languages,
                            agents: $scope.getAllAgents(),
                            distributions: $scope.getAllDistributions(),
                            openDist: $scope.openNewDist,
                            openAgent: $scope.openNewAgent,
                            isTemplate: $scope.isTemplate};
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
            if( $scope.graph.indexOf(newSet) == -1)
                $scope.graph.push(newSet);
        };

        $scope.openNewDist = function(parent) {
            var newId = $scope.getNewId(parent, 'dataid:Distribution');
            var distribution = getEmptyDistribution(newId, parent['@id'], 'dataid:SingleFile');
            distribution['dc:license']['@id'] = parent['dc:license']['@id'];
            distribution['dc:rights']['@value'] = parent['dc:rights']['@value'];
            $scope.openDist(parent, distribution);
        };

        $scope.openDist = function(parent, distribution) {

            var modalDistInstance = $modal.open({
                templateUrl: 'modalDistContent.html',
                controller: ModalDistInstanceCtrl,
                size: 'lg',
                backdrop : 'static',
                resolve: {
                    dist: function() {
                        return {distribution: distribution,
                            languages: $scope.languages,
                            licenses: $scope.licenses,
                            mimetypes:$scope.mimetypes,
                            agents: $scope.getAllAgents(),
                            openNewAgent: $scope.openNewAgent,
                            isTemplate: $scope.isTemplate,
                            urlHeaderUrl: $scope.config['evaluateDistUrl']
                        };
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
                resolve: {
                    storeRequest: function() {
                        return  $scope.config['getLatestVersion'];
                    } }
            });
            modalDistInstance.result.then(function(content) {
                $scope.loadDataId(integrateDataId(JSON.parse(content)));
            });
        };

        $scope.openPublished = function(code, agent) {

            var modalDistInstance = $modal.open({
                templateUrl: 'modalPublishContent.html',
                controller: ModalPublishCtrl,
                size: 'md',
                backdrop : 'static',
                resolve: {
                    initials: function() {
                        return  {code: code, dataid: $scope.root, agent: agent};
                    } }
            });
            modalDistInstance.result.then(function(content) {
                //$scope.loadDataId(integrateDataId(JSON.parse(content)));
            });
        };

        $scope.loadDataIdTemplate = function()
        {
            $scope.dataid = getEmptyDataId('http://dataid.dbpedia.org/dataids/myExampleDataId.ttl');
            $scope.graph = $scope.dataid["@graph"];
            $scope.root = $scope.graph[0];
            $scope.agent = $scope.addAgent('dataid:Creator');
            $scope.root['dataid:associatedAgent']['@value'].push($scope.agent['@id']);
            $scope.root = finalizeSet(null, $scope.root);
            $scope.isTemplate = true;
        };

        $scope.loadDataId = function(dataid)
        {
            $scope.dataid = dataid;
            $scope.graph = dataid["@graph"];
            $scope.root = $scope.graph[0];
            $scope.agent = getAllOfTypes(['dataid:Agent'], $scope.graph)[0];
            $('#dataiduri').prop('readonly', true);
            //$scope.root['dataid:associatedAgent']['@value'].push($scope.agent['@id']);
            $scope.validate($scope.dataid);
            $scope.isTemplate = false;
        };

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
            $scope.openValidating();
            $scope.cleanId = cleanDataId(dataid);
            var request = sendRequest($scope.config['validatorEndpoint'], "POST", JSON.stringify($scope.cleanId), true, function(e){
                if(e.target.status >202)
                    alert(e.target.responseText);
                else
                {

                    $scope.validationResult = JSON.parse(e.target.responseText);
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
                }
                validationModalDialog.dismiss('cancel');
            }, function (e) { });
        };

        $scope.publishDataId = function()
        {
            if(($scope.valid == 'true' || $scope.valid == 'warnings') && $scope.cleanId !== undefined && $scope.cleanId != null)
            {
                var url = $scope.config['insertIdEndpoint'];
                url = url.format(encodeURIComponent($scope.root['@id']),
                    encodeURIComponent($scope.currentBranch),
                    encodeURIComponent($scope.agent['@id']),
                    $scope.currentBranch.trim == 'master' ? 1 : 0);

                sendRequest(url, "POST", JSON.stringify($scope.cleanId), true, function(e){
                    if(e.target.status >202)
                        console.log(e.target.responseText);
                    else
                    {
                        $scope.openPublished(e.target.responseText, $scope.agent);
                        $scope.isTemplate = false;
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
            if(value === undefined || value.length < 4 || value.substr(0, 4) != "http")
            {
                value = "http";
                $scope.root['@id'] =  "http";
            }
            if(old === undefined || old.length < 4 || old.substr(0, 4) != "http")
                old = "http";
            for(var i=0; i < $scope.graph.length; i++)
            {
                if($scope.graph[i] != $scope.root && $scope.graph[i]['@id'] !== undefined && $scope.graph[i]['@id'].indexOf(old) >= 0)
                {
                    $scope.$broadcast('uriupdate',{instance:$scope.graph[i], newVal:($scope.graph[i]['@id'].replace(old, value)), oldVal:$scope.graph[i]['@id']});
                    $scope.graph[i]['@id'] = $scope.graph[i]['@id'].replace(old, value);
                }
            }
            $scope.$broadcast('uriupdate',{instance:$scope.root, newVal:value, oldVal:old});
        });

        $scope.$watch("isopenbranch", function(isopen){
            if(isopen)
            {
                $scope.updateBranchGraph();
            }
        });

        $scope.$on('uriupdate', function(event, newId){

            var children = getChildren(newId.instance);
            for(var i=0; i < children.length; i++)
            {
                for(var j=0; j < children[i]['@value'].length; j++) {
                    var c = getById(children[i]['@value'][j], $scope.graph);
                    if (c !== undefined && !!c && c['@parent'] == newId.oldVal) {
                        c['@parent'] = newId.newVal;
                    }
                }
            }
            for(var i=0; i < $scope.graph.length; i++)
            {
                children = getChildren($scope.graph[i]);
                for(var i=0; i < children.length; i++)
                {
                    if(children[i]['@value'].indexOf(newId.oldVal) >= 0)
                    {
                        removeFromArray(newId.oldVal, children[i]['@value']);
                        children[i]['@value'].push(newId.newVal);
                    }
                }
            }
        });

        $scope.loadDataIdTemplate();
        $scope.agentDontShowType=true;
        $scope.agentBoxTitle = 'Enter your contact information';
        $scope.datasetBoxTitle = 'Define a root-dataset';
    });

var PopoverCtrl = function($scope)
{

};

var ModalDatasetInstanceCtrl = function($scope, $modalInstance, $document, dataset) {
    $scope.dataset = dataset.dataset;
    $scope.messages = dataset.messages;
    $scope.agents = dataset.agents;
    $scope.licenses = dataset.licenses;
    $scope.languages = dataset.languages;
    $scope.distributions = dataset.distributions;
    $scope.opendistr = dataset.openDist;
    $scope.openagent = dataset.openAgent;
    $scope.popMap = {};
    $scope.edituri = true;
    $scope.halfuri = {"@required":true, "@value":""};
    $scope.selectedLanguage = 'en';

    $scope.checkIfLicenseSelected = function()
    {
        if( $scope.dataset['dc:license']['@id'] == null)
        {
            $scope.formDataset.distSelect.$setValidity('nolicense', false);
            $scope.formDataset.distSelect.$touched = true;
            return false;
        }
        else
        {
            $scope.formDataset.distSelect.$setValidity('nolicense', true);
            $scope.formDataset.distSelect.$touched = false;
            return true;
        }
    };

    $scope.init = function() {
        var zw = $('#keywords');
        zw.on('itemAdded', function(event) {
            var newVal = angular.copy($scope.dataset['dcat:keyword']['@value']);
            newVal.push(event.item);
            $scope.formDataset.keywords.$setViewValue(newVal);
            //signal its ok to show errors
            $scope.formDataset.keywords.$dirty = true;
            $scope.formDataset.keywords.$touched = true;
            $scope.formDataset.keywords.$untouched = false;
            $scope.$apply();
        });
        zw.on('itemRemoved', function(event) {
            var newVal = angular.copy($scope.dataset['dcat:keyword']['@value']);
            removeFromArray(event.item, newVal);
            $scope.formDataset.keywords.$setViewValue(newVal);
            $scope.$apply();
        });
        zw.tagsinput(
            {
                tagClass: 'label label-xlg label-warning',
                maxTags: 8,
                trimValue: true
            }
        );
    };

    $scope.preparePopover = function(popover, input)
    {
        $scope.popMap[input.$name] = preparePopover(popover, input);
        return $scope.popMap[input.$name] == null ? null : $scope.popMap[input.$name].template;
    };

    $scope.popOverClose = function(parent)
    {
        $scope.formDataset[parent].$touched = false;
        $scope.formDataset[parent].$untouched = true;
    };

    $scope.uriEditClik = function()
    {
        $scope.edituri = !$scope.edituri;
        if($scope.edituri)
            $scope.formDataset.idinput.$setViewValue(encodeURIComponent($scope.dataset['dc:title']['@value']));
    };
    $scope.ok = function() {
        toucheAllInputs($scope.formDataset);
        if($scope.formDataset.$invalid)
            return;
        if($scope.halfuri['@value'].trim().length == 0 || $scope.halfuri['@value'].indexOf('#') >=0
            || !validateURL($scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + $scope.halfuri['@value']))
        {
            $('#idinput').trigger('focus');
            return;
        }

        $scope.dataset['@id'] = $scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + $scope.halfuri['@value'];
        $modalInstance.close($scope.dataset);
    };

    $scope.$on('newAgent', function(event, args){
        $scope.agents.push(args.val);
    });
    $scope.$on('newDistribution', function(event, args){
        $scope.distributions.push(args.val);
    });
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.$watch("dataset['dc:title']['@value']", function(value) {
        $scope.dataset['@id'] = $scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + encodeURIComponent(value);
        if($scope.edituri)
            $scope.halfuri['@value'] = encodeURIComponent(value);
    });
    $scope.$watch("dataset['dc:license']['@value']", function(value) {
        $scope.dataset['dataid:licenseName']['@value'] = $.grep($scope.licenses, function(e){ return e.val == value; }).name;
    });
    $scope.$watch("selectedLanguage", function(value) {
        initializeLanguage($scope.dataset, value);
    });
    $scope.$watch("dataset['dc:license']['@id']", function(value){
        var zw = _.detect($scope.licenses, function(d) { return d.license.value == value; });
        if(zw !== undefined && !!zw)
            $scope.dataset['dataid:licenseName']['@value'] = zw.label.value;
    });
    $scope.$watch("dataset['@id']", function(value, old){
        $scope.$root.$broadcast('uriupdate',{instance:$scope.dataset, newVal:value, oldVal:old});
    });

};

var ModalAgentInstanceCtrl = function($scope, $modalInstance , $document, agent) {
    $scope.dataset = agent.agent;
    $scope.agent = agent.agent;
    $scope.messages = agent.messages;
    $scope.languages = agent.languages;
    $scope.edituri = true;
    $scope.halfuri = {"@required":true, "@value":""};
    $scope.popMap = {};

    $scope.uriEditClik = function()
    {
        $scope.edituri = !$scope.edituri;
        if($scope.edituri)
            $scope.formAgent.idinput.$setViewValue(encodeURIComponent($scope.dataset['dc:title']['@value']));
    };

    $scope.preparePopover = function(popover, input)
    {
        if(typeof input == 'string')
            input = $scope.formAgent[input];
        $scope.popMap[input.$name] = preparePopover(popover, input);
        return $scope.popMap[input.$name] == null ? null : $scope.popMap[input.$name].template;
    };

    $scope.ok = function() {
        toucheAllInputs($scope.formAgent);
        if($scope.formAgent.$invalid)
            return;
        if($scope.halfuri['@value'].trim().length == 0 || $scope.halfuri['@value'].indexOf('#') >=0
            || !validateURL($scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + $scope.halfuri['@value']))
        {
            $('#idinput').trigger('focus');
            return;
        }
        $scope.dataset['@type'] = ['dataid:Agent', 'dataid:' + $scope.dataset['dataid:agentRole']['@value']];
        $scope.dataset['@id'] = $scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + $scope.halfuri['@value'];
        $modalInstance.close($scope.dataset);
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.$watch("dataset['dataid:agentName']['@value']", function(value) {
        $scope.dataset['@id'] = $scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + encodeURIComponent(value);
        if($scope.edituri)
            $scope.halfuri['@value'] = encodeURIComponent(value);
    });
    $scope.$watch("selectedLanguage", function(value) {
        initializeLanguage($scope.dataset, value);
    });
    $scope.someTypeSelected = function () {
        return $scope.dataset['@type'].filter(function( obj ) { return !!obj && obj.trim() != 'dataid:Agent'; }).length == 0;
    };

    $scope.$watch("dataset['@id']", function(value, old){
        $scope.$root.$broadcast('uriupdate',{instance:$scope.dataset, newVal:value, oldVal:old});
    });
    $scope.selectedLanguage = 'en';
};

var ModalDistInstanceCtrl = function($scope, $modalInstance, dist) {
    $scope.dataset = dist.distribution;
    $scope.licenses = dist.licenses;
    $scope.agents = dist.agents;
    $scope.languages = dist.languages;
    $scope.openagent = dist.openNewAgent;
    $scope.mimetypes = dist.mimetypes;
    $scope.urlHeaderUrl = dist.urlHeaderUrl;
    $scope.popMap = {};
    $scope.edituri = true;
    $scope.halfuri = {"@required":true, "@value":""};
    $scope.urlHeaderMap = {};

    $scope.init = function() {
        $scope.disableFormats(true);
    };

    $scope.preparePopover = function(popover, input)
    {
        $scope.popMap[input.$name] = preparePopover(popover, input);
        return $scope.popMap[input.$name] == null ? null : $scope.popMap[input.$name].template;
    };

    $scope.popOverClose = function(parent)
    {
        $scope.formDist[parent].$touched = false;
        $scope.formDist[parent].$untouched = true;
    };

    $scope.uriEditClik = function()
    {
        $scope.edituri = !$scope.edituri;
        if($scope.edituri)
            $scope.formDist.idinput.$setViewValue(encodeURIComponent($scope.dataset['dc:title']['@value']));
    };

    $scope.ok = function() {
        toucheAllInputs($scope.formDist);
        if($scope.formDist.$invalid)
            return;
        if($scope.halfuri['@value'].trim().length == 0 || $scope.halfuri['@value'].indexOf('#') >=0
            || !validateURL($scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + $scope.halfuri['@value']))
        {
            var zw = $('#idinput');
            zw.trigger('focus');
            return;
        }

        $scope.dataset['@id'] = $scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + $scope.halfuri['@value'];
        $modalInstance.close($scope.dataset);
    };

    $scope.evalDistUrl = function(url){
        var tarea = $('#downloadURL');
        tarea.css('height', 'auto' );
        tarea.height( tarea.scrollHeight );
        url = url.replace("www.","").trim();
        if(url.indexOf("http") != 0 && url.indexOf("ftp") != 0)
            url = "http://" + url;
        $scope.formDist.downloadURL.$setViewValue(url);
        url = encodeURIComponent(url);

        sendRequest($scope.urlHeaderUrl + url, "GET", null, true, function(e){
            if(e.target.status >202) {
                if (e.target.responseText.indexOf("htmlresource") == 0)
                    $scope.formDist.downloadURL.$setValidity('htmlresource', false);
            }
            else
            {
                $scope.urlHeaderMap = JSON.parse(e.target.responseText);
                $scope.formDist.downloadURL.$setValidity('htmlresource', true);
            }

            $scope.$apply();
        }, function(e){
            alert(e.target.responseText);
        });
    };

    $scope.calcSizeString = function(bytesize)
    {
        if(bytesize === undefined)
            return "";
        var giga = 1073741824;
        var mega = 1048576;
        var kilo = 1024;
        if(typeof bytesize != 'string')
            bytesize = Number(bytesize);
        if(bytesize >= giga)
            return (bytesize/giga).toFixed(1) + " GB";
        if(bytesize >= mega)
            return (bytesize/mega).toFixed(1) + " MB";
        if(bytesize > kilo)
            return (bytesize/kilo).toFixed(1) + " KB";
        return bytesize.toString() + " B";
    };

    $scope.getExtention = function()
    {
        var array = $scope.urlHeaderMap["Compression"];
        var ret = "";
        if(array === undefined)
            return ret;
        for(var i=array.length-1; i >= 0 ; i--)
            if(array[i] != null)
                ret += "." + array[i];

        var filter = $('.multiselect-search', $('#dataInformationPanel'));
        filter.val($scope.urlHeaderMap["Extension"][0]);
        filter.keydown();
        return ret;
    };

    $scope.disableFormats = function(disable)
    {
        var formats = $('.multiselect', $('#dataInformationPanel'));
        formats.prop('disabled', disable);
    };

    $scope.$on('newAgent', function(event, args){
        $scope.agents.push(args.val);
    });

    $scope.$watch("dataset['dc:title']['@value']", function(value) {
        $scope.dataset['@id'] = $scope.dataset['@id'].substring(0, $scope.dataset['@id'].indexOf('#')) + '#' + encodeURIComponent(value);
        if($scope.edituri)
            $scope.halfuri['@value'] = encodeURIComponent(value);
    });

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.$watch("selectedLanguage", function(value) {
        initializeLanguage($scope.dataset, value);
    });
    $scope.$watch("dataset['@id']", function(value, old){
        $scope.$root.$broadcast('uriupdate',{instance:$scope.dataset, newVal:value, oldVal:old});
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

var ModalUploadCtrl = function($scope, $modalInstance, storeRequest)
{
    $scope.dataidtext = "paste a DataID here...";
    $scope.filepath = "";
    $scope.uriuri = "";

    $scope.getIdFromTheStore = function(uri)
    {
        uri = storeRequest + encodeURI(uri);
        var request = sendRequest(uri, "GET", null, false, function(e){
            if(e.target.status >202)
                console.log(e.target.responseText);
            else
            {
                $scope.dataidtext = e.target.responseText;
            }
        }, function (e) { console.log(e.target.responseText); });
    };

    $scope.$watch('uriuri', function (newVal) {
        if(newVal.length >0)
            $scope.getIdFromTheStore(newVal);
    }, true);

    $scope.ok = function() {
        $modalInstance.close($scope.dataidtext);
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};

var ModalPublishCtrl = function($scope, $modalInstance, initials)
{
    $scope.code = initials.code;
    $scope.dataid = initials.dataid;
    $scope.agent = initials.agent;

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
        newSet['dc:modified']['@value'] = new Date().toISOString();
    }
    /*    if(!!parent)
     newSet['@id'] = refactorNewId(parent['@id'], name);*/

    return newSet;
}

/*function refactorNewId(parentId, newName)
 {
 var zw = parentId.substring(0, Math.max(parentId.lastIndexOf('#')+1, parentId.lastIndexOf('/')+1));
 return zw + encodeURI(newName);
 }*/

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
        if(obj['@id'] === undefined || obj['@id'] == null)
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
            if (key != '@parent' && key != '@required' && key != "$$hashKey" && key != "@pristine" && key != "@label" && key != "@validators" && key != "@invalid")
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
            parentMap[zw[j]['@id']] = dataid['@graph'][i]['@id'];
        }
        zw = getOptionalArray(dataid['@graph'][i], 'dcat:distribution');
        for(var j =0; j < zw.length; j++)
        {
            parentMap[zw[j]['@id']] = dataid['@graph'][i]['@id'];
        }
        zw = getOptionalArray(dataid['@graph'][i], 'foaf:topic');
        for(var j =0; j < zw.length; j++)
        {
            parentMap[zw[j]['@id']] = dataid['@graph'][i]['@id'];
        }
    }
    for(var i=0; i < dataid['@graph'].length; i++)
    {
        //TODO check uniqueArray!!
        if(isOfType(dataid['@graph'][i], 'dataid:Agent'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyAgent(dataid['@graph'][i]['@id'], uniqueArray(['dataid:Agent'], dataid['@graph'][i]['@type']))));
        else if(isOfType(dataid['@graph'][i], 'dataid:Dataset'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyDataset(dataid['@graph'][i]['@id'], parentMap[dataid['@graph'][i]['@id']])));
        else if(isOfType(dataid['@graph'][i], 'dataid:Distribution'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyDistribution(dataid['@graph'][i]['@id'], parentMap[dataid['@graph'][i]['@id']], uniqueArray(['dataid:Distribution'], dataid['@graph'][i]['@type']))));
        else if(isOfType(dataid['@graph'][i], 'dataid:Linkset'))
            retDataId['@graph'].push(integrateSet(dataid['@graph'][i], getEmptyLinkset(dataid['@graph'][i]['@id'])));
    }
    console.log(retDataId);
    return retDataId;
}

function getChildren(sett)
{
    var children = [];
    if(sett['dataid:associatedAgent'] != null)
        children = children.concat(sett['dataid:associatedAgent']);
    if(sett['dcat:distribution'] != null)
        children = children.concat(sett['dcat:distribution']);
    if(sett['void:subset'] != null)
        children = children.concat(sett['void:subset']);
    if(sett['foaf:topic'] != null)
        children = children.concat(sett['foaf:topic']);
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
    if(orig['dataid:associatedAgent'] !== undefined) {
        if(typeof orig['dataid:associatedAgent'] == 'object')
            orig['dataid:associatedAgent'] = [orig['dataid:associatedAgent']];
    }
    if(orig['dcat:distribution'] !== undefined) {
        if(typeof orig['dcat:distribution'] == 'object')
            orig['dcat:distribution'] = [orig['dcat:distribution']];
    }
    if(orig['void:subset'] !== undefined) {
        if(typeof orig['void:subset'] == 'object')
            orig['void:subset'] = [orig['void:subset']];
    }
    if(orig['foaf:topic'] !== undefined) {
        if(typeof orig['foaf:topic'] == 'object')
            orig['foaf:topic'] = [orig['foaf:topic']];
    }
    if(orig['@type'] !== undefined) {
        if(orig['@type'].constructor !== Array)
            orig['@type'] = [orig['@type']];
    }
    for(var key in template) {
        if(orig[key] === undefined || !orig[key])
            continue;
        if(typeof(template[key]) == 'object') {
            if ('@language' in template[key]) {
                var langs = {};
                if (orig[key].constructor === Array) {
                    for (var i = 0; i < orig[key].length; i++) {
                        langs[orig[key][i]['@language']] = orig[key][i]['@value'];
                    }
                }
                else {
                    langs[orig[key]['@language']] = orig[key]['@value'];
                }
                template[key]['@value'] = langs;

            }
            else if (!!template[key]['@value'] && template[key]['@value'].constructor === Array) {
                if (!!orig[key] && orig[key].constructor === Array)
                {
                    if(!!orig['@type'] && orig['@type'][0].trim().substr(0,4) != "xsd:")
                        template[key]['@value'] = idsToStrings(orig[key]);
                    else
                        template[key]['@value'] = orig[key];
                }
                else
                    template[key]['@value'].push(orig[key]);
            }
            else {
                if(typeof orig[key] == 'string')
                    template[key]['@value'] = orig[key];
                if (orig[key]['@value'] !== undefined)
                    template[key]['@value'] = orig[key]['@value'];
                if (orig[key]['@type'] !== undefined)
                {
                    if (orig[key]['@type'].constructor !== Array)
                        template[key]['@type'] = [orig[key]['@type']];
                    else
                        template[key]['@type'] = orig[key]['@type'];
                }
                if(orig[key]['@id'] !== undefined)
                    template[key]['@id'] = orig[key]['@id'];
                if(orig[key]['@language'] !== undefined)
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

function validateURL(textval) {
    var urlregex = /^\s*[a-z](?:[-a-z0-9\+\.])*:(?:\/\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA000-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:])*@)?(?:\[(?:(?:(?:[0-9a-f]{1,4}:){6}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|::(?:[0-9a-f]{1,4}:){5}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){4}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4}:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){3}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,2}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){2}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,3}[0-9a-f]{1,4})?::[0-9a-f]{1,4}:(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,4}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,5}[0-9a-f]{1,4})?::[0-9a-f]{1,4}|(?:(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{1,4})?::)|v[0-9a-f]+[-a-z0-9\._~!\$&\'\(\)\*\+,;=:]+)\]|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}|(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=@])*)(?::[0-9]*)?(?:\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@]))*)*|\/(?:(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@]))+)(?:\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@]))*)*)?|(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@]))+)(?:\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@]))*)*|(?!(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@])))(?:\?(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@])|[\uE000-\uF8FF\uF0000-\uFFFFD|\u100000-\u10FFFD\/\?])*)?(?:\#(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\._~\uA0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF\u10000-\u1FFFD\u20000-\u2FFFD\u30000-\u3FFFD\u40000-\u4FFFD\u50000-\u5FFFD\u60000-\u6FFFD\u70000-\u7FFFD\u80000-\u8FFFD\u90000-\u9FFFD\uA0000-\uAFFFD\uB0000-\uBFFFD\uC0000-\uCFFFD\uD0000-\uDFFFD\uE1000-\uEFFFD!\$&\'\(\)\*\+,;=:@])|[\/\?])*)?\s*$/;
    return urlregex.test(textval);
}

function isOfType(obj, type)
{
    if(!obj['@type'])
        return false;
    var typeArr = obj['@type'];
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

function preparePopover(popover, input)
{
    var pd = getPopoverContext()[popover];
    var pops = [];
    for(var key in input.$error)
    {
        var pop = {};
        if(input.$error[key])
        {
            pop.title = pd.title[key];
            pop.message = pd.message[key];
            pop.invalid = true;
            pop.open = true;
            pop.error = input.$error[key];
            pop.delay = ('delay' in pd) ? pd.delay[key] :   0;
            pop.template = pd.templateUrl;
            pop.priority = !!pd.priority[key] ? pd.priority[key] : 3;
        }
        if(pops.length == 0 || pops[0].priority > pop.priority)
            pops.push(pop);
    }
    return pops[pops.length-1];
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

//works only for virtuoso json result format
function getJsonResource(url){
    var outerValue = null;
    sendRequest(url, "GET", null, false, function(e){
        if(e.target.status >202)
            console.log(e.target.responseText);
        else
        {
            var zw =  JSON.parse(e.target.responseText);
            outerValue =  zw.results.bindings;
        }
    }, function(e){
        alert(e.target.responseText);
    });
    return outerValue;
}

function toucheAllInputs(form)
{
    $('[name=' + form.$name + ']').each(function(){
        $(this).find(':input').each(function()
        {
            if(this.getAttribute('name') in form) {
                form[this.getAttribute('name')].$touched = true;
                setValidity(form[this.getAttribute('name')]);
            }
        });
    });
}

dataIdGen.controller('PopoverCtrl', PopoverCtrl);

//validators
/*dataIdGen.directive('integer', function()
{
    return{
        replace: true,
        scope: false,
        require: 'ngModel',
        link: function(scope, orgele, attrs, ngModel){
            ngModel.$validators.integer=function(modelValue,viewValue){
                var value=modelValue || viewValue;
                return /^[0-9]+$/.test(value);
            };
        }
    }
});

dataIdGen.directive('uri', function()
{
    return{
        replace: true,
        scope: false,
        require: 'ngModel',
        link: function(scope, orgele, attrs, ngModel){
            ngModel.$validators.uri=function(modelValue,viewValue){
                var value=modelValue || viewValue;
                return validateURL(value);
            };
        }
    }
});

dataIdGen.directive('dataiduri', function()
{
    return{
        replace: true,
        scope: false,
        require: 'ngModel',
        link: function(scope, orgele, attrs, ngModel){
            ngModel.$validators.dataiduri=function(modelValue,viewValue){
                var value=modelValue || viewValue;
                var url = validateURL(value);
                return url && value.indexOf('#') == -1;
            };
        }
    }
});


dataIdGen.directive('parturi', function()
{
    return{
        replace: true,
        scope: false,
        require: 'ngModel',
        link: function(scope, orgele, attrs, ngModel){
            ngModel.$validators.parturi=function(modelValue,viewValue){
                var dataiduri = scope.$eval(orgele.attr('parturi'));
                if(dataiduri.indexOf('#') >= 0)
                    dataiduri = dataiduri.substring(0, dataiduri.indexOf('#')+1);
                else
                    dataiduri = dataiduri + '#';
                var value = modelValue || viewValue;
                var url= validateURL((dataiduri != null ? dataiduri : 'http://somehost.com/path#') + value);
                return url && value.indexOf('#') == -1;
            };
        }
    }
});

dataIdGen.directive('uniqueuri', function()
{
    return{
        replace: true,
        scope: false,
        require: 'ngModel',
        link: function(scope, orgele, attrs, ngModel){
            ngModel.$validators.uniqueuri=function(modelValue,viewValue){
                var dataiduri = scope.$eval(orgele.attr('uniqueuri'));
                var url= validateURL((dataiduri != null ? dataiduri : 'http://somehost.com/path#') + value);
                //TODO DB - unique check
                return true;
            };
        }
    }
});*/

dataIdGen.directive('dataidModel', function()
{
    return{
        replace: true,
        scope: false,
        require: 'ngModel',
        link: function(scope, orgele, attrs, ngModel){
            //ngModel.$options = { updateOn: 'default blur', debounce: { 'default': 500, 'blur': 0 } };
            var realModel = null;
            var optKey = null;

            function initialize() {
                //figure out the actual value property
                if (attrs["dataidModel"] !== undefined && !!attrs["dataidModel"]) {
                    if (attrs["dataidModel"].substr(0, 1) == '@')
                        realModel = attrs["dataidModel"];
                    else {
                        realModel = '@value';
                        optKey = scope.$eval(attrs["dataidModel"]);
                    }
                }
                else {
                    if ('@value' in ngModel.$modelValue)
                        realModel = '@value';
                    if ('@id' in ngModel.$modelValue)
                        realModel = '@id';
                }

                ngModel.getValue = function()
                {
                    if(ngModel.$modelValue === undefined)
                        return null;
                    if(!!optKey)
                        return ngModel.$modelValue[realModel][optKey];
                    else
                        return ngModel.$modelValue[realModel];
                };


                ngModel.$modelValue['@validators'] = {};
                if(attrs['email'] !== undefined)
                    ngModel.$modelValue['@validators'].email = function(value){
                        if(!value)
                            return true;
                        return /^([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x22([^\x0d\x22\x5c\x80-\xff]|\x5c[\x00-\x7f])*\x22))*\x40([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d)(\x2e([^\x00-\x20\x22\x28\x29\x2c\x2e\x3a-\x3c\x3e\x40\x5b-\x5d\x7f-\xff]+|\x5b([^\x0d\x5b-\x5d\x80-\xff]|\x5c[\x00-\x7f])*\x5d))*$/.test(value);
                    };
                if(ngModel.$modelValue['@required'])
                    ngModel.$modelValue['@validators'].required = function(value){
                        return requiredValue(value);
                    };
                if(isOfType(ngModel.$modelValue, 'xsd:integer'))
                    ngModel.$modelValue['@validators'].integer = function(viewValue){
                        if(!viewValue)
                            return true;
                        return /^[0-9]*$/.test(viewValue);
                    };
                if(realModel == '@id')
                    ngModel.$modelValue['@validators'].uri = function(viewValue){
                        if(!viewValue)
                            return true;
                        return validateURL(viewValue);
                    };
                if(attrs['parturi'] !== undefined)
                {
                    ngModel.$modelValue['@validators'].parturi = function(modelValue,viewValue){
                        var dataiduri = scope.$eval(attrs['parturi']);
                        var value = modelValue || viewValue;
                        if(!value)
                            return true;
                        if(dataiduri.indexOf('#') >= 0)
                            dataiduri = dataiduri.substring(0, dataiduri.indexOf('#')+1);
                        else
                            dataiduri = dataiduri + '#';
                        var url= validateURL((dataiduri != null ? dataiduri : 'http://somehost.com/path#') + value);
                        return  url && value.indexOf('#') == -1;
                    };
                }
                if(attrs['uniqueuri'] !== undefined)
                {
                    ngModel.$modelValue['@validators'].uniqueuri = function(modelValue,viewValue){
                        var value = modelValue || viewValue;
                        if(!value)
                            return true;
                        var dataiduri = scope.$eval(attrs['uniqueuri']);
                        //TODO DB - unique check
                    };
                }
                if(attrs['dataiduri'] !== undefined)
                {
                    ngModel.$modelValue['@validators'].dataiduri = function(modelValue,viewValue){
                        var value=modelValue || viewValue;
                        if(!value)
                            return true;
                        var url = validateURL(value);
                        return url && value.indexOf('#') == -1;
                    };
                }
                if(attrs['dirty'] !== undefined)
                {
                    ngModel.$modelValue['@validators'].dirty = function(){
                        return ngModel.$dirty;
                    };
                }
                //clear all validators and push validators
                ngModel.$validators = {};

                ngModel.$viewValue = ngModel.getValue();
                ngModel.$render();
                unregister();
            }

            //call initialize when ngModel is assigned
            var unregister = scope.$watch(function(){
                return ngModel.$modelValue;
            }, initialize);

            //check if subproperty of value (an object key) changed
            scope.$watch(attrs["dataidModel"], function (value) {
                if(!!optKey) {
                    optKey = value;
                    ngModel.$viewValue = ngModel.$modelValue[realModel][optKey];
                    ngModel.$render();
                }
            });

            //watch for outside changes to the model
/*            scope.$watch(ngModel.getValue, function (value) {
                ngModel.$setViewValue(value);
                ngModel.$render();
            });*/

            //add the formatter
            ngModel.$formatters.push(function(value) {
                if(!realModel)
                    return value;
                if(!!optKey)
                    return value[realModel][optKey];
                else
                    return value[realModel];
            });

            //add the parser
            ngModel.$parsers.unshift(function(value) {

                if(!!optKey)
                    ngModel.$modelValue[realModel][optKey] = value;
                else
                    ngModel.$modelValue[realModel]= value;
                setValidity(ngModel, value);
                return ngModel.$modelValue;
            });

        }
    }
});



//validate
function setValidity(ngModel)
{
    if(!ngModel.getValue || !ngModel.$modelValue)
        return;
    var  value = ngModel.getValue();
    for(var key in ngModel.$modelValue['@validators'])
    {
        ngModel.$setValidity(key, ngModel.$modelValue['@validators'][key](ngModel.getValue()));
    }
    ngModel.$modelValue['@invalid'] = isValueValid(ngModel, value);
    ngModel.$render();
}

function isValueValid(ngModel)
{
    if(!ngModel.getValue)
        return;
    var value = ngModel.getValue();
    if(!ngModel.$error.required)
    {
        if(!ngModel.$dirty)
            return false;
        else
        {
            if(value !== undefined && value != null)
            {
                if(value.constructor === Array && value.length == 0)
                    return false;
                if(typeof value == 'string' && value == "")
                    return false;
            }
            else
                return false;
        }
    }
    if(ngModel.$valid && !ngModel.$error.required)
        return false;
    else
    {
        if(ngModel.$touched)
            return true;
        else
            return false;
    }
}

function requiredValue(value)
{
    if (value === undefined || value == null)
        return false;
    else
    {
        if(value.constructor === Array)
        {
            if(value.length > 0)
                return true;
            else
                return false;
        }
        else if(typeof value == 'string')
        {
            if(value.length > 0)
                return true;
            else
                return false;
        }
        else
            return false;
    }
}

//dropdown button incl. validator
dataIdGen.directive('multiselectdropdown', function ($compile) {
    return {
        replace: true,
        scope: false,
        require: 'ngModel',
        link: function (scope, orgele, attrs, ngModel) {
            var element = $(orgele[0]);
            element.multiselect({
                buttonClass: 'btn',
                buttonWidth: '100%',
                nonSelectedText: attrs["multiselectplaceholder"] !== undefined ? attrs["multiselectplaceholder"] : attrs["multiple"] !== undefined ? 'select one or more' : 'select one' ,
                selectionPrefix: attrs["multiselectprefix"] !== undefined ? attrs["multiselectprefix"] : '',
                enableCaseInsensitiveFiltering: attrs["multiselectfilter"] === undefined ? false : true,
                includeFilterClearBtn: true,
                filterBehavior: 'both',
                dropRight: true,
                inheritClass: true,
                maxHeight: 350,
                numberDisplayed: 1,
                disableIfEmpty: true,
                noDefaultOption: true,
                templates: {
                    button: '<input name="multiselectbutton" type="button" class="multiselect dropdown-toggle" data-toggle="dropdown">',
                    ul: '<ul class="multiselect-container dropdown-menu" style="width:100%;"></ul>'
                },
                onChange: function(optionElement, checked) {
                    optionElement.parent().removeClass("ng-untouched");
                    optionElement.parent().addClass("ng-touched");
                    ngModel.$touched = true;
                    ngModel.$untouched = false;
                    var modelValue = !!ngModel.getValue ? ngModel.getValue() : ngModel.$modelValue; // current model value - array of selected items
                    var result = scope[attrs["multiselectdropdown"]][Number(optionElement[0].value)];
                    result = result[attrs["multiselectidtag"] !== undefined ? attrs["multiselectidtag"] : '@id'];
                    if (_.has(result, 'value'))
                        result = result['value'];
                    optionElement.prop('selected', false);

                    if (checked) {
                        if (modelValue != null && modelValue.constructor === Array) { // current option value is not in model - add it
                            if(modelValue.indexOf(result) == -1){

                                if(attrs["multiple"] === undefined)
                                    modelValue[0] = result;
                                else
                                    modelValue.push(result);
                            }
                        }
                        else if(modelValue == null || modelValue.constructor !== Array)
                        {
                            modelValue = result;
                        }
                        optionElement.prop('selected', true);
                    } else if (modelValue != null &&  modelValue.constructor === Array) { // if it is - delete it
                        if(modelValue.indexOf(result) > -1) {
                            modelValue.splice(modelValue.indexOf(result), 1);
                        }
                    }
                    else if(modelValue == null || modelValue.constructor !== Array)
                    {
                        modelValue = null;
                    }
                    //validity check
                    //ngModel.$setValidity('multiselectValidatior', scope.validateSelectArray(orgele, modelValue));

                    modelValue = angular.copy(modelValue);
                    ngModel.$setViewValue(modelValue);
                    ngModel.$render();
                    scope.$apply();
                },
                onDropdownShown: function(event) {
                    if(scope[attrs["multiselectdropdown"]] === undefined || scope[attrs["multiselectdropdown"]].length < 1)
                    {
                        event.stopPropagation();
                        alert("No items in this selection. Please add a new instance first.");
                    }
                }
            });

            //default
            //ngModel.$setValidity('multiselectValidatior', false);

            scope.validateSelectArray = function(element, value)
            {
                if(element.attr("multiselectvalidate") === undefined)
                    return true;
                else {
                    return requiredValue(value);
                }
            };

            $compile($('input[name="multiselectbutton"]'))(scope);
            if(attrs["addfunction"] !== undefined)
            {
                var addb = $compile("<button type='button' class='btn btn-primary' style='display:table-cell; float: right; margin-left: 5px;' ng-click=\"" + attrs["addfunction"] + "\" >" + attrs["addlabel"] + "</button>");
                orgele.parent().append(addb(scope));
                element.next().css("display", "table-cell");
            }

            // Watch for any changes to the length of our select element
            scope.$watch(function () {
                return element[0].length;
            }, function (newVal, oldVal) {
                element.multiselect('rebuild');
            });

            // Watch for any changes from outside the directive and refresh
            scope.$watch(attrs.ngModel, function (value) {
                element.multiselect('refresh');
                //ngModel.$setValidity('multiselectValidatior', scope.validateSelectArray(orgele, value));
            });
        }
    };
});