DataId To DataHub publisher
###########################

Instructions (internal documentation)

This is REST Service deployed as Maven-Project to push DataId informations as datasets an DataHub.
GitHub: <insert>

1. The Json Service Interface
#############################

The two main functions to use are CreateDataset and/or UpdateDataset.
UpdataDataset is only to be called after creating a Dataset with the same agentName beforehand.

To call either of those functions with a dataId use a HTTP-POST with the following settings:

a) Both functions require the same parameters:

1. apikey : 	To use the REST-Interface of Datahub.io this key is required in any transaction.
				It can be found at your personal page on Datahub (left-bottom corner).
2. organization: To publish or change datasets a registered user has also be part of an organization listed on Datahub.io.
				(in our case this would be 'aksw')
3. datasetname:	The (internal) agentName of the dataset to be created or updated. (the unique string used to identify a dataset
				in a DataHub-URL)
4. isprivate:	If 'true', his optional parameter sets the dataset as private. (Use this to play around, debug or keep your data secret :)


An acceptable request URL would look like this:
http://<host>:<port>/dataid/datahubpublisher/createdataset?apikey=1395b749-5ecc-4197-9ba6-7e8d1afdbb67&organization=aksw&datasetname=linkedspending&isprivate=true

b) As data enter the whole dataId (including prefixes).

2. Hosting the DataHub-publisher
################################

<insert some stuff about publishing the service>

Two config-files are essential for using this service.
Both are valid json documents. 

a) MainConfig.json 

This file provides basic informations needed to interact with the CKAN-REST-Service of DataHub.io.

1. datahubActionUri : If not altered by DataHub.io, this should be: "http://datahub.io/api/3/action/".
2. mappingConfigPath: The relative path or agentUrl to find the MappingConfig.json, needed to translate DataIds to DataHub datasets.
3. ckanTimeOut      : The timeout in ms when communication with the CKAN-REST-Service of DataHub.io.
4. adminName        : Used as username to provide authority when editing the MappingConfig.json via the http server.
5. password         : The password for editing the MappingConfig.json.
6. ckanActionMap	: This map points a function of the DataId To DataHub publisher, to use the named function of the CKAN-REST-Service.
					  (should not be altered unless the CKAN-API changes)
					  
b) MappingConfig.json

Provides all informations needed to map a DataId to a DataHub dataset. (see 3.)	
The MappingConfig.json file is a JsonLD <insert agentUrl> document to provide context information similar to turtle syntax.
Under the address http://<host>:<port>/dataid/datahubpublisher/getmappings this file can be edited with the additional
entry of username and password defined in the MainConfig.json


3. Mapping DataId to DataHub datasets.
######################################

To accommodate any change on either side of this mapping (Ckan dataset properties or DataId ontology),
the mapping information is stored as a JsonLD-document which is easy to read and provides the ability to use context-prefixes.
Like everey JsonLD document this file has a @context object providing the contextual informations and prefixes, and a @graph object
providing the actual mapping informations.
The @graph-object in this file provides basic informations about the author, version and the date this mapping was published.
The two most important concepts of DataId are dataset and distribution (equivalent to 'resource' on DataHub-Ckan-Api).
The dataHubMapping-object consist therefore of two objects representing both concepts (dataset and resource). 
Other concepts of DataId are referenced through reference chains (see below).

A Datahub dataset has different types of properties which have to be addressed here. 

a) simple, common properties

Properties which are part of the standard dataset-type of the Ckan API and can be expressed as an xsd-datatype.
(e.g. 'version' - @type=xsd:string, 'metadata_created' - @type=xsd:date)
These properties are common to every dataset and are displayed in a fixed position on the html page of the dataset.

Mapping example:

	 "notes": {												-> the property-agentName is also the agentName used in the DataHub-Ckan-Api and has to be unique
	   "dataIdRef": "dc:description",						-> referenced property of DataId (may consist of multiple entries divided by a comma)
																(depending on the object to be mapped (dataset or resource as subjects) , 
																this has to be a direct predicate pointing to an object of the type @type!)
	   "@type": "xsd:string",								-> provides data of this type
	   "comment": "text description of the dataset",			-> additional comment concerning this mapping
	   "addedBy": "someone",								-> author of this mapping
	   "issued": "2014-04-16T09:26:29.603416"				-> date this mapping was written
	  }
This example maps the property 'dc:description' of a DataId dataset to the 'notes' property of a DataHub dataset.



b) additional key-value pairs

These properties are listed as additional information pertaining to the dataset on DataHub.io.
(displayed as a list)

	 "language": {											-> this property is not part of the dataset-type in the DataHub-Ckan-Api
	   "dataIdRef": "dc:language",
	   "@type": "xsd:string",								-> only string is allowed as type
	   "comment": "some mapping comment",
	   "addedBy": "someone",
	   "issued": "2014-04-16T09:26:29.603416",
	   "additionalKey": true								-> has to be true of property does not exist in the Ckan-Api and 
	 },

	 
	 
c) lists

Properties which are lists of other objects in the standard dataset-type of the Ckan API.

	 "resources": {
	   "dataIdRef": "dcat:distribution, void:sparqlEndpoint",
	   "@type": "dcat:distribution",
	   "comment": "Links a dataset to its available distributions.",
	   "addedBy": "someone",
	   "issued": "2014-04-16T09:26:29.603416",
	   "isList": true										-> has to be true if list (may be depreciable)
	  },
	 


	 

There are additional possibilities for complex mapping scenarios:


d) reference chains
If a property of the DataHub-Ckan API can not be mapped to a direct predicate of the pertaining concept in the DataId ontology, but needs to be referenced
to a whole path of predicates, a reference chain (similar to a property path in SPARQL) can be provided. A reference chain begins always at a dataset or distribution.

	 "author_email": {
	   "@type": "xsd:string",
	   "referenceChain": ["dc:publisher", "foaf:mbox"],		-> a list of connected properties, where the last entry represents the wanted information
	   "comment": "some comment...",
	   "addedBy": "someone",
	   "issued": "2014-04-16T09:26:29.603416"
	  },
	  
The information about the email of the author is to be found under the mbox property of the agent 'dc:publisher' (see DataId ontology)



e) alternative mappings
In some cases a DataHub dataset property can be mapped to different possible properties in the DataId ontology.
For these scenarios alternative mappings can be provided. Important is the order of in which the DataHub publisher is looking for an information.

	 "author": {
	   "@type": "xsd:string",
	   "referenceChain": ["dc:publisher", "rdfs:label"],
	   "comment": "some mapping comment",
	   "addedBy": "someone",
	   "issued": "2014-04-16T09:26:29.603416",
	   "hasAlternative":"alt_pub1"							-> if the publisher can not find any information provided via the given mapping object, it will
																try to use a second mapping object, if provided by the 'hasAlternative' property
	  },
	  "alt_pub1": {											-> is not part of the dataset-type in the DataHub-Ckan-Api!
	   "@type": "xsd:string",								-> types should be the same
	   "referenceChain": ["dc:publisher", "@id"],			-> a different reference chain is provided (@id means that the uri of the agent 'dc:publisher' is to be used as information)
	   "comment": "some mapping comment",
	   "addedBy": "someone",
	   "issued": "2014-04-16T09:26:29.603416",
	   "isAlternative":true									-> signals alternative status
	  },
	  
The mapping object 'alt_pub1' could be provided with "hasAlternative":"alt_pub2" to provide the next alternative, if no information could be discerned yet.