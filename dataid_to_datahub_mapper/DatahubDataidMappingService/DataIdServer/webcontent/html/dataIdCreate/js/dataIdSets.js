/**
 * Created by Chile on 9/12/2015.
 */

function getPopoverContext()
{
    return {
        dynamicPopover : {
            id: '',
            invalid: false,
            templateUrl: 'popover-version.html',
            message: 'default message',
            open: true,
            delay: 0,
            title: 'Title'
        },
        agentPopover : {
            title: 'Options',
            templateUrl: 'popover-agent-menu.html',
            agentId : ''
        },
        agentRolePopover : {
            title: 'Options',
            templateUrl: 'popover-agentrole-menu.html',
            agentId : ''
        },
        datasetPopover : {
            title: 'Dataset Options',
            templateUrl: 'popover-dataset-menu.html',
            datasetId : ''
        },
        distrPopover : {
            title: 'Options',
            templateUrl: 'popover-distr-menu.html',
            distrId : ''
        },
        dataidPopover : {
            title: 'Options',
            templateUrl: 'popover-dataid-menu.html',
            id : ''
        },
        dtaIdUriPopover : {
            invalid: false,
            open: true,
            delay:{false: "1000", true: "0"},
            title: {false: "DataID URI", true:"Uri is not valid"},
            templateUrl: {false: 'popover-note.html', true: 'popover-warning.html'},
            message:{false: "This is the most important entry. Please use an unique URI starting with 'http://' as an Id. It is recommended to choose a domain under your supervision (e.g. your organization domain name). Otherwise, please use dbpedia.dataid.org as domain.",
                    true: "This has to be a valid uri, also a DataID uri should not contain a '#' as fragments are reserved for dependent objects and versions."},
            id : ''
        },
        confirmPopover : {
            title: 'Please confirm',
            templateUrl: 'popover-confirmation.html',
            functionName: 'alert',
            stringParam: 'no function specified'
        },
        warningPopover : {
            title: {multiselectValidatior: 'No option selected',
                default:'Warning',
                required:'Required!',
                number:'No Number',
                pattern:'Entry not valid.',
                email:'No valid Email Address',
                uri:'No valid uri',
                parturi: 'No valid uri',
                dataiduri: 'No valid DataID uri',
                uniqueuri: 'No unique uri',
                nolicense: 'No license selcted',
                integer:'No valid integer'},
            open: true,
            error: null,
            templateUrl: 'popover-warning.html',
            priority:{
                nolicense: 2,
                dataiduri: 1
            },
            message: {multiselectValidatior: 'Select at least one option.',
                default:'warning',
                required:'This field is required.',
                number:'Please enter a number.',
                pattern:'The entered characters ar not in the expected pattern.',
                email:'Please enter a valid email address.',
                uri:'Please enter a valid uri.',
                parturi:'Please enter a valid uri.',
                dataiduri: 'Please enter a valid uri containing no \'#\'.',
                uniqueuri: 'Please choose another uri. This one is already in use.',
                nolicense: 'Please select a license first.',
                integer:'Please state the amount as an integer value.'}
        },
        notePopover : {
            title: 'Note',
            open: true,
            templateUrl: 'popover-warning.html',
            message: 'some warning'
        }
    };
}

function getContext()
{
    var c = {
        "dataid": "http://dataid.dbpedia.org/ns/core#",
        "dc": "http://purl.org/dc/terms/",
        "dcat": "http://www.w3.org/ns/dcat#",
        "foaf": "http://xmlns.com/foaf/0.1/",
        "owl": "http://www.w3.org/2002/07/owl#",
        "prov": "http://www.w3.org/ns/prov#",
        "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
        "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
        "sd": "http://www.w3.org/ns/sparql-service-description#",
        "void": "http://rdfs.org/ns/void#",
        "rut" : "http://rdfunit.aksw.org/ns/core#",
        "xsd": "http://www.w3.org/2001/XMLSchema#"
    };
/*    var dataid = getLdToAngularMap(getEmptyDataId('dzdzttt'));
    angular.extend(c, dataid);
    var dataset = getLdToAngularMap(getEmptyDataset('zzzzg', dataid['@id']));
    angular.extend(c, dataset);
    var dist = getLdToAngularMap(getEmptyDistribution('ftdffhghj', dataset['@id'], ['dataid:Distribution']));
    angular.extend(c, dist);
    var agent = getLdToAngularMap(getEmptyAgent('ddfgrrrs', ['dataid:Creator']));
    angular.extend(c, agent);*/
    return c;
}

