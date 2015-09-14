var dataIdGen = angular.module('dataIdGen', ['angularJsonld', 'ui.bootstrap', 'ngSanitize']);

function genController($scope, $modal, $http) {
    dt = new Date();
    dt = (dt.getMonth() + 1) + '-' + dt.getDate() + '-' + dt.getFullYear();

    $scope.messages = new Object();
    $scope.messages.notValidURI = "Not a valid URI!";
    $scope.showValidation = false;

    $scope.license = [
        {name: 'Creative Commons Attribution 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by'},
        {name: 'Creative Commons Attribution-ShareAlike 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-sa'},
        {name: 'Creative Commons Attribution-NoDerivatives 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-nd'},
        {name: 'Creative Commons Attribution-NonCommercial 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-nc'},
        {name: 'Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-nc-nd'},
        {name: 'Creative Commons Public Domain Dedication', val: 'http://purl.oclc.org/NET/rdflicense/cc-zero'},
        {name: 'Open Data Commons Attribution License', val: 'http://purl.oclc.org/NET/rdflicense/odc-by'},
        {name: 'Open Data Commons Public Domain Dedication and License', val: 'http://purl.oclc.org/NET/rdflicense/odc-pddl'},
        {name: 'GNU General Public License', val: 'http://purl.oclc.org/NET/rdflicense/gpl-3.0'}
    ]
    emptyDataSet = {
        parentDataset: [],
        datasetURI: '',
        type: 'main',
        main: true,
        title: 'New subset',
        label: '',
        description: '',
        keyword: '',
        issued: dt,
        rights: '',
        rootResource: '',
        exampleResource: '',
        language: '',
        licenseName: '',
        landingPage: '',
        ontologyLocation: '',
        versionInfo: '',
        distribution: [],
        linkset: [],
        agent: [],
        datasets: [],
        wasDerivedFromTitle: '',
        wasDerivedFromAgent: [],
        wasDerivedFromResource: '',
        wasGeneratedByTitle: '',
        wasGeneratedByAgent: [],
        wasGeneratedByResource: '',
        license: []
    }

    $scope.object = new Object();
    $scope.object.dataSets = [];
    $scope.object.format = 'turtle';

    $scope.object.dataSets.ds = [];

    var copiedDataset = {};
    var copiedDatasetFinal = {};
    jQuery.extend(copiedDataset, emptyDataSet);
    copiedDataset.title = 'Main Dataset';
    $scope.object.dataSets.push(copiedDataset);
    $scope.ds = copiedDataset;


    var i = 0;
    $scope.addDataset2 = function(dataset) {
        var copiedDataset = {};
        jQuery.extend(copiedDataset, emptyDataSet);
        copiedDataset.main = false;
        copiedDataset.parentDataset = dataset;
        copiedDataset.title = copiedDataset.title + i;
        copiedDataset.distribution = [];
        copiedDataset.linkset = [];
        copiedDataset.type = 'void:subset';
        copiedDataset.agent = [];
        copiedDataset.datasets = [];
        copiedDataset.wasDerivedFromAgent = [];
        copiedDataset.wasGeneratedByAgent = [];
        i++;
        dataset.datasets.push(copiedDataset);
        console.log(dataset.datasets);
    };

    $scope.delDataset2 = function(dataset) {
        var index = dataset.parentDataset.datasets.indexOf(dataset);
        dataset.parentDataset.datasets.splice(index, 1);
    }
    $scope.removeAllDist = function() {
        $scope.ds.distribution = [];
    }


    $scope.changeSubset = function(subset) {
        $scope.ds = subset;
    }
    $scope.addLinkset = function(linkset) {
        $scope.ds.linkset.push(linkset);
    }
    $scope.addAgent = function(agent) {
        $scope.ds.agent.push(agent);
    }
    $scope.addWasDerivedFromAgent = function(wasDerivedFromAgent) {
        $scope.ds.wasDerivedFromAgent.push(wasDerivedFromAgent);
    }
    $scope.addWasGeneratedByAgent = function(wasGeneratedByAgent) {
        $scope.ds.wasGeneratedByAgent.push(wasGeneratedByAgent);
    }

    $scope.removeLinkset = function(linkset) {
        var index = $scope.ds.linkset.indexOf(linkset);
        $scope.ds.linkset.splice(index, 1);
    }
    $scope.removeAgent = function(agent) {
        var index = $scope.ds.agent.indexOf(agent);
        $scope.ds.agent.splice(index, 1);
    }
    $scope.removeWasDerivedFromAgent = function(wasDerivedFromAgent) {
        var index = $scope.ds.wasDerivedFromAgent.indexOf(wasDerivedFromAgent);
        $scope.ds.wasDerivedFromAgent.splice(index, 1);
    }
    $scope.removeWasGeneratedByAgent = function(wasGeneratedByAgent) {
        var index = $scope.ds.wasGeneratedByAgent.indexOf(wasGeneratedByAgent);
        $scope.ds.wasGeneratedByAgent.splice(index, 1);
    }

    $scope.addDistribution = function(dist) {
        $scope.ds.distribution.push(dist);
    }

    $scope.removeDistribution = function(distribution) {
        var index = $scope.ds.distribution.indexOf(distribution);
        $scope.ds.distribution.splice(index, 1);
    }


    $scope.validateURL = function(textval) {
        var urlregex = new RegExp(
                "^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
        return urlregex.test(textval);
    }

    clone = function(obj) {
        var copy;

        // Handle the 3 simple types, and null or undefined
        if (null == obj || "object" != typeof obj)
            return obj;

        // Handle Date
        if (obj instanceof Date) {
            copy = new Date();
            copy.setTime(obj.getTime());
            return copy;
        }

        // Handle Array
        if (obj instanceof Array) {
            copy = [];
            for (var i = 0, len = obj.length; i < len; i++) {
                copy[i] = clone(obj[i]);
            }
            return copy;
        }

        // Handle Object
        if (obj instanceof Object) {
            copy = {};
            for (var attr in obj) {
                if (attr != 'parentDataset')
                    if (obj.hasOwnProperty(attr))
                        copy[attr] = clone(obj[attr]);
            }
            return copy;
        }

        throw new Error("Unable to copy obj! Its type isn't supported.");
    }
    $scope.processForm = function(a) {
        $scope.progressBarWidth = 30;

        console.log($scope.object.dataSets[0]);
        copiedDatasetFinal = clone($scope.object);

        $http({
            method: 'POST',
            url: 'genRdf.php',
            data: $.param(copiedDatasetFinal), // pass in data as strings
            //          data: $.param($scope.object), // pass in data as strings;
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
        })
                .success(function(data) {
                    if (!$scope.showValidation)
                        $scope.progressBarWidth = 0;
                    else
                        $scope.progressBarWidth = 70;
                    $scope.rdf = data;
                    $("#rdfOutput").show();
                    $scope.spanError = "";
                    $("#rdfOutput").dialog({maxHeight: 550, width: 700, zIndex: 500, title: "DataID RDF", position: ['center', 20]});
                    if ($scope.showValidation) {
                        send = new Object();
                        send.s = data;
                        send.t = 'text';
                        send.i = 'turtle';
                        send.csurl = 'http://localhost:8787/validate';

                        $http({
                            method: 'POST',
                            url: 'proxy.php',
                            data: $.param(send),
                            headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
                        })
                            .success(function(data) {
                                $scope.progressBarWidth = 0;
                                var bodyhtml = data.split('<body>').pop().split('</body>')[0];
                                $("#rdfOutputValidatorHTML").html(bodyhtml);
                                $("#rdfOutputValidator").dialog({maxHeight: 550, width: 900, title: "DataID Validator"});

                            });
                    }


                })
                .error(function(data, status, headers, config) {
                    console.log("Error using validator");
                    $scope.progressBarWidth = 0;
                    $scope.spanError = "DataID file generation was failed.";
                });
        ;

    }

    $scope.editDist = function(dist) {
        console.log(dist);
        var modalInstance = $modal.open({
            templateUrl: 'modalDistContent.html',
            controller: ModalEditDistInstanceCtrl,
            size: 'lg',
            resolve: {
                dist: function() {
                    return {dist: dist, messages: $scope.messages, license: $scope.license};
                }
            }
        });
        modalInstance.result.then(function(dist) {
        }, function() {
            console.log('Modal dismissed at: ' + new Date());
        });

    };
    $scope.editAgent = function(agent) {
        var modalInstance = $modal.open({
            templateUrl: 'modalAgentContent.html',
            controller: ModalEditAgentInstanceCtrl,
            size: 'md',
            resolve: {
                agent: function() {
                    return {agent: agent, messages: $scope.messages};
                }
            }
        });
    };
    $scope.editWasDerivedFromAgent = function(wasDerivedFromAgent) {
        var modalInstance = $modal.open({
            templateUrl: 'modalWasDerivedFromAgentContent.html',
            controller: ModalEditWasDerivedFromAgentInstanceCtrl,
            size: 'md',
            resolve: {
                wasDerivedFromAgent: function() {
                    return {wasDerivedFromAgent: wasDerivedFromAgent, messages: $scope.messages};
                }
            }
        });
    };
    $scope.editWasGeneratedByAgent = function(wasGeneratedByAgent) {
        var modalInstance = $modal.open({
            templateUrl: 'modalWasGeneratedByAgentContent.html',
            controller: ModalEditWasGeneratedByAgentInstanceCtrl,
            size: 'md',
            resolve: {
                wasGeneratedByAgent: function() {
                    return {wasGeneratedByAgent: wasGeneratedByAgent, messages: $scope.messages};
                }
            }
        });
    };
    $scope.parseExternalDistributions = function(a) {
        $scope.progressBarWidth = 30;


        b = new Object();
        b.address = a.address;
        b.mediaType = a.mediaType;
        b.description = a.description;
        
        console.log(b.description+"!");

        $http({
            method: 'POST',
            url: 'parser/parser.php',
            data: $.param(b), // pass in data as strings
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
        })
                .success(function(data) {
                    $scope.progressBarWidth = 70;
                    for (i in data) {
                        $scope.ds.distribution.push({
                            'accessUrl': b.address + data[i].name,
                            'title': data[i].name,
                            'mediaType': b.mediaType,
                            'description': b.description,
                            'issued': data[i].date,
                            'format': data[i].name.split(".").pop(-1),
//                            'description': data[i].description,
                            'prop': 'dcat:distribution'});

                    }

                    $scope.progressBarWidth = 0;

                }).error(function(data, status, headers, config) {
                    console.log("Error trying to parse webpage");
                    $scope.progressBarWidth = 0;
                    $scope.spanError = "Parsing webpage failed.";
                });;

    }

    $scope.editLinkset = function(linkset) {
        console.log(linkset);
        var modalInstance = $modal.open({
            templateUrl: 'modalLinksetContent.html',
            controller: ModalEditLinksetInstanceCtrl,
            size: 'md',
            resolve: {
                linkset: function() {
                    return {linkset: linkset, messages: $scope.messages};
                }
            }
        });
        modalInstance.result.then(function(linkset) {
        }, function() {
            console.log('Modal dismissed at: ' + new Date());
        });

    };


    $scope.open = function(size) {
        var modalInstance = $modal.open({
            templateUrl: 'modalContent.html',
            controller: ModalInstanceCtrl,
            size: size,
            resolve: {
                subset: function() {
                    return {subset: {issued: dt, modified: dt}, messages: $scope.messages};
                }
            }
        });
        modalInstance.result.then(function(subset) {
            $scope.ds.subset.push(subset);
        }, function() {
            console.log('Modal dismissed at: ' + new Date());
        });

    };
    $scope.openUploadDataHub = function(size) {
        var modalInstance = $modal.open({
            templateUrl: 'modalUploadDataHub.html',
            controller: ModalUploadDataHubCtrl,
            size: size,
            resolve: {
                dataHubObj: function() {
                    return {};
                }
            }
        });
        modalInstance.result.then(function(dataHubObj) {
            console.log("DataHub Post Here");
        }, function() {
            console.log('Modal dismissed at: ' + new Date());
        });

    };

    $scope.openDist = function(size) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalDistContent.html',
            controller: ModalDistInstanceCtrl,
            size: size,
            resolve: {
                dist: function() {
                    return {dist: {prop: 'void:sparqlEndpoint', title: ''}, messages: $scope.messages, license: $scope.license};
                }
            }
        });
        modalDistInstance.result.then(function(dist) {
            if (dist.title != '')
                $scope.ds.distribution.push(dist);
        });

    };
    $scope.openDistWeb = function(size) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalDistWebContent.html',
            controller: ModalDistWebInstanceCtrl,
            size: size,
            resolve: {
                parseAddr: function() {
                    return {address: ''};
                }
            }
        });
        modalDistInstance.result.then(function(a) {
            if (a.address != '')
                $scope.parseExternalDistributions(a);
        });

    };
    $scope.openAgent = function(size) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalAgentContent.html',
            controller: ModalAgentInstanceCtrl,
            size: size,
            resolve: {
                agent: function() {
                    return {agent: {prop: 'dcat:contactPoint', resource: '', name: '', mbox: '', label: ''}, messages: $scope.messages};
                }
            }
        });
        modalDistInstance.result.then(function(agent) {
            if (agent.resource != '' || agent.name != '' || agent.mbox != '' || agent.label != '')
                $scope.ds.agent.push(agent);

        });
    };
    $scope.openWasDerivedFromAgent = function(size) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalWasDerivedFromAgentContent.html',
            controller: ModalWasDerivedFromAgentInstanceCtrl,
            size: size,
            resolve: {
                wasDerivedFromAgent: function() {
                    return {wasDerivedFromAgent: {prop: 'dct:creator', resource: '', name: '', mbox: '', label: ''}, messages: $scope.messages};
                }
            }
        });
        modalDistInstance.result.then(function(wasDerivedFromAgent) {
            if (wasDerivedFromAgent.resource != '' || wasDerivedFromAgent.name != '' || wasDerivedFromAgent.mbox != '' || wasDerivedFromAgent.label != '')
                $scope.ds.wasDerivedFromAgent.push(wasDerivedFromAgent);
        });
    };
    $scope.openWasGeneratedByAgent = function(size) {
        var modalDistInstance = $modal.open({
            templateUrl: 'modalWasGeneratedByAgentContent.html',
            controller: ModalWasGeneratedByAgentInstanceCtrl,
            size: size,
            resolve: {
                wasGeneratedByAgent: function() {
                    return {wasGeneratedByAgent: {prop: 'dct:creator', resource: '', name: '', mbox: '', label: ''}, messages: $scope.messages};
                }
            }
        });
        modalDistInstance.result.then(function(wasGeneratedByAgent) {
            if (wasGeneratedByAgent.resource != '' || wasGeneratedByAgent.name != '' || wasGeneratedByAgent.mbox != '' || wasGeneratedByAgent.label != '')
                $scope.ds.wasGeneratedByAgent.push(wasGeneratedByAgent);
        });
    };
    $scope.openLinkset = function(size) {
        var modalInstance = $modal.open({
            templateUrl: 'modalLinksetContent.html',
            controller: ModalLinksetInstanceCtrl,
            size: size,
            resolve: {
                linkset: function() {
                    return {linkset: {issued: dt, modified: dt, prop: 'void:subset'}, messages: $scope.messages};
                }
            }
        });
        modalInstance.result.then(function(linkset) {
            $scope.ds.linkset.push(linkset);
        }, function() {
            console.log('Modal dismissed at: ' + new Date());
        });

    };

    $scope.downloadInnerHtml = function() {
        fileName = 'dataId.ttl'; // You can use the .txt extension if you want
        var elHtml = document.getElementById('rdf').textContent;
        var link = document.createElement('a');
        mimeType = 'text/plain';

        link.setAttribute('download', fileName);
        link.setAttribute('href', 'data:' + mimeType + ';charset=utf-8,' + encodeURIComponent(elHtml));
        link.click();
    }


}


