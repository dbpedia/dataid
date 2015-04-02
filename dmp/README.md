Data Management Plan generation
======

Tool to generate EU H2020 Data Management Plans from DataID files. Use the contained dataid.ttl as template for your dataset, then add its location to the aligned_dataindex.ttl. 

Run the conversion skript using python3 generate_dmp.py -i aligned_dataindex.ttl

The skript is relatively forgiving for missing metadata. Mandatory properties are:
* dc:title - Long name of the dataset
* rdfs:label - Short name of the dataset
* dcat:landingPage - Homepage of the dataset
* dataid:associatedAgent - At least one agent responsible for the dataset, including name and email
* dc:description - A textual description of the dataset

Textual descriptions for DMP have to be added as well:
* dmp:usefulness - to whom it could be useful, and whether it underpins a scientific publication
* dmp:similarData - Information on the existence (or not) of similar data
* dmp:reuseAndIntegration - Information on the possibilities for integration and reuse
* dmp:additionalSoftware - necessary software and other tools for enabling re-use
* dmp:repositoryUrl - URL of a repository the data is filed in
* dmp:growth - Information on growth and what is its approximated end volume
* dmp:archiveLink - Link to a data archive
* dmp:preservation - Indication of how long the data should be preserved what the associated costs are and how these are planned to be covered
* dmp:openness - Description of how data will be shared, including access procedures, embargo periods (if any); definition of whether access will be widely open or restricted to specific groups. In case the dataset cannot be shared, the reasons for this should be mentioned (e.g. ethical, rules of personal data, intellectual property, commercial, privacy-related, security-related)
* dmp:licenseName - Name of the license of the dataset