function getEmptyDataId(id) {
    var dataid = {
        "@graph": [{
            "@id": id,
            "@type": ["void:DatasetDescription"],
            "dataid:associatedAgent": {
                "@required": true,
                "@label":"primary agent which is responsible for this DataID",
                "@type":["dataid:Agent"],
                "@value": []
            },
            "foaf:topic": {
                "@required": true,
                "@label":"relates a DataID to a dataset that the DataID is about.",
                "@type":["dataid:Dataset"],
                "@value": []
            },
            "dataid:previousDataIdVersion": {
                "@required": false,
                "@label":"previous version of this DataId (version published before this version)",
                "@id": null,
                "@type": ["dataid:Dataset"]
            },
            "dataid:nextDataIdVersion": {
                "@required": false,
                "@label":"Next version of this DataId (version published after this version)",
                "@id": null,
                "@type": ["dataid:Dataset"]
            },
            "dataid:latestDataIdVersion": {
                "@required": false,
                "@label":"The latest version of this DataId",
                "@id": null,
                "@type":["dataid:Dataset"]
            },
            "dc:description": {
                "@required": false,
                "@label":"a useful description",
                "@language": null,
                "@value": {}
            },
            "dc:issued": {
                "@required": false,
                "@label":"",
                "@type": ["xsd:date"],
                "@value": null
            },
            "dc:modified": {
                "@required": false,
                "@label":"",
                "@type": ["xsd:date"],
                "@value": null
            },
            "rdfs:label": {
                "@required": false,
                "@label":"a label for this instance (recommended)",
                "@language": null,
                "@value": {}
            },
            "dc:title": {
                "@required": true,
                "@label":"Name of this DataID",
                "@type": ["xsd:string"],
                "@value": "new DataID"
            }
        }],
        "@context": {
            "dataid": "http://dataid.dbpedia.org/ns/core#",
            "dc": "http://purl.org/dc/terms/",
            "dcat": "http://www.w3.org/ns/dcat#",
            "foaf": "http://xmlns.com/foaf/0.1/",
            "owl": "http://www.w3.org/2002/07/owl#",
            "prov": "http://www.w3.org/ns/prov#",
            "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
            "sd": "http://www.w3.org/ns/sparql-service-description#",
            "void": "http://rdfs.org/ns/void#",
            "xsd": "http://www.w3.org/2001/XMLSchema#"
        }
    };
    return dataid;
}