var ModalInstanceCtrl = function($scope, $modalInstance, subset) {
    $scope.subset = subset.subset;
    $scope.messages = subset.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.subset);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalUploadDataHubCtrl = function($scope, $modalInstance, dataHubObj) {
    $scope.dataHubObj = dataHubObj;
    $scope.ok = function() {
        $modalInstance.close($scope.dataHubObj);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};

var ModalEditInstanceCtrl = function($scope, $modalInstance, subset) {
    $scope.subset = subset.subset;
    $scope.messages = subset.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.subset);
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};

var ModalEditDistInstanceCtrl = function($scope, $modalInstance, dist) {
    $scope.distribution = dist.dist;
    $scope.messages = dist.messages;
    $scope.license = dist.license;
    $scope.ok = function() {
        $modalInstance.close($scope.distribution);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalEditAgentInstanceCtrl = function($scope, $modalInstance, agent) {
    $scope.agent = agent.agent;
    $scope.messages = agent.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.agent);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalEditWasDerivedFromAgentInstanceCtrl = function($scope, $modalInstance, wasDerivedFromAgent) {
    $scope.wasDerivedFromAgent = wasDerivedFromAgent.wasDerivedFromAgent;
    $scope.messages = wasDerivedFromAgent.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.wasDerivedFromAgent);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalEditwasGeneratedByAgentInstanceCtrl = function($scope, $modalInstance, wasGeneratedByAgent) {
    $scope.wasGeneratedByAgent = wasGeneratedByAgent.wasGeneratedByAgent;
    $scope.messages = wasGeneratedByAgent.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.wasGeneratedByAgent);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalDistInstanceCtrl = function($scope, $modalInstance, dist) {
    $scope.distribution = dist.dist;
    $scope.messages = dist.messages;
    $scope.license = dist.license;
    $scope.ok = function() {
        $modalInstance.close($scope.distribution);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalDistWebInstanceCtrl = function($scope, $modalInstance, parseAddr) {

    $scope.address2 = parseAddr.address;
    $scope.ok = function(addr, format, description) {
        console.log(addr + format);
        a = new Object();
        a.address = addr;
        a.mediaType = format;
        a.description = description;
        $modalInstance.close(a);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalAgentInstanceCtrl = function($scope, $modalInstance, agent) {
    $scope.agent = agent.agent;
    $scope.messages = agent.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.agent);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalWasDerivedFromAgentInstanceCtrl = function($scope, $modalInstance, wasDerivedFromAgent) {
    $scope.wasDerivedFromAgent = wasDerivedFromAgent.wasDerivedFromAgent;
    $scope.messages = wasDerivedFromAgent.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.wasDerivedFromAgent);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalWasGeneratedByAgentInstanceCtrl = function($scope, $modalInstance, wasGeneratedByAgent) {
    $scope.wasGeneratedByAgent = wasGeneratedByAgent.wasGeneratedByAgent;
    $scope.messages = wasGeneratedByAgent.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.wasGeneratedByAgent);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};

var ModalEditLinksetInstanceCtrl = function($scope, $modalInstance, linkset) {
    $scope.linkset = linkset.linkset;
    $scope.messages = linkset.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.linkset);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};
var ModalLinksetInstanceCtrl = function($scope, $modalInstance, linkset) {
    $scope.linkset = linkset.linkset;
    $scope.messages = linkset.messages;
    $scope.ok = function() {
        $modalInstance.close($scope.linkset);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
};

var INTEGER_REGEXP = /^\-?\d+$/;
var URL_REGEXP = new RegExp(
        "^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");

dataIdGen.directive('url', function() {
    return {
        require: 'ngModel',
        link: function(scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                if (URL_REGEXP.test(viewValue)) {
                    // it is valid
                    ctrl.$setValidity('url', true);
                    console.log('valid url');
                    return viewValue;
                } else {
                    // it is invalid, return undefined (no model update)
                    ctrl.$setValidity('url', false);
                    console.log('!valid url');
                    return undefined;
                }
            });
        }
    };
});



