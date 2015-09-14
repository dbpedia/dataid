/**
 * Created by Chile on 9/9/2015.
 */
var dataIdGen = angular.module('dataIdGen', ['d3', 'angularJsonld', 'ui.bootstrap', 'ngSanitize'])
var context = null;

dataIdGen.config(function(jsonldContextProvider){
    /* If we need to change the semantics of 'fullName' we just do it here for the entire application */
    context = getContext();
    jsonldContextProvider.add(context);
});

function genController($scope, $modal, $http) {
    $scope.messages = {};
    $scope.messages.notValidURI = "Not a valid URI!";
    $scope.showValidation = false;
    $scope.newIdSwitch = false;
    $scope.currentTab = 'creatr';
    $scope.languages = getLanguages();
    $scope.selectedLanguage = 'en';
    $scope.agentRoles = ["Creator", "Maintainer", "Publisher", "Contact", "Contributor" ];
    $scope.validationResult = {'@graph':[], '@context':{}};

    $scope.countInstancesOfAnyType = function()
    {
        var counts = {};
        for(var i = 0; i< $scope.dataid['@graph'].length; i++) {
            var num = $scope.dataid['@graph'][i]['@type'];
            counts[num] = counts[num] ? counts[num]+1 : 1;
        }
        return counts;
    };

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

    $scope.addAgent = function(type) {
        var newId = $scope.getNewId($scope.root, type);
        var agent = getEmptyAgent(newId, [type])
        $scope.graph.push(agent);
        return  agent;
    };


    $scope.delAgent = function(dataset) {
        removeFromArray(dataset, $scope.graph);
        for(var i =0; i < $scope.graph.length; i++)
        {
            var arr = $scope.graph[i]['dataid:associatedAgent'];
            if(arr != undefined)
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

    //TODO deleted datasets=
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
        var ret = [];
        for(var i=0; i < $scope.graph.length; i++)
        {
            if(isOfType($scope.graph[i], 'dataid:Agent'))
                ret.push($scope.graph[i]);
        }
        return ret;
    };

    $scope.getAllDistributions = function()
    {
        var ret = [];
        for(var i=0; i < $scope.graph.length; i++)
        {
            if(isOfType($scope.graph[i], 'dataid:Distribution') || isOfType($scope.graph[i], 'dataid:SparqlEndpoint'))
                ret.push($scope.graph[i]);
        }
        return ret;
    };

    $scope.openWelcome = function() {

        var modalDistInstance = $modal.open({
            templateUrl: 'welcome.html',
            controller: ModalWelcomeCtrl,
            size: 'md',
            backdrop : 'static',
            resolve: {            }
        });
        modalDistInstance.result.then(function(resp) {
            if(resp == 'learn')
                alert('learn');
            if(resp == 'newId')
                $scope.newIdSwitch = true;
            if(resp == 'loadId')
                alert('loadId');
        });
    };

    $scope.openNewAgent = function() {
        $scope.openAgent(null);
    };
    $scope.openAgent = function(agent) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalAgentContent.html',
            controller: ModalAgentInstanceCtrl,
            size: 'lg',
            backdrop : 'static',
            resolve: {
                agent: function() {
                    var newId = $scope.getNewId($scope.root, 'dataid:Agent');
                    if(agent == null)
                        agent = getEmptyAgent(newId, []);
                    return {agent: agent, messages: $scope.messages};
                }
            }
        });
        modalDistInstance.result.then(function(agent) {
            $scope.pushNewSet(null, agent);
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
                    var newId = $scope.getNewId(parent, 'dataid:Dataset');
                    if(dataset == null)
                        dataset = getEmptyDataset(newId, parent['@id']);
                    return {dataset: dataset, messages: $scope.messages, licenses: getLicenses(), agents: $scope.getAllAgents(), distributions: $scope.getAllDistributions(), openDist: $scope.openDist, openAgent: $scope.openAgent};
                }
            }
        });
        modalDistInstance.result.then(function(retSet) {
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
                    var newId = $scope.getNewId(parent, 'dataid:Distribution');
                    if(distribution == null)
                        distribution = getEmptyDistribution(newId, parent['@id'], 'dataid:Distribution');
                    return {distribution: distribution, license: getLicenses(), scope: $scope};
                }
            }
        });
        modalDistInstance.result.then(function(dist) {
            $scope.pushNewSet(parent, dist);
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

    $scope.validationResult = {};

    $scope.putOut = function(dataid)
    {
        var ttl = cleanDataId(dataid);
        console.log(ttl);
        var request = sendRequest("http://localhost:9995/dataid/publisher/validateid", "POST", JSON.stringify(ttl), true, function(e){
            if(e.target.status >202)
                alert(request.responseText);
            else
                $scope.validationResult = JSON.parse(request.responseText);
            console.log($scope.validationResult);
        }, function (e) { });
    };

    $scope.$watchCollection('graph', function(newVal){
            if(isOfType(newVal[newVal.length-1], 'dataid:Agent'))
                $scope.$root.$broadcast('newAgent',{"val":newVal[newVal.length-1]});
            else if(isOfType(newVal[newVal.length-1], 'dataid:Distribution'))
                $scope.$root.$broadcast('newDistribution',{"val":newVal[newVal.length-1]});
            else if(isOfType(newVal[newVal.length-1], 'dataid:Dataset'))
                $scope.$root.$broadcast('newDataset',{"val":newVal[newVal.length-1]});
    });

    $scope.dataid = getEmptyDataId('http://example.org/dataids#dataid1');
    $scope.graph = $scope.dataid["@graph"];
    $scope.root = $scope.graph[0];
    $scope.root = finalizeSet(null, $scope.root);
        $scope.agent = $scope.addAgent('dataid:Creator');
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
    }
    $scope.selectedLanguage = 'en';
};