function getEmptyDataset(id, parentId) {
    var dataset = {
        "@id": id,
        "@type": ["dataid:Dataset"],
        "@parent": parentId,
        "@pristine": true,
        "dataid:associatedAgent": {
            "@required": true,
            "@label":"some agent generally connected to the dataset",
            "@type":["dataid:Agent"],
            "@value": []
        },
        "dc:description": {
            "@required": false,
            "@label":"a useful description",
            "@language": null,
            "@value": {}
        },
        "dc:issued": {
            "@required": false,
            "@label":"",
            "@type": ["xsd:date"],
            "@value": null
        },
        "dc:language": {
            "@required": false,
            "@label":"A language of the resource.",
            "@type": ["xsd:string"],
            "@value": null
        },
        "dataid:licenseName": {
            "@required": false,
            "@label":"Name of the license of the dataset as plain string",
            "@type":["xsd:string"],
            "@value":null
        },
        "dc:license":{
            "@required": true,
            "@label":"A legal document giving official permission to do something with the resource.",
            "@type":["dc:LicenseDocument"],
            "@id": null
        },
        "dc:rights": {
            "@required": false,
            "@label":"Information about rights held in and over the resource.",
            "@language": null,
            "@value": {}
        },
        "dc:modified": {
            "@required": false,
            "@label":"",
            "@type": ["xsd:date"],
            "@value": null
        },
        "void:entities":{
            "@required": false,
            "@label":"The total number of entities that are described in a void:Dataset.",
            "@type": ["xsd:integer"],
            "@value": null
        },
        "void:classes":{
            "@required": false,
            "@label":"The total number of distinct classes in a void:Dataset. In other words, the number of distinct resources occuring as objects of rdf:type triples in the dataset.",
            "@type": ["xsd:integer"],
            "@value": null
        },
        "void:distinctObjects":{
            "@required": false,
            "@label":"The total number of distinct objects in a void:Dataset. In other words, the number of distinct resources that occur in the object position of triples in the dataset. Literals are included in this count.",
            "@type": ["xsd:integer"],
            "@value": null
        },
        "dc:accrualPeriodicity":{
            "@required": false,
            "@label":"The frequency with which items are added to a collection.",
            "@type": ["dc:Frequency"],
            "@id": null
        },
        "dc:title": {
            "@required": true,
            "@label":"Name of this dataset",
            "@type": ["xsd:string"],
            "@value": 'a new dataset'
        },
        "void:exampleResource": {
            "@required": false,
            "@label":"example resource of this dataset",
            "@type":["rdfs:Resource"],
            "@value": []
        },
        "void:rootResource": {
            "@required": false,
            "@label":"A top concept or entry point for a void:Dataset that is structured in a tree-like fashion. All resources in a dataset can be reached by following links from its root resources in a small number of steps.",
            "@id": null
        },
        "void:sparqlEndpoint": {
            "@required": false,
            "@label":"A SPARQL endpoint offering the data of this dataset",
            "@type":["dataid:SparqlEndpoint"],
            "@value": []
        },
        "void:triples": {
            "@required": false,
            "@label":"number of triples contained in this dataset",
            "@type": ["xsd:integer"],
            "@value": null
        },
        "rdfs:label": {
            "@required": false,
            "@label":"a label for this instance (recommended)",
            "@language": null,
            "@value": {}
        },
        "dcat:distribution": {
            "@required": true,
            "@label":"Represents a specific available form of a dataset. Each dataset might be available in different forms, these forms might represent different formats of the dataset or different endpoints. Examples of distributions include a downloadable CSV file, an API or an RSS feed",
            "@type":["dataid:Distribution"],
            "@value": []
        },
        "dcat:keyword": {
            "@required": true,
            "@label":"A keyword or tag describing the dataset.",
            "@type":["xsd:string"],
            "@value": []
        },
        "void:subset": {
            "@required": false,
            "@label":"A child dataset",
            "@type":["dataid:Dataset"],
            "@value": []
        },
        "dcat:landingPage": {
            "@required": true,
            "@label":"A Web page that can be navigated to in a Web browser to gain access to the dataset, its distributions and/or additional information.",
            "@id": null
        },
        "dataid:previousDatasetVersion": {
            "@required": false,
            "@label":"previous version of this dataset (version published before this version)",
            "@id": null,
            "@type": ["dataid:Dataset"]
        },
        "dataid:nextDatasetVersion": {
            "@required": false,
            "@label":"Next version of this dataset (version published after this version)",
            "@id": null,
            "@type": ["dataid:Dataset"]
        },
        "dataid:latestDatasetVersion": {
            "@required": false,
            "@label":"The latest version of this dataset",
            "@id": null,
            "@type":["dataid:Dataset"]
        },
        "dataid:containsLinks":{
            "@required": false,
            "@label":"A set of links to another dataset contained in this dataset",
            "@id":null,
            "@type":["dataid:Linkset"]
        }
    };
    return dataset;
}

function getEmptyDistribution(id, parentId, types)
{
    var distr = {
        "@id": id,
        "@type": [].concat(types),
        "@parent": parentId,
        "@pristine": true,
        "dc:description": {
            "@required": false,
            "@label":"a useful description",
            "@language": null,
            "@value": {}
        },
        "dc:issued": {
            "@required": false,
            "@label":"",
            "@type": ["xsd:date"],
            "@value": null
        },
        "dc:modified": {
            "@required": false,
            "@label":"",
            "@type": ["xsd:date"],
            "@value": null
        },
        "dataid:licenseName": {
            "@required": false,
            "@label":"Name of the license of the dataset as plain string",
            "@type":["xsd:string"],
            "@value":null
        },
        "dc:license":{
            "@required": true,
            "@label":"A legal document giving official permission to do something with the resource.",
            "@type":["dc:LicenseDocument"],
            "@id": null
        },
        "dc:rights": {
            "@required": false,
            "@label":"Information about rights held in and over the resource.",
            "@language": null,
            "@value": {}
        },
        "dc:format": {
            "@required": true,
            "@label":"The file format, physical medium, or dimensions of the resource. (as MIME Type)",
            "@type": ["xsd:string"],
            "@value": null
        },
        "dc:title":{
            "@required": true,
            "@label":"Name of this distribution",
            "@type": ["xsd:string"],
            "@value": 'a new distribution'
        },
        "rdfs:label": {
            "@required": false,
            "@label":"a label for this instance (recommended)",
            "@language": null,
            "@value": {}
        },
        "dcat:downloadURL": {
            "@required": true,
            "@label":"A file that contains the distribution of the dataset in a given format",
            "@id": null
        },
        "dcat:accessURL": {
            "@required": true,
            "@label":"A landing page, feed, SPARQL endpoint or other type of resource that gives access to the distribution of the dataset",
            "@id": null
        },
        "dcat:mediaType":{
            "@required": false,
            "@label":"The media type of the distribution as defined by IANA.",
            "@type": ["xsd:string"],
            "@value": null
        },
        "dcat:byteSize":{
            "@required": false,
            "@label":"size of the associated files in bytes",
            "@type" : ["xsd:integer"],
            "@value": null
        },
        "dataid:md5Hash":{
            "@required": false,
            "@label":"md5 hash",
            "@type" : ["dataid:md5"],
            "@value": null
        },
        "dataid:checksum":{
            "@required": false,
            "@label":"Checksum of a file to check for correctness",
            "@type": ["dataid:crc32", "dataid:md5"],
            "@value": null
        },
        "dataid:graphName":{
            "@required": false,
            "@label":"The name of the graph of this distribution",
            "@type":["xsd:string"],
            "@value": null
        },
        "dataid:associatedAgent": {
            "@required": false,
            "@label":"some agent generally connected to the dataset",
            "@type":["dataid:Agent"],
            "@value": []
        }
    };
    return distr;
}

function getEmptyLinkset(id)
{
    var linkset = {
        "@id": id,
        "@type": ["dataid:Linkset"],
        "@pristine": true,
        "void:objectsTarget":{
            "@required": true,
            "@label":"object dataset of links",
            "@id":null
        },
        "void:subjectsTarget":{
            "@required": true,
            "@label":"subject dataset of links",
            "@id":null
        },
        "void:triples":{
            "@required": true,
            "@label":"the number of triples contained in this linkset (number of links between two datasets)",
            "@type":["xsd:integer"],
            "@value":null
        },
        "dataid:previousLinksetVersion":{
            "@required": false,
            "@label":"previous version of a linkstet (the one published before this version)",
            "@id":null
        },
        "dataid:latestLinksetVersion":{
            "@required": false,
            "@label":"latest version of this linkset",
            "@id":null
        },
        "dataid:nextLinksetVersion":{
            "@required": false,
            "@label":"next version of this linkset (the one published after this version)",
            "@id":null
        },
        "dc:issued": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        },
        "dc:modified": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        }
    };
    return linkset;
}

function getEmptyAgent(id, type){
    var agent = {
        "@id": id,
        "@type": ["dataid:Agent"].concat(type),
        "@pristine": true,
        "dataid:agentMail": {
            "@required": true,
            "@label":"The e-mail address of an associated agent",
            "@type":["xsd:string"],
            "@value": null
        },
        "rdfs:label": {
            "@required": false,
            "@label":"a label for this instance (recommended)",
            "@language": null,
            "@value": {}
        },
        "dataid:agentName": {
            "@required": true,
            "@label":"The name of an associated agent",
            "@type":["xsd:string"],
            "@value": "A name"
        },
        "dataid:agentUrl" : {
            "@required": false,
            "@label":"landing page associated with an agent",
            "@type":["xsd:string"],
            "@value": null
        },
        "dataid:agentRole" : {
            "@required": false,
            "@label":"the role this agent occupies",
            "@type":["xsd:string"],
            "@value": type.length > 0 ? type[0].substring(type[0].indexOf(':')+1) : 'Agent'
        }
    }
    return agent;
}