var ModalDistInstanceCtrl = function($scope, $modalInstance, dist) {
    $scope.distribution = dist.distribution;
    $scope.license = dist.license;
    $scope.agents = dist.scope.getAllAgents();
    $scope.languages = getLanguages();
    $scope.foreignScope = dist.scope;
    $scope.openAgent = dist.scope.openAgent;

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
    $scope.selectedLanguage = 'en';
};

var ModalWelcomeCtrl = function($scope, $modalInstance) {
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
        if(!!newSet['dc:issued']['@value'])
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

    return c1long.trim().toLowerCase() == c2long.trim().toLowerCase();
}

function cleanDataId(obj) {
    if(obj === null || typeof(obj) !== 'object' || 'isActiveClone' in obj)
        return obj;

    var temp = obj.constructor();

    if('@value' in obj)
    {
        if(obj['@value'] == null)
            return null;
        if(obj['@value'].constructor === Array)
            return obj['@value'];
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
    for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
            obj['isActiveClone'] = null;
            if (key != '@parent' && key != '@required' && key != "$$hashKey")
            {
                var zw = cleanDataId(obj[key]);
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
                    template[key]['@value'] = orig[key];
                else
                    template[key]['@value'].push(orig[key]);
            }
            else {
                template[key]['@value'] = orig[key]['@value'];
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
            if(isOfType(graph[i], types[j]))
                ret.push(graph[i]);
        }
    }
    return ret;
}

getById = function(id, graph)
{
    return graph.filter(function( obj ) { return obj['@id'] == id; })[0];
};

dataIdGen.directive('d3Bars', ['$window', '$timeout', 'd3Service',
    function($window, $timeout, d3Service) {
        return {
            restrict: 'A',
            scope: false,
            link: function(scope, ele, attrs) {
                d3Service.d3().then(function(d3) {

                    var renderTimeout;
                    var margin = parseInt(attrs.margin) || 20,
                        barHeight = parseInt(attrs.barHeight) || 20,
                        barPadding = parseInt(attrs.barPadding) || 5;

                    $window.onresize = function() {
                        scope.$apply();
                    };

/*                    scope.$watch(function() {
                        return angular.element($window)[0].innerWidth;
                    }, function() {
                        scope.render(scope.data);
                    });*/

                    scope.$watch('validationResult', function(newData) {
                        alert(JSON.stringify(newData));
                        scope.render(newData);
                    }, true);

                    scope.render = function(data) {
                        var layers = getLayers(data);
                        var counts = {};
                        for(var i = 0; i< layers.length; i++) {
                            var num = layers[i]["level"];
                            counts[num] = counts[num] ? counts[num]+1 : 1;
                        }
                        var peopleTable = tabulate(d3.select("#d3canvas"), ["level","resource", "message"], layers);

                        resultTitle("Validation result: number of tests: " + result["@graph"][0]["testsRun"] + ", number of errors: " +
                        counts["rlog:ERROR"] != null ? counts["rlog:ERROR"] : 0 + ", number of warnings: " +
                        counts["rlog:WARN"] != null ? counts["rlog:WARN"] : 0);
                        // uppercase the column headers
                        peopleTable.selectAll("thead th")
                            .text(function (column) {
                                return column.charAt(0).toUpperCase() + column.substr(1);
                            })
                            .style("background-color", function(d) {
                                return "#B2D1E0";
                            });
                        peopleTable.selectAll("tbody td")
                            .text(function (td) {
                                if(td.column == "level")
                                    return td.value.replace('rlog:', '').replace('WARN', 'WARNING');
                                else
                                    return td.value;
                            });

                        peopleTable.selectAll("tbody tr")
                            .style("background-color", function(d) {
                                //d2 will be the same as d, but j will always be 0
                                //since d3.select(this) only has one element
                                if (d["level"] == "rlog:ERROR"){
                                    return "#FF5C33";
                                }
                                else if (d["level"] == "rlog:WARN"){
                                    return "#FFD633";
                                }
                                else {
                                    return "#ffffff" ;
                                }
                            } );

                        $('table').DataTable({
                            "order": [[ 1, "asc" ]]
                        });
                    };
                });
            }}
    }])